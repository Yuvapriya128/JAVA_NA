package org.northernarc.loanemi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.northernarc.loanemi.dto.ChangePasswordRequestDTO;
import org.northernarc.loanemi.dto.ChangePasswordResponseDTO;
import org.northernarc.loanemi.exception.CustomerNotFoundException;
import org.northernarc.loanemi.exception.ValidationException;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private Customer testCustomer;
    private MockHttpServletRequest httpRequest;
    private final String TEST_EMAIL = "test@example.com";
    private final String CURRENT_PASSWORD = "OldPass@123";
    private final String NEW_PASSWORD = "NewPass@456";
    private final String ENCODED_CURRENT_PASSWORD = "$2a$10$encoded_current";
    private final String ENCODED_NEW_PASSWORD = "$2a$10$encoded_new";

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setEmail(TEST_EMAIL);
        testCustomer.setCustomerName("Test User");
        testCustomer.setPassword(ENCODED_CURRENT_PASSWORD);
        testCustomer.setRole("USER");
        testCustomer.setPhoneNumber("9876543210");
        testCustomer.setCity("Chennai");
        testCustomer.setCreditScore(750);
        testCustomer.setActive(true);

        httpRequest = new MockHttpServletRequest();
        httpRequest.setRemoteAddr("127.0.0.1");

        // Set up security context
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                TEST_EMAIL, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Change password - Success")
    void changePassword_Success() {
        // Given
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO(CURRENT_PASSWORD, NEW_PASSWORD);
        
        when(customerRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testCustomer));
        when(passwordEncoder.matches(CURRENT_PASSWORD, ENCODED_CURRENT_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        ResponseEntity<ChangePasswordResponseDTO> response = authController.changePassword(request, httpRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Password changed successfully.", response.getBody().getMessage());
        
        verify(customerRepository).save(any(Customer.class));
        verify(passwordEncoder).encode(NEW_PASSWORD);
    }

    @Test
    @DisplayName("Change password - User not found")
    void changePassword_UserNotFound() {
        // Given
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO(CURRENT_PASSWORD, NEW_PASSWORD);
        
        when(customerRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerNotFoundException.class, () -> {
            authController.changePassword(request, httpRequest);
        });
        
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Change password - Current password incorrect")
    void changePassword_IncorrectCurrentPassword() {
        // Given
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO("WrongPassword@123", NEW_PASSWORD);
        
        when(customerRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testCustomer));
        when(passwordEncoder.matches("WrongPassword@123", ENCODED_CURRENT_PASSWORD)).thenReturn(false);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            authController.changePassword(request, httpRequest);
        });
        
        assertEquals("Current password is incorrect", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Change password - New password same as current")
    void changePassword_SamePassword() {
        // Given
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO(CURRENT_PASSWORD, CURRENT_PASSWORD);
        
        when(customerRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testCustomer));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            authController.changePassword(request, httpRequest);
        });
        
        assertEquals("New password must be different from current password", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Change password - Password changed timestamp is updated")
    void changePassword_UpdatesTimestamp() {
        // Given
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO(CURRENT_PASSWORD, NEW_PASSWORD);
        
        when(customerRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testCustomer));
        when(passwordEncoder.matches(CURRENT_PASSWORD, ENCODED_CURRENT_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            assertNotNull(saved.getPasswordChangedAt());
            assertEquals(ENCODED_NEW_PASSWORD, saved.getPassword());
            return saved;
        });

        // When
        authController.changePassword(request, httpRequest);

        // Then
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Change password - X-Forwarded-For header used for IP")
    void changePassword_XForwardedForHeader() {
        // Given
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO(CURRENT_PASSWORD, NEW_PASSWORD);
        httpRequest.addHeader("X-Forwarded-For", "203.0.113.195, 70.41.3.18, 150.172.238.178");
        
        when(customerRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testCustomer));
        when(passwordEncoder.matches(CURRENT_PASSWORD, ENCODED_CURRENT_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        ResponseEntity<ChangePasswordResponseDTO> response = authController.changePassword(request, httpRequest);

        // Then
        assertEquals(200, response.getStatusCode().value());
    }
}
