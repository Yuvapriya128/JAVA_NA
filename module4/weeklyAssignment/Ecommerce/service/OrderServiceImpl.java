package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.OrderRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderUpdateDTO;
import org.example.springdatajpademo.Ecommerce.exceptions.CustomerNotFound;
import org.example.springdatajpademo.Ecommerce.exceptions.OrderNotFound;
import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.Order;
import org.example.springdatajpademo.Ecommerce.model.OrderItem;
import org.example.springdatajpademo.Ecommerce.model.Product;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.example.springdatajpademo.Ecommerce.repository.OrderItemRepo;
import org.example.springdatajpademo.Ecommerce.repository.OrderRepo;
import org.example.springdatajpademo.Ecommerce.repository.ProductRepo;
import org.springframework.security.access.prepost.PreAuthorize;
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
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Order savedOrder;
        if (request.getOrderId() == null) {
            Customer customer = customerRepo.findById(request.getCustomerId())
                    .orElseThrow(() -> new CustomerNotFound("Customer not found"));

            Order order = new Order();
            order.setCustomer(customer);
            order.setStatus("PLACED");
            savedOrder = orderRepo.save(order);
        } else {
            savedOrder = orderRepo.findById(request.getOrderId())
                    .orElseThrow(() -> new OrderNotFound("Order not found"));
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(savedOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(request.getQuantity());
        orderItemRepo.save(orderItem);

        return mapToResponse(savedOrder);
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
        return mapToResponse(order);
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void cancelOrder(Integer orderId) {
        orderRepo.deleteById(orderId);
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<OrderResponseDTO> getOrdersByCustomer(Integer customerId) {
        return orderRepo.findOrderByCustomerId(customerId)
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
        order.setStatus(dto.getStatus());

        Order updatedOrder = orderRepo.save(order);
        return mapToResponse(updatedOrder);
    }

    private OrderResponseDTO mapToResponse(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        dto.setCustomerName(order.getCustomer() != null ? order.getCustomer().getName() : null);
        dto.setStatus(order.getStatus());
        return dto;
    }
}
