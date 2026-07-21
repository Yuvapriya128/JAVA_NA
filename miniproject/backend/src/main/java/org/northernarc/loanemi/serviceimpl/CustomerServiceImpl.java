package org.northernarc.loanemi.serviceimpl;

import org.northernarc.loanemi.dto.CreateCustomerRequest;
import org.northernarc.loanemi.dto.CustomerResponseDTO;
import org.northernarc.loanemi.dto.UpdateCustomerRequest;
import org.northernarc.loanemi.enums.LoanStatus;
import org.northernarc.loanemi.enums.Role;
import org.northernarc.loanemi.exception.CustomerNotFoundException;
import org.northernarc.loanemi.exception.ValidationException;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.repository.LoanRepository;
import org.northernarc.loanemi.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository, LoanRepository loanRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CreateCustomerRequest request) {
        log.info("Creating customer for email={} city={}", request.getEmail(), request.getCity());
        if (customerRepository.existsByEmail(request.getEmail())) {
            log.warn("Duplicate customer creation attempted for email={}", request.getEmail());
            throw new DataIntegrityViolationException("Email already registered");
        }

        Customer customer = new Customer();
        customer.setCustomerName(request.getCustomerName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setCity(request.getCity());
        customer.setCreditScore(request.getCreditScore());
        customer.setRole(request.getRole());
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully customerId={} email={}",
                savedCustomer.getCustomerId(), savedCustomer.getEmail());
        return toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomer(Long customerId) {
        log.info("Fetching customer details customerId={}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        return toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerByEmail(String email) {
        log.info("Fetching customer details by email={}", email);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));
        return toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(int page, int size, String sort, Sort.Direction direction) {
        log.info("Fetching all customers page={} size={} sort={} direction={}", page, size, sort, direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return customerRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> searchCustomers(String name, String email, String phone, String city, String role, 
                                                     Boolean active, Integer creditScoreMin, Integer creditScoreMax, 
                                                     int page, int size, String sort, Sort.Direction direction) {
        log.info("Searching customers name={} email={} city={} active={}", name, email, city, active);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        List<Customer> allCustomers = customerRepository.findAll();
        
        List<Customer> filtered = allCustomers.stream()
                .filter(c -> name == null || c.getCustomerName().toLowerCase().contains(name.toLowerCase()))
                .filter(c -> email == null || c.getEmail().toLowerCase().contains(email.toLowerCase()))
                .filter(c -> phone == null || c.getPhoneNumber().contains(phone))
                .filter(c -> city == null || c.getCity().equalsIgnoreCase(city))
                .filter(c -> role == null || (c.getRole() != null && c.getRole().name().equalsIgnoreCase(role)))
                .filter(c -> active == null || c.isActive() == active)
                .filter(c -> creditScoreMin == null || c.getCreditScore() >= creditScoreMin)
                .filter(c -> creditScoreMax == null || c.getCreditScore() <= creditScoreMax)
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, filtered.size());
        List<CustomerResponseDTO> content = filtered.subList(start, end).stream()
                .map(this::toResponse)
                .toList();
        
        return new PageImpl<>(content, pageable, filtered.size());
    }

    @Override
    @Transactional
    public CustomerResponseDTO activateCustomer(Long customerId) {
        log.info("Activate customer requested customerId={}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        if (customer.isActive()) {
            return toResponse(customer);
        }

        customer.setActive(true);
        Customer updated = customerRepository.save(customer);
        log.info("Customer activated successfully customerId={}", customerId);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public CustomerResponseDTO deactivateCustomer(Long customerId) {
        log.info("Deactivate customer requested customerId={}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        if (loanRepository.existsByCustomerCustomerIdAndLoanStatus(customerId, LoanStatus.ACTIVE)) {
            throw new IllegalStateException("Cannot deactivate customer with active loans");
        }
        if (!customer.isActive()) {
            return toResponse(customer);
        }

        customer.setActive(false);
        Customer updated = customerRepository.save(customer);
        log.info("Customer deactivated successfully customerId={}", customerId);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(Long customerId, UpdateCustomerRequest request, String actorEmail) {
        log.info("Update customer requested customerId={} by actor={}", customerId, actorEmail);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        
        // Check if email is being changed and if new email already exists
        if (!customer.getEmail().equalsIgnoreCase(request.getEmail())) {
            Optional<Customer> existingWithEmail = customerRepository.findByEmail(request.getEmail());
            if (existingWithEmail.isPresent() && !existingWithEmail.get().getCustomerId().equals(customerId)) {
                throw new ValidationException("Email already registered to another customer");
            }
        }
        
        // Validate deactivation with active loans
        if (customer.isActive() && !request.getActive()) {
            if (loanRepository.existsByCustomerCustomerIdAndLoanStatus(customerId, LoanStatus.ACTIVE)) {
                throw new ValidationException("Cannot deactivate customer with active loans");
            }
        }
        
        // Track changes for audit
        StringBuilder changes = new StringBuilder();
        if (!customer.getCustomerName().equals(request.getCustomerName())) {
            changes.append("name: ").append(customer.getCustomerName()).append(" -> ").append(request.getCustomerName()).append("; ");
        }
        if (!customer.getEmail().equals(request.getEmail())) {
            changes.append("email: ").append(customer.getEmail()).append(" -> ").append(request.getEmail()).append("; ");
        }
        if (!customer.getPhoneNumber().equals(request.getPhoneNumber())) {
            changes.append("phone: ").append(customer.getPhoneNumber()).append(" -> ").append(request.getPhoneNumber()).append("; ");
        }
        if (!customer.getCity().equals(request.getCity())) {
            changes.append("city: ").append(customer.getCity()).append(" -> ").append(request.getCity()).append("; ");
        }
        if (!customer.getCreditScore().equals(request.getCreditScore())) {
            changes.append("creditScore: ").append(customer.getCreditScore()).append(" -> ").append(request.getCreditScore()).append("; ");
        }
        if (customer.getRole() != request.getRole()) {
            changes.append("role: ").append(customer.getRole()).append(" -> ").append(request.getRole()).append("; ");
        }
        if (customer.isActive() != request.getActive()) {
            changes.append("active: ").append(customer.isActive()).append(" -> ").append(request.getActive()).append("; ");
        }
        
        // Update fields
        customer.setCustomerName(request.getCustomerName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setCity(request.getCity());
        customer.setCreditScore(request.getCreditScore());
        customer.setRole(request.getRole().name());
        customer.setActive(request.getActive());
        
        Customer updated = customerRepository.save(customer);
        
        log.info("AUDIT: CUSTOMER_UPDATED - customerId={}, actor={}, changes=[{}], timestamp={}", 
                customerId, actorEmail, changes, LocalDateTime.now());
        
        return toResponse(updated);
    }

    private CustomerResponseDTO toResponse(Customer customer) {
        return new CustomerResponseDTO(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCity(),
                customer.getCreditScore(),
                customer.getRole() == null ? null : customer.getRole().name(),
                customer.isActive()
        );
    }
}
