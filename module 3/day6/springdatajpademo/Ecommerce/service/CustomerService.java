package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.Order;

import java.util.List;

public interface CustomerService {
    Customer saveCustomer(Customer customer);

    List<Customer> getAllCustomers();

    Customer getCustomerById(Integer id);

    Customer updateCustomer(Integer id, Customer customer);

    void deleteCustomer(Integer id);

    List<Order> getCustomerOrders(Integer customerId);
}
