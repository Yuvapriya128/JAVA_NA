package org.northernarc.loanemi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.northernarc.loanemi.dto.ChangePasswordRequestDTO;
import org.northernarc.loanemi.dto.ChangePasswordResponseDTO;
import org.northernarc.loanemi.dto.LoginRequestDTO;
import org.northernarc.loanemi.dto.LoginResponseDTO;
import org.northernarc.loanemi.dto.RegisterRequestDTO;
import org.northernarc.loanemi.dto.RegisterResponseDTO;
import org.northernarc.loanemi.enums.Role;
import org.northernarc.loanemi.exception.CustomerNotFoundException;
import org.northernarc.loanemi.exception.ValidationException;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthController(CustomerRepository customerRepository, JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login requested for email={}", request.getEmail());
        Customer customer = customerRepository.findByEmail(request.getEmail()).orElse(null);
        if (customer == null) {
            log.warn("Login failed: user not found for email={}", request.getEmail());
            throw new UsernameNotFoundException("User not found");
        }
        if (!customer.isActive()) {
            log.warn("Login failed: customer is inactive (pending approval or deactivated) for email={}", request.getEmail());
            throw new org.springframework.security.authentication.BadCredentialsException("Account is inactive. Await admin approval.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            String rootCauseMessage = ex.getCause() != null ? ex.getCause().getMessage() : "none";
            log.warn(
                    "Authentication failed for email={} exceptionClass={} message={} rootCause={}",
                    request.getEmail(),
                    ex.getClass().getName(),
                    ex.getMessage(),
                    rootCauseMessage,
                    ex
            );
            throw ex;
        }

        LoginResponseDTO response = new LoginResponseDTO();
        String role = customer.getRole() != null ? customer.getRole().name() : "USER";
        response.setToken(jwtUtil.generateToken(customer.getEmail(), role));
        log.info("Login succeeded for email={} role={}", request.getEmail(), role);
        return response;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user account (public)")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Registration requested for email={}", request.getEmail());
        
        // Check if email already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: email already exists email={}", request.getEmail());
            throw new org.northernarc.loanemi.exception.DuplicateEmailException(
                "An account with this email already exists");
        }
        
        // Check if phone number already exists
        Customer existingPhone = customerRepository.findByPhoneNumber(request.getPhoneNumber());
        if (existingPhone != null) {
            log.warn("Registration failed: phone number already exists phone={}", request.getPhoneNumber());
            throw new ValidationException("An account with this phone number already exists");
        }
        
        // Create new customer - ALWAYS set role to USER regardless of any input
        Customer customer = new Customer();
        customer.setCustomerName(request.getCustomerName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setCity(request.getCity());
        customer.setRole(Role.USER.name()); // Force USER role - security critical
        customer.setCreditScore(650); // Default credit score for new users
        customer.setActive(false); // Requires explicit admin approval
        customer.setPasswordChangedAt(LocalDateTime.now());
        
        Customer saved = customerRepository.save(customer);
        
        // Audit log
        log.info("AUDIT: USER_REGISTERED - customerId={}, email={}, role=USER, timestamp={}",
                saved.getCustomerId(), saved.getEmail(), LocalDateTime.now());
        
        log.info("Registration successful and pending admin approval for email={} customerId={}", 
                saved.getEmail(), saved.getCustomerId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RegisterResponseDTO.success(saved.getCustomerId()));
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change password for authenticated user")
    public ResponseEntity<ChangePasswordResponseDTO> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request,
            HttpServletRequest httpRequest) {
        
        // Get current user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        String clientIp = getClientIp(httpRequest);
        
        log.info("Password change requested for user={} from IP={}", userEmail, clientIp);
        
        // Fetch user from database
        Customer customer = customerRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.warn("Password change failed: user not found for email={}", userEmail);
                    return new CustomerNotFoundException("User not found");
                });
        
        // Validate new password is different from current
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            log.warn("Password change failed: new password same as current for user={}", userEmail);
            throw new ValidationException("New password must be different from current password");
        }
        
        // Verify current password
        boolean currentPasswordValid = passwordEncoder.matches(request.getCurrentPassword(), customer.getPassword());
        if (!currentPasswordValid) {
            log.warn("Password change failed: incorrect current password for user={} from IP={}", userEmail, clientIp);
            throw new ValidationException("Current password is incorrect");
        }
        
        // Hash and update new password
        String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());
        customer.setPassword(hashedNewPassword);
        customer.setPasswordChangedAt(LocalDateTime.now());
        customerRepository.save(customer);
        
        // Audit log
        log.info("AUDIT: CHANGE_PASSWORD - userId={}, customerId={}, timestamp={}, clientIp={}",
                userEmail, customer.getCustomerId(), LocalDateTime.now(), clientIp);
        
        log.info("Password changed successfully for user={}", userEmail);
        
        return ResponseEntity.ok(new ChangePasswordResponseDTO("Password changed successfully."));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
