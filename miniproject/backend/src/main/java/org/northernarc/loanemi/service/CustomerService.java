package org.northernarc.loanemi.service;

import org.northernarc.loanemi.dto.CreateCustomerRequest;
import org.northernarc.loanemi.dto.CustomerResponseDTO;
import org.northernarc.loanemi.dto.UpdateCustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface CustomerService {
    CustomerResponseDTO createCustomer(CreateCustomerRequest request);

    CustomerResponseDTO getCustomer(Long customerId);

    CustomerResponseDTO getCustomerByEmail(String email);

    Page<CustomerResponseDTO> getAllCustomers(int page, int size, String sort, Sort.Direction direction);

    CustomerResponseDTO activateCustomer(Long customerId);

    CustomerResponseDTO deactivateCustomer(Long customerId);

    Page<CustomerResponseDTO> searchCustomers(String name, String email, String phone, String city, String role, 
                                             Boolean active, Integer creditScoreMin, Integer creditScoreMax, 
                                             int page, int size, String sort, Sort.Direction direction);
    
    /**
     * Update customer information.
     * @param customerId Customer ID
     * @param request Update request with new values
     * @param actorEmail Email of the user performing the update (for audit)
     * @return Updated customer
     */
    CustomerResponseDTO updateCustomer(Long customerId, UpdateCustomerRequest request, String actorEmail);
}
