package flightSIOC.service;

import flightSIOC.dao.FlightDao;
import flightSIOC.entity.Flight;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//created Dao interface variable and constructor

public class FlightServiceImpl implements FlightService{
    private final FlightDao flightDao;

    public FlightServiceImpl(FlightDao flightDao) {
        this.flightDao = flightDao;
    }

    @Override
    public void addFlight(Flight flight) {
        System.out.println(" Entered Flight Services");
      flightDao.save(flight);
    }

    @Override
    public Flight getFlight(String flightNo) {
        return flightDao.findByNo(flightNo);
    }

    @Override
    public Collection<Flight> getAllFlights() {
        return flightDao.findAll();
    }

    @Override
    public void deleteFlight(String flightNo) {
         flightDao.deleteByNo(flightNo);
    }

    @Override
    public void updateFlight(String flightNo, Flight flight) {
        flightDao.updateByNo(flightNo,flight);
    }

    @Override
    public Collection<Flight> searchFlight(String src, String dest, LocalDate depDate, LocalTime depTime) {
        return flightDao.findBySrcDestAndDepDate(src,dest,depDate,depTime);
    }

}
