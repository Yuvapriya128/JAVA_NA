package org.northernarc.loanemi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.northernarc.loanemi.dto.CreateCustomerRequest;
import org.northernarc.loanemi.dto.CustomerResponseDTO;
import org.northernarc.loanemi.dto.UpdateCustomerRequest;
import org.northernarc.loanemi.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer management APIs")
public class CustomerController {
    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Create a customer")
    public CustomerResponseDTO createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        log.info("Create customer requested for email={}", request.getEmail());
        return customerService.createCustomer(request);
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get customer by ID")
    public CustomerResponseDTO getCustomer(@PathVariable Long customerId) {
        log.info("Get customer requested for customerId={}", customerId);
        return customerService.getCustomer(customerId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Get all customers with pagination")
    public Page<CustomerResponseDTO> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "customerId") String sort,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        log.info("Get all customers requested page={} size={} sort={} direction={}", page, size, sort, direction);
        return customerService.getAllCustomers(page, size, sort, direction);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get current authenticated user profile")
    public CustomerResponseDTO getCurrentUser(Authentication authentication) {
        log.info("Get current user requested for email={}", authentication.getName());
        return customerService.getCustomerByEmail(authentication.getName());
    }

    @PatchMapping("/{customerId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate customer (soft delete)")
    public CustomerResponseDTO deactivateCustomer(@PathVariable Long customerId) {
        log.info("Deactivate customer requested for customerId={}", customerId);
        return customerService.deactivateCustomer(customerId);
    }

    @PatchMapping("/{customerId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve/activate customer")
    public CustomerResponseDTO activateCustomer(@PathVariable Long customerId) {
        log.info("Activate customer requested for customerId={}", customerId);
        return customerService.activateCustomer(customerId);
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Update customer information",
            description = "Update all customer fields. Only MANAGER and ADMIN can update customers.")
    public CustomerResponseDTO updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody UpdateCustomerRequest request,
            Authentication authentication) {
        log.info("Update customer requested for customerId={} by={}", customerId, authentication.getName());
        return customerService.updateCustomer(customerId, request, authentication.getName());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Search customers with filters")
    public Page<CustomerResponseDTO> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer creditScoreMin,
            @RequestParam(required = false) Integer creditScoreMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "customerId") String sort,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        log.info("Search customers requested name={} email={} city={} active={}", name, email, city, active);
        return customerService.searchCustomers(name, email, phone, city, role, active, creditScoreMin, creditScoreMax, page, size, sort, direction);
    }
}
