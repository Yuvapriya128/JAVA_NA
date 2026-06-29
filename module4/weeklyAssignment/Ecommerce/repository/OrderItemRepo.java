package org.example.springdatajpademo.Ecommerce.repository;

import org.example.springdatajpademo.Ecommerce.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem,Integer> {
    public List<OrderItem> findByOrderId(int orderid);
}
