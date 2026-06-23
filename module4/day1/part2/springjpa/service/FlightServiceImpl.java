package org.example.springjpa.service;

import org.example.springjpa.dto.*;
import org.example.springjpa.model.Flight;
import org.example.springjpa.model.Luggage;
import org.example.springjpa.model.Ticket;
import org.example.springjpa.repository.FlightRepo;
import org.example.springjpa.repository.LuggageRepo;
import org.example.springjpa.repository.TicketRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FlightServiceImpl implements FlightService{

    @Autowired
    private FlightRepo flightRepo;

    @Autowired
    private LuggageRepo luggageRepo;

    @Autowired
    private TicketRepo ticketRepo;

    private FlightResponseDTO mapToResponse(Flight f){

        return new FlightResponseDTO(f.getId(),f.getSrc(),f.getDest(),
                f.getDod(),f.getDoa(),f.getTod(),f.getToa());
    }

    private TicketResponseDTO mapToResponseTicket(Ticket t){

        return new TicketResponseDTO(t.getId(),t.getSeatno(),t.getSeatfare(),t.getBoardingDate(),t.getBoardingTime());
    }
    private LuggageResponseDTO mapToResponseLug(Luggage l){
        return new LuggageResponseDTO(l.getId(),l.getWeight(),l.getFare());
    }
    private Flight mapToEntity(FlightRequestDTO f){
        Flight flight=new Flight();
        flight.setDest(f.getDest());
        flight.setSrc(f.getSrc());
        flight.setDoa(f.getDoa());
        flight.setDod(f.getDod());
        flight.setToa(f.getToa());
        flight.setTod(f.getTod());
        return flight;
    }


    @Override
    public FlightResponseDTO saveFlight(FlightRequestDTO dto) {
        Flight tempf=mapToEntity(dto);

        return mapToResponse(flightRepo.save(tempf));
    }

    @Override
    public List<FlightResponseDTO> getAllFlights() {
        return (flightRepo.findAll().stream().map((f)->mapToResponse(f)).toList());
    }

    @Override
    public FlightResponseDTO getFlightById(Integer id) {
        Flight flight= flightRepo.findById(id).orElseThrow(()->new RuntimeException("Flight not found"));
        return mapToResponse(flight);
    }

    @Override
    public FlightResponseDTO updateFlight(Integer id, FlightUpdateDTO dto) {
        Flight flight=flightRepo.findById(id).orElseThrow(()-> new RuntimeException("Flight not found"));
        flight.setSrc(dto.getSrc());
        flight.setDest(dto.getDest());
        flight.setDoa(dto.getDoa());
        flight.setDod(dto.getDod());
        flight.setToa(dto.getToa());
        flight.setTod(dto.getTod());
        Flight flight1=flightRepo.save(flight);
        return mapToResponse(flight1);
    }

    @Override
    public void deleteFlight(Integer id) {
        flightRepo.deleteById(id);
    }

    @Override
    public List<FlightResponseDTO> findBySource(String src) {
       return flightRepo.findBySrc(src).stream().map(this::mapToResponse).toList();

    }

    @Override
    public List<FlightResponseDTO> findByDestination(String dest) {
        return flightRepo.findByDest(dest).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<FlightResponseDTO> findBySourceAndDestination(String src, String dest) {
        return flightRepo.findBySrcAndDest(src,dest).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<TicketResponseDTO> getTicketsByFlightId(Integer flightId) {
        Flight temp=flightRepo.findById(flightId).orElseThrow(()->new RuntimeException("Flight not found"));
        return temp.getTickets().stream().map((t)->mapToResponseTicket(t)).toList();
    }

    @Override
    public List<LuggageResponseDTO> getLuggageByFlightId(Integer flightId) {
        Flight temp=flightRepo.findById(flightId).orElseThrow(()-> new RuntimeException("Flight not found"));
        return temp.getLuggageList().stream().map((l)->mapToResponseLug(l)).toList();

    }
}
