package org.example.loanemimgmt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.loanemimgmt.model.Customer;
import org.example.loanemimgmt.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Customer management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Create customer")
    public Map<String, String> createCustomer(@Valid @RequestBody Customer customer) {
        logger.info("API call: Create customer - Email: {}", customer.getEmail());
        return customerService.createCustomer(customer);
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @Operation(summary = "Get customer by id")
    public Map<String, String> getCustomerById(@PathVariable Long customerId) {
        logger.debug("API call: Get customer - ID: {}", customerId);
        return customerService.getCustomerById(customerId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Get all customers")
    public List<Map<String, String>> getCustomers(@RequestParam(required = false) String city) {
        logger.debug("API call: Get customers - City filter: {}", city != null && !city.isBlank() ? city : "None");
        if (city != null && !city.isBlank()) {
            return customerService.getCustomersByCity(city);
        }
        return customerService.getAllCustomers();
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete customer")
    public Map<String, String> deleteCustomer(@PathVariable Long customerId) {
        logger.info("API call: Delete customer - ID: {}", customerId);
        return customerService.deleteCustomer(customerId);
    }
}

