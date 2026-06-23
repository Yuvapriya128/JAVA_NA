package org.example.springjpa.repository;

import org.example.springjpa.dto.FlightResponseDTO;
import org.example.springjpa.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepo extends JpaRepository<Flight,Integer> {
    List<Flight> findBySrc(String src);
    List<Flight> findByDest(String dest);
    List<Flight> findBySrcAndDest(String src,String dest);
}
