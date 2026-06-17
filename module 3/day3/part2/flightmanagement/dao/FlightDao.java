package com.northernArc.flightmanagement.dao;

import com.northernArc.flightmanagement.model.Flight;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

public interface FlightDao {
    void save(Flight flight);
    Flight findByNo(String flightNo);
    Collection<Flight> findAll();
    void deleteByNo(String flightNo);
    Collection<Flight> findBySrcDestAndDepDate(String src, String dest, Date departureDate, Time departureTime);
    void updateByNo(String flightNo,Flight flight);


}
