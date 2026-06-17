package flightSIOC.service;

import flightSIOC.entity.Flight;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

public interface FlightService {
    void addFlight(Flight flight);

    Flight getFlight(String flightNo);

    Collection<Flight> getAllFlights();

    void deleteFlight(String flightNo);

    void updateFlight(String flightNo,Flight flight);

    Collection<Flight> searchFlight(
            String src,
            String dest,
            LocalDate depDate,
            LocalTime depTime);
}
