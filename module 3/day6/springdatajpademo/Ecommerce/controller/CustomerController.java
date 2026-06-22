package org.example.springdatajpademo.Ecommerce.controller;

import jakarta.validation.Valid;
import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.Order;
import org.example.springdatajpademo.Ecommerce.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecom/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> findall(){
      return  ResponseEntity.ok(customerService.getAllCustomers());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(@PathVariable int id){
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id){
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping
    public ResponseEntity<Customer> save(@Valid @RequestBody Customer customer){
        return ResponseEntity.status(201).body(customerService.saveCustomer(customer));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable int id,@Valid @RequestBody Customer customer){
        return ResponseEntity.ok(customerService.updateCustomer(id,customer));
    }
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<Order>> getCustomerOders(@PathVariable int id){
        return ResponseEntity.ok(customerService.getCustomerOrders(id));
    }

}
