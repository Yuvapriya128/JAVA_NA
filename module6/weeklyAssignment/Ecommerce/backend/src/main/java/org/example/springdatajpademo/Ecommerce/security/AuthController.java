package org.example.springdatajpademo.Ecommerce.security;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.springdatajpademo.Ecommerce.DTO.AuthRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.AuthResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ErrorResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ecom/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO authRequest) {
        logger.info("=== LOGIN REQUEST STARTED ===");
        logger.info("Email: {}", authRequest.getEmail());
        logger.debug("Password provided: {}", authRequest.getPassword() != null ? "YES (length: " + authRequest.getPassword().length() + ")" : "NO");

        try {
            logger.debug("Creating UsernamePasswordAuthenticationToken with email: {}", authRequest.getEmail());
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());

            logger.debug("Calling authenticationManager.authenticate()");
            Authentication authentication = authenticationManager.authenticate(authToken);

            logger.info("Authentication successful for email: {}", authRequest.getEmail());
            logger.debug("Principal: {}, Authorities: {}", authentication.getPrincipal(), authentication.getAuthorities());

            String authenticatedEmail = authentication.getName();
            logger.debug("Generating JWT token with claims for user: {}", authenticatedEmail);

            String token = authService.generateLoginToken(authenticatedEmail);
            logger.info("JWT token generated successfully");

            AuthResponseDTO response = new AuthResponseDTO(token);
            logger.info("=== LOGIN REQUEST COMPLETED SUCCESSFULLY ===");
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            logger.error("BadCredentialsException: Email or password mismatch for email: {}", authRequest.getEmail());
            logger.debug("Exception message: {}", ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    "INVALID_CREDENTIALS",
                    "Email or password don't match",
                    401
            );
            return ResponseEntity.status(401).body(errorResponse);

        } catch (UsernameNotFoundException ex) {
            logger.error("UsernameNotFoundException: Customer not found for email: {}", authRequest.getEmail());
            logger.debug("Exception message: {}", ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    "USER_NOT_FOUND",
                    "Customer not found with provided email",
                    401
            );
            return ResponseEntity.status(401).body(errorResponse);

        } catch (Exception ex) {
            logger.error("Unexpected authentication exception for email: {}", authRequest.getEmail(), ex);
            logger.error("Exception type: {}", ex.getClass().getName());
            logger.error("Exception message: {}", ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    "AUTHENTICATION_FAILED",
                    "Authentication failed. Please try again.",
                    401
            );
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}

