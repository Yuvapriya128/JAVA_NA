package org.example.springjpa.service;

import org.example.springjpa.dto.LuggageRequestDTO;
import org.example.springjpa.dto.LuggageResponseDTO;
import org.example.springjpa.dto.LuggageUpdateDTO;
import org.example.springjpa.model.Flight;
import org.example.springjpa.model.Luggage;
import org.example.springjpa.model.Passenger;
import org.example.springjpa.repository.FlightRepo;
import org.example.springjpa.repository.LuggageRepo;
import org.example.springjpa.repository.PassengerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class LuggageServiceImpl implements LuggageService{
    @Autowired
    private LuggageRepo luggageRepo;

    @Autowired
    private PassengerRepo passengerRepo;
    @Autowired
    private FlightRepo flightRepo;

    private Luggage maptToEntity(LuggageRequestDTO dto){
        Luggage luggage=new Luggage();
        luggage.setWeight(dto.getWeight());
        luggage.setFare(dto.getFare());
        return luggage;
    }
    private LuggageResponseDTO mapToResponse(Luggage l){
        return  new LuggageResponseDTO(l.getId(),l.getFare(),l.getWeight());
    }

    @Override
    public LuggageResponseDTO addLuggage(LuggageRequestDTO dto) {
        Luggage templ=maptToEntity(dto);
        Luggage saved=luggageRepo.save(templ);

        return mapToResponse(saved);
    }

    @Override
    public List<LuggageResponseDTO> getAllLuggage() {
        return (luggageRepo.findAll().stream().map((l)->mapToResponse(l)).toList());
    }

    @Override
    public LuggageResponseDTO getLuggageById(Integer id) {
        Luggage saved=luggageRepo.findById(id).orElseThrow(()->new RuntimeException("Luggage not found"));
        return mapToResponse(saved);
    }

    @Override
    public LuggageResponseDTO updateLuggage(Integer id, LuggageUpdateDTO dto) {
        Luggage saved=luggageRepo.findById(id).orElseThrow(()->new RuntimeException("Luggage not found"));
        saved.setFare(dto.getFare());
        saved.setWeight(dto.getWeight());
        Luggage temp=luggageRepo.save(saved);
        return mapToResponse(temp);
    }

    @Override
    public void deleteLuggage(Integer id) {
        luggageRepo.deleteById(id);
    }

    @Override
    public void assignLuggageToPassenger(Integer luggageId, Integer passengerId) {
        Passenger passenger=passengerRepo.findById(passengerId).orElseThrow(()->new RuntimeException("Passenger not found"));
        Luggage luggage=luggageRepo.findById(luggageId).orElseThrow(()->new RuntimeException("Luggage not found"));
        luggage.setPassenger(passenger);

        luggageRepo.save(luggage);

    }

    @Override
    public void assignLuggageToFlight(Integer luggageId, Integer flightId) {
        Flight flight=flightRepo.findById(flightId).orElseThrow(()->new RuntimeException("Flight not found"));
        Luggage l=luggageRepo.findById(luggageId).orElseThrow(()->new RuntimeException("Luggage not found"));

        l.setFlight(flight);
        luggageRepo.save(l);

    }

    @Override
    public List<LuggageResponseDTO> findByWeightGreaterThan(double weight) {
        return luggageRepo.findAll().stream().filter(l-> l.getWeight() > weight).map(l->mapToResponse(l)).toList();

    }

    @Override
    public List<LuggageResponseDTO> findByFareGreaterThan(double fare) {
        return luggageRepo.findAll().stream().filter(l-> l.getFare() > fare).map(this::mapToResponse).toList();
    }
}
