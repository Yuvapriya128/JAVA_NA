package org.northernarc.week5_assess.service;

import org.northernarc.week5_assess.dto.CustomerDto;

import java.util.List;

public interface CustomerService {

	Object createCustomer(CustomerDto customerDto);

	Object getCustomerById(Long id);

	List<Object> getAllCustomers();

	Object updateCustomer(Long id, CustomerDto customerDto);

	void deleteCustomer(Long id);
}

