package org.example.springjpa.controller;
import jakarta.validation.Valid;
import org.example.springjpa.dto.LuggageResponseDTO;
import org.example.springjpa.dto.PassengerRequestDTO;
import org.example.springjpa.dto.PassengerResponseDTO;
import org.example.springjpa.dto.PassengerUpdateDTO;
import org.example.springjpa.dto.TicketResponseDTO;
import org.example.springjpa.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @PostMapping
    public ResponseEntity<PassengerResponseDTO> savePassenger(
            @Valid @RequestBody PassengerRequestDTO dto) {
        return ResponseEntity.status(201)
                .body(passengerService.savePassenger(dto));
    }

    @GetMapping
    public ResponseEntity<List<PassengerResponseDTO>> getAllPassengers() {
        return ResponseEntity.ok(passengerService.getAllPassengers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> getPassengerById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(passengerService.getPassengerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> updatePassenger(
            @PathVariable Integer id,
            @Valid @RequestBody PassengerUpdateDTO dto) {
        return ResponseEntity.ok(
                passengerService.updatePassenger(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(
            @PathVariable Integer id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<TicketResponseDTO>>
    getTicketsByPassengerId(@PathVariable Integer id) {
        return ResponseEntity.ok(
                passengerService.getTicketsByPassengerId(id));
    }

    @GetMapping("/{id}/luggage")
    public ResponseEntity<List<LuggageResponseDTO>>
    getLuggageByPassengerId(@PathVariable Integer id) {
        return ResponseEntity.ok(
                passengerService.getLuggageByPassengerId(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<PassengerResponseDTO>>
    findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(
                passengerService.findByEmail(email));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<PassengerResponseDTO>>
    findByName(@PathVariable String name) {
        return ResponseEntity.ok(
                passengerService.findByName(name));
    }
}