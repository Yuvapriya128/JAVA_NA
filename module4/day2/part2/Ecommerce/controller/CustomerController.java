package org.example.springdatajpademo.Ecommerce.controller;

import jakarta.validation.Valid;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerUpdateDTO;
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
    public ResponseEntity<List<CustomerResponseDTO>> findAll() {

        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> findById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> save(
            @Valid @RequestBody CustomerRequestDTO customerDTO) {

        return ResponseEntity
                .status(201)
                .body(customerService.saveCustomer(customerDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody CustomerUpdateDTO customerDTO) {

        return ResponseEntity.ok(
                customerService.updateCustomer(id, customerDTO)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable Integer id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<Order>> getCustomerOrders(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                customerService.getCustomerOrders(id)
        );
    }
}