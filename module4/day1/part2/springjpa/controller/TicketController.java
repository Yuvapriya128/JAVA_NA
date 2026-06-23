package org.example.springjpa.controller;
import jakarta.validation.Valid;
import org.example.springjpa.dto.TicketRequestDTO;
import org.example.springjpa.dto.TicketResponseDTO;
import org.example.springjpa.dto.TicketUpdateDTO;
import org.example.springjpa.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponseDTO> bookTicket(
            @Valid @RequestBody TicketRequestDTO dto) {
        return ResponseEntity.status(201)
                .body(ticketService.bookTicket(dto));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> updateTicket(
            @PathVariable Integer id,
            @Valid @RequestBody TicketUpdateDTO dto) {
        return ResponseEntity.ok(
                ticketService.updateTicket(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable Integer id) {
        ticketService.cancelTicket(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{ticketId}/passenger/{passengerId}")
    public ResponseEntity<String> assignTicketToPassenger(
            @PathVariable Integer ticketId,
            @PathVariable Integer passengerId) {

        ticketService.assignTicketToPassenger(
                ticketId,
                passengerId);

        return ResponseEntity.ok(
                "Ticket assigned to passenger");
    }

    @PutMapping("/{ticketId}/flight/{flightId}")
    public ResponseEntity<String> assignTicketToFlight(
            @PathVariable Integer ticketId,
            @PathVariable Integer flightId) {

        ticketService.assignTicketToFlight(
                ticketId,
                flightId);

        return ResponseEntity.ok(
                "Ticket assigned to flight");
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponseDTO>>
    findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(
                ticketService.findByStatus(status));
    }

    @GetMapping("/seat/{seatNo}")
    public ResponseEntity<List<TicketResponseDTO>>
    findBySeatNo(@PathVariable int seatNo) {
        return ResponseEntity.ok(
                ticketService.findBySeatNo(seatNo));
    }

    @GetMapping("/expired")
    public ResponseEntity<List<TicketResponseDTO>>
    getExpiredTickets() {
        return ResponseEntity.ok(
                ticketService.getExpiredTickets());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TicketResponseDTO>>
    getActiveTickets() {
        return ResponseEntity.ok(
                ticketService.getActiveTickets());
    }
}
