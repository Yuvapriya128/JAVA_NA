package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.OrderRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderUpdateDTO;
import org.example.springdatajpademo.Ecommerce.exceptions.CustomerNotFound;
import org.example.springdatajpademo.Ecommerce.exceptions.OrderNotFound;
import org.example.springdatajpademo.Ecommerce.model.*;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.example.springdatajpademo.Ecommerce.repository.OrderItemRepo;
import org.example.springdatajpademo.Ecommerce.repository.OrderRepo;
import org.example.springdatajpademo.Ecommerce.repository.ProductRepo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final CustomerRepo customerRepo;
    private final ProductRepo productRepo;
    private final OrderItemRepo orderItemRepo;

    public OrderServiceImpl(OrderRepo orderRepo,
                            CustomerRepo customerRepo,
                            ProductRepo productRepo,
                            OrderItemRepo orderItemRepo) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
        this.orderItemRepo = orderItemRepo;
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public OrderResponseDTO placeOrder(OrderRequestDTO request) {
        enforceCustomerAccess(request.getCustomerId());

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Backward compatibility: legacy products with stock<=0 are treated as untracked inventory.
        if (product.getStock() != null
                && product.getStock() > 0
                && product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock available");
        }

        // Parse payment method from request using custom deserializer
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.fromValue(request.getPaymentMethod());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }

        Order savedOrder;
        if (request.getOrderId() == null) {
            Customer customer = customerRepo.findById(request.getCustomerId())
                    .orElseThrow(() -> new CustomerNotFound("Customer not found"));

            Order order = new Order();
            order.setCustomer(customer);
            order.setStatus("CONFIRMED");
            order.setTotalAmount(0.0);
            order.setPaymentMethod(paymentMethod);
            // All payment methods auto-succeed in demo mode
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            savedOrder = orderRepo.save(order);
        } else {
            savedOrder = orderRepo.findById(request.getOrderId())
                    .orElseThrow(() -> new OrderNotFound("Order not found"));
            // Update payment method if provided
            savedOrder.setPaymentMethod(paymentMethod);
            savedOrder.setPaymentStatus(PaymentStatus.SUCCESS);
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(savedOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(request.getQuantity());
        orderItem.setUnitPrice(product.getCost());
        orderItem.setSubtotal(request.getQuantity() * product.getCost());
        orderItemRepo.save(orderItem);

        // Reduce stock only when inventory tracking is active for this product.
        if (product.getStock() != null && product.getStock() > 0) {
            product.setStock(product.getStock() - request.getQuantity());
            productRepo.save(product);
        }

        // Calculate and update total amount
        double totalAmount = orderItemRepo.findByOrderId(savedOrder.getId())
                .stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
        savedOrder.setTotalAmount(totalAmount);
        Order finalOrder = orderRepo.save(savedOrder);

        return mapToResponse(finalOrder);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public OrderResponseDTO getOrderById(Integer id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFound("Order not found"));
        enforceCustomerAccess(order.getCustomer().getId());
        return mapToResponse(order);
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void cancelOrder(Integer orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFound("Order not found"));
        enforceCustomerAccess(order.getCustomer().getId());

        // Restore stock for all items in order
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepo.save(product);
        }

        orderRepo.deleteById(orderId);
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<OrderResponseDTO> getOrdersByCustomer(Integer customerId) {
        enforceCustomerAccess(customerId);
        return orderRepo.findOrderByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<OrderResponseDTO> getMyOrders() {
        return orderRepo.findOrderByCustomerEmail(getCurrentUserEmail())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponseDTO updateOrder(Integer id, OrderUpdateDTO dto) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFound("Order not found"));

        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new CustomerNotFound("Customer not found"));

        order.setCustomer(customer);
        if (dto.getStatus() != null && !isValidStatusTransition(order.getStatus(), dto.getStatus())) {
            throw new IllegalArgumentException("Invalid status transition from " + order.getStatus() + " to " + dto.getStatus());
        }
        if (dto.getStatus() != null) {
            order.setStatus(dto.getStatus());
        }
        if (dto.getTotalAmount() != null) {
            order.setTotalAmount(dto.getTotalAmount());
        }
         if (dto.getPaymentMethod() != null) {
             try {
                 order.setPaymentMethod(PaymentMethod.fromValue(dto.getPaymentMethod()));
             } catch (IllegalArgumentException e) {
                 throw new RuntimeException(e.getMessage());
             }
         }
        if (dto.getPaymentStatus() != null) {
            try {
                order.setPaymentStatus(PaymentStatus.valueOf(dto.getPaymentStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid payment status: " + dto.getPaymentStatus());
            }
        }

        Order updatedOrder = orderRepo.save(order);
        return mapToResponse(updatedOrder);
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Unauthenticated request");
        }
        return authentication.getName();
    }

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private void enforceCustomerAccess(Integer customerId) {
        if (isCurrentUserAdmin()) {
            return;
        }
        Customer currentUser = customerRepo.findByEmail(getCurrentUserEmail())
                .orElseThrow(() -> new CustomerNotFound("Authenticated customer not found"));

        if (!currentUser.getId().equals(customerId)) {
            throw new AccessDeniedException("You can only access your own orders");
        }
    }

    private boolean isValidStatusTransition(String fromStatus, String toStatus) {
        if (toStatus == null) {
            return true;
        }
        if (fromStatus == null) {
            return "CONFIRMED".equals(toStatus);
        }
        if (fromStatus.equals(toStatus)) {
            return true;
        }
        return ("CONFIRMED".equals(fromStatus) && "PROCESSING".equals(toStatus))
                || ("PROCESSING".equals(fromStatus) && "SHIPPED".equals(toStatus))
                || ("SHIPPED".equals(fromStatus) && "DELIVERED".equals(toStatus));
    }

    private OrderResponseDTO mapToResponse(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        dto.setCustomerName(order.getCustomer() != null ? order.getCustomer().getName() : null);
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : null);
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().toString() : null);
        dto.setOrderDate(order.getOrderDate());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }
}
