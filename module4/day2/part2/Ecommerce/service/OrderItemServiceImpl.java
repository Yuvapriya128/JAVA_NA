package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.OrderItemRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderItemResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderItemUpdateDTO;
import org.example.springdatajpademo.Ecommerce.exceptions.OrderNotFound;
import org.example.springdatajpademo.Ecommerce.model.Order;
import org.example.springdatajpademo.Ecommerce.model.OrderItem;
import org.example.springdatajpademo.Ecommerce.model.Product;
import org.example.springdatajpademo.Ecommerce.repository.OrderItemRepo;
import org.example.springdatajpademo.Ecommerce.repository.OrderRepo;
import org.example.springdatajpademo.Ecommerce.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Override
    public OrderItemResponseDTO saveOrderItem(OrderItemRequestDTO dto) {

        Order order = orderRepo.findById(dto.getOrderId())
                .orElseThrow(() ->
                        new OrderNotFound("Order not found"));

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(dto.getQuantity());

        OrderItem saved = orderItemRepo.save(orderItem);

        return mapToResponse(saved);
    }

    @Override
    public List<OrderItemResponseDTO> getAllOrderItems() {

        return orderItemRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderItemResponseDTO getOrderItemById(Integer id) {

        OrderItem orderItem = orderItemRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("OrderItem not found"));

        return mapToResponse(orderItem);
    }

    @Override
    public OrderItemResponseDTO updateOrderItem(Integer id,
                                                OrderItemUpdateDTO dto) {

        OrderItem orderItem = orderItemRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("OrderItem not found"));

        Order order = orderRepo.findById(dto.getOrderId())
                .orElseThrow(() ->
                        new OrderNotFound("Order not found"));

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(dto.getQuantity());

        OrderItem updated = orderItemRepo.save(orderItem);

        return mapToResponse(updated);
    }

    @Override
    public void deleteOrderItem(Integer id) {

        orderItemRepo.deleteById(id);
    }

    @Override
    public List<OrderItemResponseDTO> getItemsByOrder(Integer orderId) {

        return orderItemRepo.findByOrderId(orderId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderItemResponseDTO mapToResponse(OrderItem orderItem) {

        OrderItemResponseDTO dto = new OrderItemResponseDTO();

        dto.setId(orderItem.getId());

        dto.setOrderId(orderItem.getOrder().getId());

        dto.setProductId(orderItem.getProduct().getId());

        dto.setProductName(orderItem.getProduct().getName());

        dto.setQuantity(orderItem.getQuantity());

        dto.setProductCost(orderItem.getProduct().getCost());

        dto.setTotalPrice(
                orderItem.getQuantity()
                        * orderItem.getProduct().getCost()
        );

        return dto;
    }
}