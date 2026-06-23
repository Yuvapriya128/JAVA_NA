package org.example.springjpa.service;

import org.example.springjpa.dto.LuggageRequestDTO;
import org.example.springjpa.dto.LuggageResponseDTO;
import org.example.springjpa.dto.LuggageUpdateDTO;

import java.util.List;

public interface LuggageService {
    LuggageResponseDTO addLuggage(LuggageRequestDTO dto);

    List<LuggageResponseDTO> getAllLuggage();

    LuggageResponseDTO getLuggageById(Integer id);

    LuggageResponseDTO updateLuggage(
            Integer id,
            LuggageUpdateDTO dto
    );

    void deleteLuggage(Integer id);
    void assignLuggageToPassenger(
            Integer luggageId,
            Integer passengerId
    );

    void assignLuggageToFlight(
            Integer luggageId,
            Integer flightId
    );
    List<LuggageResponseDTO> findByWeightGreaterThan(
            double weight
    );

    List<LuggageResponseDTO> findByFareGreaterThan(
            double fare
    );
}
