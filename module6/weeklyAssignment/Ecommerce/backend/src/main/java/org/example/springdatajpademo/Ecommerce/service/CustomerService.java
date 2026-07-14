package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.DTO.AdminCustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.ChangeCurrentPasswordDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CurrentCustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerRequestDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerResponseDTO;
import org.example.springdatajpademo.Ecommerce.DTO.CustomerUpdateDTO;
import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.Order;
import org.example.springdatajpademo.Ecommerce.model.UserRole;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO saveCustomer(CustomerRequestDTO customerDTO);

    CustomerResponseDTO saveCustomerByAdmin(AdminCustomerRequestDTO customerDTO);

    List<CustomerResponseDTO> getAllCustomers();

    CustomerResponseDTO getCustomerById(Integer id);

    CustomerResponseDTO updateCustomer(Integer id,
                                       CustomerUpdateDTO customerDTO);

    CustomerResponseDTO getCurrentCustomer();

    CustomerResponseDTO updateCurrentCustomer(CurrentCustomerUpdateDTO customerDTO);

    void changeCurrentPassword(ChangeCurrentPasswordDTO requestDTO);

    CustomerResponseDTO updateCustomerRole(Integer id, UserRole role);

    void deleteCustomer(Integer id);

    List<Order> getCustomerOrders(Integer customerId);
}
