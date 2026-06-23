package org.example.springjpa.repository;

import org.example.springjpa.model.Luggage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LuggageRepo extends JpaRepository<Luggage,Integer> {
}
