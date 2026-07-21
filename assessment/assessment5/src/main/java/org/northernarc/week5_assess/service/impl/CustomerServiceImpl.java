package org.northernarc.week5_assess.service.impl;

import org.northernarc.week5_assess.dto.CustomerDto;
import org.northernarc.week5_assess.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Override
    public Object createCustomer(CustomerDto customerDto) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object getCustomerById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Object> getAllCustomers() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object updateCustomer(Long id, CustomerDto customerDto) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteCustomer(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

