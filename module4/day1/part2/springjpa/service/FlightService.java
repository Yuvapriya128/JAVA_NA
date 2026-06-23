package org.example.springjpa.service;


import org.example.springjpa.dto.*;

import java.util.List;

public interface FlightService {
    FlightResponseDTO saveFlight(FlightRequestDTO dto);

    List<FlightResponseDTO> getAllFlights();

    FlightResponseDTO getFlightById(Integer id);

    FlightResponseDTO updateFlight(Integer id, FlightUpdateDTO dto);

    void deleteFlight(Integer id);

    List<FlightResponseDTO> findBySource(String src);

    List<FlightResponseDTO> findByDestination(String dest);

    List<FlightResponseDTO> findBySourceAndDestination(
            String src,
            String dest
    );
    List<TicketResponseDTO> getTicketsByFlightId(Integer flightId);

    List<LuggageResponseDTO> getLuggageByFlightId(Integer flightId);

}
