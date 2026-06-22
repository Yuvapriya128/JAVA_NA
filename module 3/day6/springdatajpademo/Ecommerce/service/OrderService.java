package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.OrderRequestDTO;
import org.example.springdatajpademo.Ecommerce.model.Order;

import java.util.List;

public interface OrderService {

    Order placeOrder(OrderRequestDTO request);

    List<Order> getAllOrders();

    Order getOrderById(Integer id);

    void cancelOrder(Integer orderId);

    List<Order> getOrdersByCustomer(Integer customerId);

    Order updateOrder(Integer id, Order order);
}