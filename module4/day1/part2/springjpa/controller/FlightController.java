package org.example.springjpa.controller;


import jakarta.validation.Valid;
import org.example.springjpa.dto.FlightRequestDTO;
import org.example.springjpa.dto.FlightResponseDTO;
import org.example.springjpa.dto.FlightUpdateDTO;
import org.example.springjpa.dto.LuggageResponseDTO;
import org.example.springjpa.dto.TicketResponseDTO;
import org.example.springjpa.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @PostMapping
    public ResponseEntity<FlightResponseDTO> saveFlight(
            @Valid @RequestBody FlightRequestDTO dto) {

        return ResponseEntity.status(201)
                .body(flightService.saveFlight(dto));
    }

    @GetMapping
    public ResponseEntity<List<FlightResponseDTO>> getAllFlights() {

        return ResponseEntity.ok(
                flightService.getAllFlights()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> getFlightById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                flightService.getFlightById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> updateFlight(
            @PathVariable Integer id,
            @Valid @RequestBody FlightUpdateDTO dto) {

        return ResponseEntity.ok(
                flightService.updateFlight(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(
            @PathVariable Integer id) {

        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/source/{src}")
    public ResponseEntity<List<FlightResponseDTO>> findBySource(
            @PathVariable String src) {

        return ResponseEntity.ok(
                flightService.findBySource(src)
        );
    }

    @GetMapping("/destination/{dest}")
    public ResponseEntity<List<FlightResponseDTO>> findByDestination(
            @PathVariable String dest) {

        return ResponseEntity.ok(
                flightService.findByDestination(dest)
        );
    }

    @GetMapping("/route/{src}/{dest}")
    public ResponseEntity<List<FlightResponseDTO>>
    findBySourceAndDestination(
            @PathVariable String src,
            @PathVariable String dest) {

        return ResponseEntity.ok(
                flightService.findBySourceAndDestination(src, dest)
        );
    }

    @GetMapping("/{flightId}/tickets")
    public ResponseEntity<List<TicketResponseDTO>>
    getTicketsByFlightId(
            @PathVariable Integer flightId) {

        return ResponseEntity.ok(
                flightService.getTicketsByFlightId(flightId)
        );
    }

    @GetMapping("/{flightId}/luggage")
    public ResponseEntity<List<LuggageResponseDTO>>
    getLuggageByFlightId(
            @PathVariable Integer flightId) {

        return ResponseEntity.ok(
                flightService.getLuggageByFlightId(flightId)
        );
    }
}