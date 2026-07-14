package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.AdminCustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ChangeCurrentPasswordDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CurrentCustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.exceptions.CustomerNotFound;
import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.Order;
import org.example.springdatajpademo.Ecommerce.model.UserRole;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public CustomerResponseDTO saveCustomer(CustomerRequestDTO dto) {

        Customer customer = mapToEntity(dto, UserRole.USER);

        Customer savedCustomer = customerRepo.save(customer);

        return mapToResponse(savedCustomer);
    }

    @Override
    public CustomerResponseDTO saveCustomerByAdmin(AdminCustomerRequestDTO dto) {

        UserRole role = dto.getRole() != null ? dto.getRole() : UserRole.USER;
        Customer customer = mapToEntity(dto, role);

        Customer savedCustomer = customerRepo.save(customer);

        return mapToResponse(savedCustomer);
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {

        return customerRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CustomerResponseDTO getCustomerById(Integer id) {

        Customer customer = customerRepo.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFound("Customer not found"));

        return mapToResponse(customer);
    }

    @Override
    public CustomerResponseDTO updateCustomer(Integer id,
                                              CustomerUpdateDTO dto) {

        Customer customer = customerRepo.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFound("Customer not found"));

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        if (dto.getPhoneNumber() != null) {
            customer.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (customer.getRole() == null) {
            customer.setRole(UserRole.USER);
        }

        Customer updatedCustomer = customerRepo.save(customer);

        return mapToResponse(updatedCustomer);
    }

    @Override
    public CustomerResponseDTO getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Current authenticated username: {}", authentication != null ? authentication.getName() : "null");

        Customer currentCustomer = getAuthenticatedCustomer();
        logger.info("Fetched customer: id={}, email={}, role={}",
                currentCustomer.getId(), currentCustomer.getEmail(), currentCustomer.getRole());

        CustomerResponseDTO responseDTO = mapToResponse(currentCustomer);
        logger.info("Returned DTO: {}", responseDTO);
        return responseDTO;
    }

    @Override
    public CustomerResponseDTO updateCurrentCustomer(CurrentCustomerUpdateDTO dto) {
        Customer customer = getAuthenticatedCustomer();

        String fullName = buildFullName(dto.getFirstName(), dto.getLastName());
        customer.setName(fullName);
        customer.setAddress(dto.getAddress());
        customer.setPhoneNumber(dto.getPhoneNumber());

        Customer updatedCustomer = customerRepo.save(customer);
        return mapToResponse(updatedCustomer);
    }

    @Override
    public void changeCurrentPassword(ChangeCurrentPasswordDTO requestDTO) {
        if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password must match");
        }

        Customer customer = getAuthenticatedCustomer();
        if (!passwordEncoder.matches(requestDTO.getCurrentPassword(), customer.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        customer.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        customerRepo.save(customer);
    }

    @Override
    public CustomerResponseDTO updateCustomerRole(Integer id, UserRole role) {

        Customer customer = customerRepo.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFound("Customer not found"));

        customer.setRole(role != null ? role : UserRole.USER);

        Customer updatedCustomer = customerRepo.save(customer);

        return mapToResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Integer id) {
        customerRepo.deleteById(id);
    }

    @Override
    public List<Order> getCustomerOrders(Integer customerId) {

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFound("Customer not found"));

        return customer.getOrders();
    }

    // =========================
    // Mapping Methods
    // =========================

    private Customer mapToEntity(CustomerRequestDTO dto, UserRole role) {

        Customer customer = new Customer();

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        customer.setRole(role);
        if (dto.getPhoneNumber() != null) {
            customer.setPhoneNumber(dto.getPhoneNumber());
        }

        return customer;
    }

    private Customer mapToEntity(AdminCustomerRequestDTO dto, UserRole role) {

        Customer customer = new Customer();

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        customer.setRole(role);
        if (dto.getPhoneNumber() != null) {
            customer.setPhoneNumber(dto.getPhoneNumber());
        }

        return customer;
    }

    private CustomerResponseDTO mapToResponse(Customer customer) {

        CustomerResponseDTO dto = new CustomerResponseDTO();

        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setPassword(customer.getPassword());
        dto.setRole(customer.getRole() != null ? customer.getRole().name() : null);
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setJoinedDate(customer.getJoinedDate());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());

        return dto;
    }

    private Customer getAuthenticatedCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("Authenticated customer not found in security context");
        }

        String email = authentication.getName();
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Authenticated customer email is missing");
        }

        return customerRepo.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFound("Customer not found"));
    }

    private String buildFullName(String firstName, String lastName) {
        return (firstName.trim() + " " + lastName.trim()).trim();
    }
}