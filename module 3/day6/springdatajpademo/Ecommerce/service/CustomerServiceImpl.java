package org.example.springdatajpademo.Ecommerce.service;

import org.example.springdatajpademo.Ecommerce.exceptions.CustomerNotFound;
import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.Order;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepo.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    @Override
    public Customer getCustomerById(Integer id) {
        return customerRepo.findById(id).orElseThrow(()->new CustomerNotFound("Customer not found"));
    }

    @Override
    public Customer updateCustomer(Integer id, Customer customer) {
        Customer ctemp=customerRepo.findById(id).orElseThrow(()->new CustomerNotFound("Customer Not found"));
        ctemp.setName(customer.getName());
        ctemp.setEmail(customer.getEmail());
        ctemp.setAddress(customer.getAddress());

        return customerRepo.save(ctemp);
    }

    @Override
    public void deleteCustomer(Integer id) {
        customerRepo.deleteById(id);

    }

    @Override
    public List<Order> getCustomerOrders(Integer customerId) {
        Customer ctemp=customerRepo.findById(customerId).orElseThrow(()->new CustomerNotFound("Customer Not found"));
        return ctemp.getOrders();
    }
}
