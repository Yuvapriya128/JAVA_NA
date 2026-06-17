package flightSIOC.entity;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Flight {
    private String flightno;
    private String source;
    private String destination;
    private double costPerSeat;
    private int noOfSeat;
    private LocalDate dOfDeparture;
    private LocalDate dOfArrival;
    private LocalTime tOfDeparture;
    private LocalTime tOfArrival;

    public Flight(){}

    public Flight(String flightno, String source, String destination, double costPerSeat, int noOfSeat) {
        this.flightno = flightno;
        this.source = source;
        this.destination = destination;
        this.costPerSeat = costPerSeat;
        this.noOfSeat = noOfSeat;
    }

    public Flight(String flightno, String source, String destination, double costPerSeat, int noOfSeat, LocalDate dOfDeparture, LocalDate dOfArrival, LocalTime tOfDeparture, LocalTime tOfArrival) {
        this.flightno = flightno;
        this.source = source;
        this.destination = destination;
        this.costPerSeat = costPerSeat;
        this.noOfSeat = noOfSeat;
        this.dOfDeparture = dOfDeparture;
        this.dOfArrival = dOfArrival;
        this.tOfDeparture = tOfDeparture;
        this.tOfArrival = tOfArrival;
    }


    @Override
    public String toString(){
        return ("Flight["+flightno+" "+source+" -> "+destination+" "+noOfSeat+" "+dOfDeparture+"-"+dOfArrival+" "+tOfDeparture+" "+tOfArrival +"]");
    }
    public String getFlightno() {
        return flightno;
    }

    public void setFlightno(String flightno) {
        this.flightno = flightno;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getCostPerSeat() {
        return costPerSeat;
    }

    public void setCostPerSeat(double costPerSeat) {
        this.costPerSeat = costPerSeat;
    }

    public int getNoOfSeat() {
        return noOfSeat;
    }

    public void setNoOfSeat(int noOfSeat) {
        this.noOfSeat = noOfSeat;
    }

    public LocalDate getdOfDeparture() {
        return dOfDeparture;
    }

    public void setdOfDeparture(LocalDate dOfDeparture) {
        this.dOfDeparture = dOfDeparture;
    }

    public LocalDate getdOfArrival() {
        return dOfArrival;
    }

    public void setdOfArrival(LocalDate dOfArrival) {
        this.dOfArrival = dOfArrival;
    }

    public LocalTime gettOfDeparture() {
        return tOfDeparture;
    }

    public void settOfDeparture(LocalTime tOfDeparture) {
        this.tOfDeparture = tOfDeparture;
    }

    public LocalTime gettOfArrival() {
        return tOfArrival;
    }

    public void settOfArrival(LocalTime tOfArrival) {
        this.tOfArrival = tOfArrival;
    }
}
