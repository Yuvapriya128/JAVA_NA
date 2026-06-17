package flightSIOC.dao;

import flightSIOC.entity.Flight;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

public interface FlightDao {
    void save(Flight flight);
    Flight findByNo(String flightNo);
    Collection<Flight> findAll();
    void deleteByNo(String flightNo);
    Collection<Flight> findBySrcDestAndDepDate(String src, String dest, LocalDate departureDate, LocalTime departureTime);
    void updateByNo(String flightNo,Flight flight);


}
