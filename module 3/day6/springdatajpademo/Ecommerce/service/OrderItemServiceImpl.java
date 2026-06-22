package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.model.OrderItem;
import org.example.springdatajpademo.Ecommerce.repository.OrderItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService{


    @Autowired
    private OrderItemRepo orderItemRepo;

    @Override
    public OrderItem saveOrderItem(OrderItem orderItem) {

        return orderItemRepo.save(orderItem);
    }

    @Override
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepo.findAll();
    }

    @Override
    public OrderItem getOrderItemById(Integer id) {
        return orderItemRepo.findById(id).orElseThrow(()->new RuntimeException("Order Item not found"));
    }

    @Override
    public void deleteOrderItem(Integer id) {
        orderItemRepo.deleteById(id);
    }

    @Override
    public List<OrderItem> getItemsByOrder(Integer orderId) {
        return orderItemRepo.findByOrderId(orderId);
    }
}
