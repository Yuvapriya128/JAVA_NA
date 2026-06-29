package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.CustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.exceptions.CustomerNotFound;
import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.Order;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public CustomerResponseDTO saveCustomer(CustomerRequestDTO dto) {

        Customer customer = mapToEntity(dto);

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
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

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

    private Customer mapToEntity(CustomerRequestDTO dto) {

        Customer customer = new Customer();

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));

        return customer;
    }

    private CustomerResponseDTO mapToResponse(Customer customer) {

        CustomerResponseDTO dto = new CustomerResponseDTO();

        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setPassword(customer.getPassword());

        return dto;
    }
}