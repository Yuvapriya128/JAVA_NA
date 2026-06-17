package com.northernArc.flightmanagement.dao;

import com.northernArc.flightmanagement.model.Flight;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Component
public class FlightDaoImpl implements FlightDao{
    List<Flight> flightList=new LinkedList<>();

    @PostConstruct
    public void init(){
        flightList.add(new Flight("AS234","Mumbai","Pune",1899,19));
        flightList.add(new Flight("ASD34","Pune","Chennai",2899,29));
    }

    @PreDestroy
    public void clear(){
        flightList.clear();
    }
    @Override
    public void save(Flight flight) {
        flightList.add(flight);
    }

    @Override
    public Flight findByNo(String flightNo) {
        for(Flight f:flightList){
            if(f.getFlightno().equalsIgnoreCase(flightNo)){
                return f;
            }
        }
        return null;
    }

    @Override
    public Collection<Flight> findAll() {
        return flightList;
    }

    @Override
    public void deleteByNo(String flightNo) {
        Flight temp=null;
        for(Flight f:flightList){
            if(f.getFlightno().equalsIgnoreCase(flightNo)){
                temp=f;
            }
        }
       flightList.remove(temp);
    }

    @Override
    public Collection<Flight> findBySrcDestAndDepDate(String src, String dest, Date departureDate, Time departureTime) {
       Collection<Flight> flights=new ArrayList<>();
       for(Flight f:flightList){
           if(f.getSource().equalsIgnoreCase(src) && f.getDestination().equalsIgnoreCase(dest)
           && f.getdOfDeparture().equals(departureDate) && f.gettOfDeparture().equals(departureTime)){
               flights.add(f);
           }
       }
        return flights;
    }

    @Override
    public void updateByNo(String flightNo, Flight flight) {
        for(Flight f:flightList){
            if(f.getFlightno().equalsIgnoreCase(flightNo)){
                f.setDestination(flight.getDestination());
                f.setCostPerSeat(flight.getCostPerSeat());
                f.setSource(flight.getSource());
                f.setNoOfSeat(flight.getNoOfSeat());

            }
        }

    }
}
