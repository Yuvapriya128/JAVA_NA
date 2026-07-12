package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.CustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.exceptions.CustomerNotFound;
import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}

