package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.model.OrderItem;

import java.util.List;

public interface OrderItemService {
    OrderItem saveOrderItem(OrderItem orderItem);

    List<OrderItem> getAllOrderItems();

    OrderItem getOrderItemById(Integer id);

    void deleteOrderItem(Integer id);

    List<OrderItem> getItemsByOrder(Integer orderId);
}