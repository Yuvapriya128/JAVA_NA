package org.example.springjpa.service;

import org.example.springjpa.dto.*;

import java.util.List;

public interface PassengerService {
    PassengerResponseDTO savePassenger(PassengerRequestDTO dto);

    List<PassengerResponseDTO> getAllPassengers();

    PassengerResponseDTO getPassengerById(Integer id);

    PassengerResponseDTO updatePassenger(
            Integer id,
            PassengerUpdateDTO dto
    );

    void deletePassenger(Integer id);

    List<TicketResponseDTO> getTicketsByPassengerId(Integer passengerId);

    List<LuggageResponseDTO> getLuggageByPassengerId(Integer passengerId);

    List<PassengerResponseDTO> findByEmail(String email);

    List<PassengerResponseDTO> findByName(String name);


}
