package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.CustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.Order;

import java.util.List;

public interface CustomerService {

CustomerResponseDTO saveCustomer(CustomerRequestDTO customerDTO);

    List<CustomerResponseDTO> getAllCustomers();

    CustomerResponseDTO getCustomerById(Integer id);

    CustomerResponseDTO updateCustomer(Integer id,
                                       CustomerUpdateDTO customerDTO);

    void deleteCustomer(Integer id);

    List<Order> getCustomerOrders(Integer customerId);
}
