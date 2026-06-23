package org.example.springjpa.repository;

import org.example.springjpa.dto.PassengerResponseDTO;
import org.example.springjpa.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepo extends JpaRepository<Passenger,Integer> {
    List<PassengerResponseDTO> findPassengerByEmail(String email);

    List<PassengerResponseDTO> findPassengerByName(String name);
}
