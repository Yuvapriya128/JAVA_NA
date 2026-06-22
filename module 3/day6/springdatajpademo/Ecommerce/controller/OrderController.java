package org.example.springdatajpademo.Ecommerce.controller;

import jakarta.validation.Valid;
import org.example.springdatajpademo.Ecommerce.DTO.OrderRequestDTO;
import org.example.springdatajpademo.Ecommerce.model.Order;
import org.example.springdatajpademo.Ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecom/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAll(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getbyid(@PathVariable int id){
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletebyid(@PathVariable int id){
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable int id,@Valid @RequestBody Order order){
        return ResponseEntity.status(201).body(orderService.updateOrder(id,order));
    }
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<Order>> getOrderbycust(@PathVariable int id){
        return ResponseEntity.ok(orderService.getOrdersByCustomer(id));
    }
    @PostMapping
    public ResponseEntity<Order> save(@Valid @RequestBody OrderRequestDTO orderRequestDTO){
        return ResponseEntity.status(201).body(orderService.placeOrder(orderRequestDTO));
    }




}
