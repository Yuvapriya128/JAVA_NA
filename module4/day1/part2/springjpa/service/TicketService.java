package org.example.springjpa.service;

import org.example.springjpa.dto.TicketRequestDTO;
import org.example.springjpa.dto.TicketResponseDTO;
import org.example.springjpa.dto.TicketUpdateDTO;

import java.util.List;

public interface TicketService {
    TicketResponseDTO bookTicket(TicketRequestDTO dto);

    List<TicketResponseDTO> getAllTickets();

    TicketResponseDTO getTicketById(Integer id);

    TicketResponseDTO updateTicket(
            Integer id,
            TicketUpdateDTO dto
    );

    void cancelTicket(Integer id);
    void assignTicketToPassenger(
            Integer ticketId,
            Integer passengerId
    );

    void assignTicketToFlight(
            Integer ticketId,
            Integer flightId
    );
    List<TicketResponseDTO> findByStatus(String status);

    List<TicketResponseDTO> findBySeatNo(int seatNo);
    List<TicketResponseDTO> getExpiredTickets();

    List<TicketResponseDTO> getActiveTickets();
}
