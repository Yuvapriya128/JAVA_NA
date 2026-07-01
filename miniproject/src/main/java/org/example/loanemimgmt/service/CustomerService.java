package org.example.loanemimgmt.service;

import org.example.loanemimgmt.model.Customer;

import java.util.List;
import java.util.Map;

public interface CustomerService {

    Map<String, String> createCustomer(Customer customer);

    Map<String, String> getCustomerById(Long customerId);

    List<Map<String, String>> getAllCustomers();

    List<Map<String, String>> getCustomersByCity(String city);

    Map<String, String> deleteCustomer(Long customerId);
}

