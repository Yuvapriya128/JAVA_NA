package org.example.springdatajpademo.Ecommerce.controller;

import jakarta.validation.Valid;
import org.example.springdatajpademo.Ecommerce.DTO.OrderRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.OrderUpdateDTO;
import org.example.springdatajpademo.Ecommerce.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecom/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<OrderResponseDTO>> getAll() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderResponseDTO> getById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderResponseDTO> save(
            @Valid @RequestBody OrderRequestDTO dto) {

        logger.info("========== ORDER CONTROLLER HIT ==========");
        logger.info("POST /api/ecom/order request received");

        // Log authentication details
        var auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication object: {}", auth);
        logger.info("Principal: {}", auth != null ? auth.getPrincipal() : "null");
        logger.info("Authorities: {}", auth != null ? auth.getAuthorities() : "null");
        logger.info("Username: {}", auth != null ? auth.getName() : "null");
        logger.info("Is Authenticated: {}", auth != null ? auth.isAuthenticated() : "false");

        logger.info("OrderRequestDTO: {}", dto);

        return ResponseEntity.status(201)
                .body(orderService.placeOrder(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody OrderUpdateDTO dto) {

        return ResponseEntity.ok(
                orderService.updateOrder(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteById(
            @PathVariable Integer id) {

        orderService.cancelOrder(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>>
    getOrdersByCustomer(@PathVariable Integer id) {

        return ResponseEntity.ok(
                orderService.getOrdersByCustomer(id)
        );
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders() {

        return ResponseEntity.ok(
                orderService.getMyOrders()
        );
    }
}
