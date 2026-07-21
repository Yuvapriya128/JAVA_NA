package org.northernarc.loanemi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.northernarc.loanemi.dto.CustomerResponseDTO;
import org.northernarc.loanemi.exception.CustomerNotFoundException;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile Management", description = "User profile management APIs")
public class ProfileController {
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get logged-in user profile")
    public CustomerResponseDTO getProfile(Authentication authentication) {
        log.info("Get profile requested for email={}", authentication.getName());
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomerNotFoundException("User not found with email: " + authentication.getName()));
        return toResponse(customer);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Update profile")
    public CustomerResponseDTO updateProfile(
            @Valid @RequestBody Map<String, String> updateRequest,
            Authentication authentication) {
        log.info("Update profile requested for email={}", authentication.getName());
        
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomerNotFoundException("User not found with email: " + authentication.getName()));
        
        if (updateRequest.containsKey("customerName")) {
            customer.setCustomerName(updateRequest.get("customerName"));
        }
        if (updateRequest.containsKey("phoneNumber")) {
            customer.setPhoneNumber(updateRequest.get("phoneNumber"));
        }
        if (updateRequest.containsKey("city")) {
            customer.setCity(updateRequest.get("city"));
        }
        
        Customer updated = customerRepository.save(customer);
        log.info("Profile updated successfully for email={}", authentication.getName());
        return toResponse(updated);
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Change password")
    public Map<String, String> changePassword(
            @Valid @RequestBody Map<String, String> passwordRequest,
            Authentication authentication) {
        log.info("Change password requested for email={}", authentication.getName());
        
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomerNotFoundException("User not found with email: " + authentication.getName()));
        
        String oldPassword = passwordRequest.get("oldPassword");
        String newPassword = passwordRequest.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            throw new IllegalArgumentException("Old password and new password are required");
        }
        
        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
        log.info("Password changed successfully for email={}", authentication.getName());
        
        return Map.of("message", "Password changed successfully");
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Logout user")
    public Map<String, String> logout(Authentication authentication) {
        log.info("Logout requested for email={}", authentication.getName());
        return Map.of("message", "Logged out successfully");
    }

    private CustomerResponseDTO toResponse(Customer customer) {
        return new CustomerResponseDTO(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCity(),
                customer.getCreditScore(),
                customer.getRole() == null ? null : customer.getRole().name(),
                customer.isActive()
        );
    }
}
