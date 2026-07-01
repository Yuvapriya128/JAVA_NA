package org.example.loanemimgmt.serviceImpl;

import org.example.loanemimgmt.exception.BusinessRuleException;
import org.example.loanemimgmt.exception.CustomerNotFoundException;
import org.example.loanemimgmt.model.Customer;
import org.example.loanemimgmt.repository.CustomerRepository;
import org.example.loanemimgmt.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Map<String, String> createCustomer(Customer customer) {
        logger.info("Creating customer: {}", customer.getEmail());
        if (customerRepository.existsByEmailIgnoreCase(customer.getEmail())) {
            logger.warn("Customer creation failed - Email already exists: {}", customer.getEmail());
            throw new BusinessRuleException("Email already exists");
        }
        if (customerRepository.existsByPhoneNumber(customer.getPhoneNumber())) {
            logger.warn("Customer creation failed - Phone number already exists: {}", customer.getPhoneNumber());
            throw new BusinessRuleException("Phone number already exists");
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        Customer saved = customerRepository.save(customer);
        logger.info("Customer created successfully with ID: {}, Email: {}", saved.getCustomerId(), saved.getEmail());

        return toCustomerMap(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getCustomerById(Long customerId) {
        logger.debug("Fetching customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Customer not found for id: {}", customerId);
                    return new CustomerNotFoundException("Customer not found for id: " + customerId);
                });

        return toCustomerMap(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getAllCustomers() {
        logger.debug("Fetching all customers");
        return customerRepository.findAll().stream()
                .map(this::toCustomerMap)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getCustomersByCity(String city) {
        logger.debug("Fetching customers from city: {}", city);
        return customerRepository.findByCityIgnoreCase(city).stream()
                .map(this::toCustomerMap)
                .toList();
    }

    @Override
    public Map<String, String> deleteCustomer(Long customerId) {
        logger.info("Deleting customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Customer not found for deletion - ID: {}", customerId);
                    return new CustomerNotFoundException("Customer not found for id: " + customerId);
                });

        customerRepository.delete(customer);
        logger.info("Customer deleted successfully - ID: {}", customerId);
        return Map.of("message", "Customer deleted successfully", "customerId", String.valueOf(customerId));
    }

    private Map<String, String> toCustomerMap(Customer customer) {
        Map<String, String> row = new HashMap<>();
        row.put("customerId", String.valueOf(customer.getCustomerId()));
        row.put("customerName", customer.getCustomerName());
        row.put("email", customer.getEmail());
        row.put("phoneNumber", customer.getPhoneNumber());
        row.put("city", customer.getCity());
        row.put("creditScore", String.valueOf(customer.getCreditScore()));
        row.put("role", String.valueOf(customer.getRole()));
        return row;
    }
}

