package org.example.springdatajpademo.Ecommerce.service;


import org.example.springdatajpademo.Ecommerce.DTO.OrderResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderUpdateDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderRequestDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/*to place order
* valid customer->valid product
* create order->create orderitem and link
* then save orderitem*/
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepo orderRepo;


    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Override
    public OrderResponseDTO placeOrder(OrderRequestDTO request) {

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        Order savedOrder;

        if (request.getOrderId() == null) {

            Customer customer = customerRepo.findById(request.getCustomerId())
                    .orElseThrow(() ->
                            new CustomerNotFound("Customer not found"));

            Order order = new Order();

            order.setCustomer(customer);
            order.setStatus("PLACED");

            savedOrder = orderRepo.save(order);

        } else {

            savedOrder = orderRepo.findById(request.getOrderId())
                    .orElseThrow(() ->
                            new OrderNotFound("Order not found"));
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(savedOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(request.getQuantity());

        orderItemRepo.save(orderItem);

        return mapToResponse(savedOrder);
    }
    @Override
    public List<OrderResponseDTO> getAllOrders() {

        return orderRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public OrderResponseDTO getOrderById(Integer id) {

        Order order = orderRepo.findById(id)
                .orElseThrow(() ->
                        new OrderNotFound("Order not found"));

        return mapToResponse(order);
    }

    @Override
    public void cancelOrder(Integer orderId) {
        orderRepo.deleteById(orderId);

    }

    @Override
    public List<OrderResponseDTO> getOrdersByCustomer(Integer customerId) {

        return orderRepo.findOrderByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponseDTO updateOrder(Integer id,
                                        OrderUpdateDTO dto) {

        Order order = orderRepo.findById(id)
                .orElseThrow(() ->
                        new OrderNotFound("Order not found"));

        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() ->
                        new CustomerNotFound("Customer not found"));

        order.setCustomer(customer);
        order.setStatus(dto.getStatus());

        Order updatedOrder = orderRepo.save(order);

        return mapToResponse(updatedOrder);
    }

//    mapping
private OrderResponseDTO mapToResponse(Order order) {

    OrderResponseDTO dto = new OrderResponseDTO();

    dto.setId(order.getId());

    dto.setCustomerId(
            order.getCustomer() != null
                    ? order.getCustomer().getId()
                    : null
    );

    dto.setCustomerName(
            order.getCustomer() != null
                    ? order.getCustomer().getName()
                    : null
    );

    dto.setStatus(order.getStatus());

    return dto;
}
}
