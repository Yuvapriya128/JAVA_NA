package org.example.springjpa.service;

import org.example.springjpa.dto.TicketRequestDTO;
import org.example.springjpa.dto.TicketResponseDTO;
import org.example.springjpa.dto.TicketUpdateDTO;
import org.example.springjpa.model.Flight;
import org.example.springjpa.model.Passenger;
import org.example.springjpa.model.Ticket;
import org.example.springjpa.repository.FlightRepo;
import org.example.springjpa.repository.PassengerRepo;
import org.example.springjpa.repository.TicketRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService{
    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private FlightRepo flightRepo;

    @Autowired
    private PassengerRepo passengerRepo;


    private Ticket mapToEntity(TicketRequestDTO dto){
        Ticket t=new Ticket();
        t.setBoardingDate(dto.getBoardingDate());
        t.setBoardingTime(dto.getBoardingTime());
        t.setSeatfare(dto.getSeatfare());


        t.setSeatno(dto.getSeatno());
        return t;
    }
    private TicketResponseDTO mapToResponse(Ticket t){
        return new TicketResponseDTO (t.getId(),t.getSeatno(),t.getSeatfare()
        ,t.getBoardingDate(),t.getBoardingTime());
    }

    @Override
    public TicketResponseDTO bookTicket(TicketRequestDTO dto) {
        return mapToResponse(ticketRepo.save(mapToEntity(dto)));
    }

    @Override
    public List<TicketResponseDTO> getAllTickets() {
        return ticketRepo.findAll().stream().map(t->mapToResponse(t)).toList();
    }

    @Override
    public TicketResponseDTO getTicketById(Integer id) {
        return ticketRepo.findTicketById(id);
    }

    @Override
    public TicketResponseDTO updateTicket(Integer id, TicketUpdateDTO dto) {
        Ticket t=ticketRepo.findById(id).orElseThrow(()->new RuntimeException("Ticket not found"));

        t.setSeatno(dto.getSeatno());
        t.setSeatfare(dto.getSeatfare());
        t.setBoardingTime(dto.getBoardingTime());
        t.setBoardingDate(dto.getBoardingDate());

        return mapToResponse(ticketRepo.save(t));
    }

    @Override
    public void cancelTicket(Integer id) {
        Ticket t=ticketRepo.findById(id).orElseThrow(()->new RuntimeException("Ticket not found"));
        ticketRepo.delete(t);


    }

    @Override
    public void assignTicketToPassenger(Integer ticketId, Integer passengerId) {
        Passenger p=passengerRepo.findById(passengerId).orElseThrow(()->new RuntimeException("Passenger not found"));
        Ticket ticket=ticketRepo.findById(ticketId).orElseThrow(()->new RuntimeException("Ticket not found"));
        ticket.setPassenger(p);
        ticketRepo.save(ticket);

    }

    @Override
    public void assignTicketToFlight(Integer ticketId, Integer flightId) {
        Flight flight=flightRepo.findById(flightId).orElseThrow(()->new RuntimeException("Flight not found"));
        Ticket ticket=ticketRepo.findById(ticketId).orElseThrow(()->new RuntimeException("Ticket not found"));
        ticket.setFlight(flight);
        ticketRepo.save(ticket);

    }

    @Override
    public List<TicketResponseDTO> findByStatus(String status) {
        return ticketRepo.findTicketByStatus(status);
    }

    @Override
    public List<TicketResponseDTO> findBySeatNo(int seatNo) {
        return ticketRepo.findTicketBySeatno(seatNo);
    }

    @Override
    public List<TicketResponseDTO> getExpiredTickets() {
        return ticketRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .filter(t -> "Expired".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketResponseDTO> getActiveTickets() {
        return ticketRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .filter(t -> "Active".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());
    }
}
