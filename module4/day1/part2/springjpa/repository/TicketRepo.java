package org.example.springjpa.repository;

import org.example.springjpa.dto.TicketResponseDTO;
import org.example.springjpa.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket,Integer> {
    TicketResponseDTO findTicketById(int id);
    List<TicketResponseDTO> findTicketByStatus(String status);
    List<TicketResponseDTO> findTicketBySeatno(int seatNo);
}

