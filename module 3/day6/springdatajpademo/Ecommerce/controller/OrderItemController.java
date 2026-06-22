package org.example.springdatajpademo.Ecommerce.controller;

import jakarta.validation.Valid;
import org.example.springdatajpademo.Ecommerce.model.OrderItem;
import org.example.springdatajpademo.Ecommerce.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecom/orderitem")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<List<OrderItem>> findall(){
        return  ResponseEntity.ok(orderItemService.getAllOrderItems());
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> findById(@PathVariable int id){
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id){
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping
    public ResponseEntity<OrderItem> save(@Valid @RequestBody OrderItem OrderItem){
        return ResponseEntity.status(201).body(orderItemService.saveOrderItem(OrderItem));
    }
    @GetMapping("/order/{id}")
    public ResponseEntity<List<OrderItem>> getItemsByoid(@PathVariable int id){
        return ResponseEntity.ok(orderItemService.getItemsByOrder(id));
    }

}
