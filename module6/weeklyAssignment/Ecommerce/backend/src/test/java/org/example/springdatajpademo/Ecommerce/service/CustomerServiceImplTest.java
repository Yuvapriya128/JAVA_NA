package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.ChangeCurrentPasswordDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CurrentCustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.exceptions.CustomerNotFound;
import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerServiceImplTest {

    private CustomerRepo customerRepo;
    private PasswordEncoder passwordEncoder;
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerRepo = mock(CustomerRepo.class);
        passwordEncoder = mock(PasswordEncoder.class);

        customerService = new CustomerServiceImpl();
        ReflectionTestUtils.setField(customerService, "customerRepo", customerRepo);
        ReflectionTestUtils.setField(customerService, "passwordEncoder", passwordEncoder);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void saveCustomer_encodesPasswordAndReturnsResponse() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO(
                "Yuva",
                "yuva@example.com",
                "Chennai",
                "plainPassword"
        );

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(customerRepo.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(1);
            return c;
        });

        CustomerResponseDTO result = customerService.saveCustomer(requestDTO);

        assertEquals(1, result.getId());
        assertEquals("Yuva", result.getName());
        assertEquals("yuva@example.com", result.getEmail());
        assertEquals("Chennai", result.getAddress());
        assertEquals("encodedPassword", result.getPassword());

        verify(passwordEncoder).encode("plainPassword");
        verify(customerRepo).save(any(Customer.class));
    }

    @Test
    void updateCustomer_updatesPasswordWhenProvided() {
        Customer existing = new Customer();
        existing.setId(10);
        existing.setName("Old");
        existing.setEmail("old@example.com");
        existing.setAddress("Old Address");
        existing.setPassword("oldEncodedPassword");

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO(
                10,
                "New Name",
                "new@example.com",
                "New Address",
                "newPassword"
        );

        when(customerRepo.findById(10)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(customerRepo.save(existing)).thenReturn(existing);

        CustomerResponseDTO result = customerService.updateCustomer(10, updateDTO);

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("New Address", result.getAddress());
        assertEquals("newEncodedPassword", result.getPassword());

        verify(passwordEncoder).encode("newPassword");
        verify(customerRepo).save(existing);
    }

    @Test
    void getCustomerById_throwsWhenCustomerMissing() {
        when(customerRepo.findById(999)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFound.class, () -> customerService.getCustomerById(999));
    }

    @Test
    void updateCurrentCustomer_updatesOnlyCurrentAuthenticatedCustomer() {
        Customer current = new Customer();
        current.setId(12);
        current.setName("Old Name");
        current.setEmail("current@example.com");
        current.setAddress("Old Address");
        current.setPhoneNumber("+1234567890");
        current.setPassword("encodedPwd");

        setAuthentication("current@example.com");

        CurrentCustomerUpdateDTO updateDTO = new CurrentCustomerUpdateDTO(
                "Jane",
                "Doe",
                "New Address",
                "+19876543210"
        );

        when(customerRepo.findByEmail("current@example.com")).thenReturn(Optional.of(current));
        when(customerRepo.save(current)).thenReturn(current);

        CustomerResponseDTO response = customerService.updateCurrentCustomer(updateDTO);

        assertEquals("Jane Doe", response.getName());
        assertEquals("current@example.com", response.getEmail());
        assertEquals("New Address", response.getAddress());
        assertEquals("+19876543210", response.getPhoneNumber());
        verify(customerRepo).save(current);
    }

    @Test
    void changeCurrentPassword_updatesPasswordWhenCurrentPasswordMatches() {
        Customer current = new Customer();
        current.setId(3);
        current.setEmail("secure@example.com");
        current.setPassword("existingEncodedPassword");

        setAuthentication("secure@example.com");

        ChangeCurrentPasswordDTO requestDTO = new ChangeCurrentPasswordDTO(
                "currentPassword",
                "NewPass@123",
                "NewPass@123"
        );

        when(customerRepo.findByEmail("secure@example.com")).thenReturn(Optional.of(current));
        when(passwordEncoder.matches("currentPassword", "existingEncodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("NewPass@123")).thenReturn("newEncodedPassword");

        customerService.changeCurrentPassword(requestDTO);

        assertEquals("newEncodedPassword", current.getPassword());
        verify(customerRepo).save(current);
    }

    @Test
    void changeCurrentPassword_throwsWhenCurrentPasswordIsWrong() {
        Customer current = new Customer();
        current.setEmail("secure@example.com");
        current.setPassword("existingEncodedPassword");

        setAuthentication("secure@example.com");

        ChangeCurrentPasswordDTO requestDTO = new ChangeCurrentPasswordDTO(
                "wrongPassword",
                "NewPass@123",
                "NewPass@123"
        );

        when(customerRepo.findByEmail("secure@example.com")).thenReturn(Optional.of(current));
        when(passwordEncoder.matches("wrongPassword", "existingEncodedPassword")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.changeCurrentPassword(requestDTO)
        );

        assertTrue(ex.getMessage().contains("Current password is incorrect"));
    }

    private void setAuthentication(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
    }
}
