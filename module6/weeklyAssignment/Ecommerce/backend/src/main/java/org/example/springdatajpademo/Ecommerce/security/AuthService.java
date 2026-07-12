package org.example.springdatajpademo.Ecommerce.security;

import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final CustomerRepo customerRepo;
    private final JwtUtil jwtUtil;

    public AuthService(CustomerRepo customerRepo, JwtUtil jwtUtil) {
        this.customerRepo = customerRepo;
        this.jwtUtil = jwtUtil;
    }

    public String generateLoginToken(String authenticatedEmail) {
        Customer customer = customerRepo.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found for email: " + authenticatedEmail));

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", customer.getEmail());
        claims.put("name", customer.getName());
        claims.put("customerId", customer.getId());
        claims.put("role", customer.getRole() != null ? customer.getRole().name() : null);

        return jwtUtil.generateToken(customer.getEmail(), claims);
    }
}

