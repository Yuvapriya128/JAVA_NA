package org.example.loanemimgmt.serviceImpl;

import org.example.loanemimgmt.dto.AuthResponseDTO;
import org.example.loanemimgmt.dto.LoginRequestDTO;
import org.example.loanemimgmt.dto.RegisterRequestDTO;
import org.example.loanemimgmt.enums.UserRole;
import org.example.loanemimgmt.exception.BusinessRuleException;
import org.example.loanemimgmt.exception.CustomerNotFoundException;
import org.example.loanemimgmt.model.Customer;
import org.example.loanemimgmt.repository.CustomerRepository;
import org.example.loanemimgmt.security.JwtUtil;
import org.example.loanemimgmt.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        logger.info("Login attempt for user: {}", request.email());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException ex) {
            logger.warn("Failed login attempt for user: {} - Invalid credentials", request.email());
            throw new BadCredentialsException("Invalid email or password");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        logger.debug("JWT token generated for user: {}", request.email());

        Customer customer = customerRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> {
                    logger.error("Customer not found during login for email: {}", userDetails.getUsername());
                    return new CustomerNotFoundException("User not found");
                });

        logger.info("User {} logged in successfully", request.email());
        return new AuthResponseDTO(token, "Bearer", customer.getEmail(), customer.getRole());
    }

    @Override
    public String register(RegisterRequestDTO request) {
        logger.info("Registration attempt for email: {}", request.email());
        if (customerRepository.existsByEmailIgnoreCase(request.email())) {
            logger.warn("Registration failed - Email already exists: {}", request.email());
            throw new BusinessRuleException("Email already exists");
        }
        if (customerRepository.existsByPhoneNumber(request.phoneNumber())) {
            logger.warn("Registration failed - Phone number already exists: {}", request.phoneNumber());
            throw new BusinessRuleException("Phone number already exists");
        }

        Customer customer = Customer.builder()
                .customerName(request.customerName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .city(request.city())
                .creditScore(request.creditScore() == null ? 300 : request.creditScore())
                .role(UserRole.USER)
                .build();

        Customer saved = customerRepository.save(customer);
        logger.info("Customer registered successfully with ID: {}, Email: {}", saved.getCustomerId(), saved.getEmail());
        return "Customer registered with id: " + saved.getCustomerId();
    }
}


