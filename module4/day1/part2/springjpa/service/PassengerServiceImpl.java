package org.example.springjpa.service;

import org.example.springjpa.dto.*;
import org.example.springjpa.model.Luggage;
import org.example.springjpa.model.Passenger;
import org.example.springjpa.model.Ticket;
import org.example.springjpa.repository.PassengerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService{
    @Autowired
    private PassengerRepo passengerRepo;

    private Passenger mapToEntity(PassengerRequestDTO p){
        Passenger temp=new Passenger();
        temp.setName(p.getName());
        temp.setEmail(p.getEmail());
        temp.setPhoneno(p.getPhoneno());
        return temp;
    }
    private PassengerResponseDTO mapToResponse(Passenger p){
        return new PassengerResponseDTO(p.getId(),p.getName());
    }
    private TicketResponseDTO mapToResponseTicket(Ticket t){

        return new TicketResponseDTO(t.getId(),t.getSeatno(),t.getSeatfare(),t.getBoardingDate(),t.getBoardingTime());
    }

    private LuggageResponseDTO mapToResponseLug(Luggage l){
        return  new LuggageResponseDTO(l.getId(),l.getFare(),l.getWeight());
    }

    @Override
    public PassengerResponseDTO savePassenger(PassengerRequestDTO dto) {
        Passenger p=mapToEntity(dto);
        return mapToResponse(passengerRepo.save(p));
    }

    @Override
    public List<PassengerResponseDTO> getAllPassengers() {
        return passengerRepo.findAll().stream().map((p)->mapToResponse(p)).toList();
    }

    @Override
    public PassengerResponseDTO getPassengerById(Integer id) {
        Passenger passenger=passengerRepo.findById(id).orElseThrow(()-> new RuntimeException("Passenger not found"));
        return mapToResponse(passenger);
    }

    @Override
    public PassengerResponseDTO updatePassenger(Integer id, PassengerUpdateDTO dto) {
        Passenger p=passengerRepo.findById(id).orElseThrow(()->new RuntimeException("Passenger not found"));

        p.setName(dto.getName());
        p.setPhoneno(dto.getPhoneno());
        p.setEmail(dto.getEmail());

        Passenger psaved=passengerRepo.save(p);

        return mapToResponse(psaved);
    }

    @Override
    public void deletePassenger(Integer id) {
        passengerRepo.deleteById(id);
    }

    @Override
    public List<TicketResponseDTO> getTicketsByPassengerId(Integer passengerId) {
        Passenger p=passengerRepo.findById(passengerId).orElseThrow(()->new RuntimeException("Passenger not found"));
        return p.getTickets().stream().map((t)->mapToResponseTicket(t)).toList();

    }

    @Override
    public List<LuggageResponseDTO> getLuggageByPassengerId(Integer passengerId) {
        Passenger p=passengerRepo.findById(passengerId).orElseThrow(()->new RuntimeException("Passenger not found"));
        return p.getLuggageList().stream().map(l->mapToResponseLug(l)).toList();

    }

    @Override
    public List<PassengerResponseDTO> findByEmail(String email) {
        return passengerRepo.findPassengerByEmail(email);
    }

    @Override
    public List<PassengerResponseDTO> findByName(String name) {
        return passengerRepo.findPassengerByName(name);
    }
}
