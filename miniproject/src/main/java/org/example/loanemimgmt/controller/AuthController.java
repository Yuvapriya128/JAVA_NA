package org.example.loanemimgmt.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.loanemimgmt.dto.AuthResponseDTO;
import org.example.loanemimgmt.dto.LoginRequestDTO;
import org.example.loanemimgmt.dto.RegisterRequestDTO;
import org.example.loanemimgmt.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Authentication", description = "Login and registration endpoints")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Returns JWT token for valid credentials")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        logger.info("API call: Login attempt for user: {}", request.email());
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user", description = "Creates a new user account with USER role")
    @ApiResponse(responseCode = "201", description = "Registration successful")
    @ApiResponse(responseCode = "400", description = "Validation or business rule failed")
    public Map<String, String> register(@Valid @RequestBody RegisterRequestDTO request) {
        logger.info("API call: Register new user: {}", request.email());
        String message = authService.register(request);
        return Map.of("message", message);
    }
}

