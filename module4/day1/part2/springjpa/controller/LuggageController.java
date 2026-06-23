package org.example.springjpa.controller;
import jakarta.validation.Valid;
import org.example.springjpa.dto.LuggageRequestDTO;
import org.example.springjpa.dto.LuggageResponseDTO;
import org.example.springjpa.dto.LuggageUpdateDTO;
import org.example.springjpa.service.LuggageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/luggage")
public class LuggageController {

    @Autowired
    private LuggageService luggageService;

    @PostMapping
    public ResponseEntity<LuggageResponseDTO> addLuggage(
            @Valid @RequestBody LuggageRequestDTO dto) {
        return ResponseEntity.status(201)
                .body(luggageService.addLuggage(dto));
    }

    @GetMapping
    public ResponseEntity<List<LuggageResponseDTO>>
    getAllLuggage() {
        return ResponseEntity.ok(
                luggageService.getAllLuggage());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LuggageResponseDTO>
    getLuggageById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                luggageService.getLuggageById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LuggageResponseDTO>
    updateLuggage(
            @PathVariable Integer id,
            @Valid @RequestBody LuggageUpdateDTO dto) {

        return ResponseEntity.ok(
                luggageService.updateLuggage(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLuggage(
            @PathVariable Integer id) {

        luggageService.deleteLuggage(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{luggageId}/passenger/{passengerId}")
    public ResponseEntity<String>
    assignLuggageToPassenger(
            @PathVariable Integer luggageId,
            @PathVariable Integer passengerId) {

        luggageService.assignLuggageToPassenger(
                luggageId,
                passengerId);

        return ResponseEntity.ok(
                "Luggage assigned to passenger");
    }

    @PutMapping("/{luggageId}/flight/{flightId}")
    public ResponseEntity<String>
    assignLuggageToFlight(
            @PathVariable Integer luggageId,
            @PathVariable Integer flightId) {

        luggageService.assignLuggageToFlight(
                luggageId,
                flightId);

        return ResponseEntity.ok(
                "Luggage assigned to flight");
    }

    @GetMapping("/weight/{weight}")
    public ResponseEntity<List<LuggageResponseDTO>>
    findByWeightGreaterThan(
            @PathVariable double weight) {

        return ResponseEntity.ok(
                luggageService.findByWeightGreaterThan(
                        weight));
    }

    @GetMapping("/fare/{fare}")
    public ResponseEntity<List<LuggageResponseDTO>>
    findByFareGreaterThan(
            @PathVariable double fare) {

        return ResponseEntity.ok(
                luggageService.findByFareGreaterThan(
                        fare));
    }
}