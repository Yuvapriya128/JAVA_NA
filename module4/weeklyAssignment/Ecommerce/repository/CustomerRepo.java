package org.example.springdatajpademo.Ecommerce.repository;

import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer,Integer> {

	Optional<Customer> findByEmail(String email);

}
