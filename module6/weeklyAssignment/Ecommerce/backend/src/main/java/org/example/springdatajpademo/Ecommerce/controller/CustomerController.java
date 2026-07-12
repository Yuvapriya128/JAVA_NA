package org.example.springdatajpademo.Ecommerce.controller;

import jakarta.validation.Valid;
import org.example.springdatajpademo.Ecommerce.DTO.AdminCustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ChangeRoleRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.model.Order;
import org.example.springdatajpademo.Ecommerce.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecom/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
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

    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> saveByAdmin(
            @Valid @RequestBody AdminCustomerRequestDTO customerDTO) {

        return ResponseEntity
                .status(201)
                .body(customerService.saveCustomerByAdmin(customerDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody CustomerUpdateDTO customerDTO) {

        return ResponseEntity.ok(
                customerService.updateCustomer(id, customerDTO)
        );
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> updateRole(
            @PathVariable Integer id,
            @Valid @RequestBody ChangeRoleRequestDTO requestDTO) {

        return ResponseEntity.ok(
                customerService.updateCustomerRole(id, requestDTO.getRole())
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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