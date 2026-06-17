package com.northernArc.flightmanagement.model;


import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//use this with java only
//import java.time.LocalDate;
//import java.time.LocalTime;

public class Flight {
    private String flightno;
    private String source;
    private String destination;
    private double costPerSeat;
    private int noOfSeat;
    private Date dOfDeparture;
    private Date dOfArrival;
    private Time tOfDeparture;
    private Time tOfArrival;

//    This works while displaying in Localtime
//    public DateTimeFormatter timeFormatter =
//            DateTimeFormatter.ofPattern("HH:mm");


    public Flight(){}

    public Flight(String flightno, String source, String destination, double costPerSeat, int noOfSeat) {
        this.flightno = flightno;
        this.source = source;
        this.destination = destination;
        this.costPerSeat = costPerSeat;
        this.noOfSeat = noOfSeat;
    }

    public Flight(String flightno, String source, String destination, double costPerSeat, int noOfSeat, Date dOfDeparture, Date dOfArrival, Time tOfDeparture, Time tOfArrival) {
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
        return ("Flight["+flightno+" "+source+" -> "+destination+
                " "+noOfSeat+" "+
                (dOfDeparture == null ? LocalDate.now() : dOfDeparture)
                +"-->"+
                (dOfArrival == null ? LocalDate.now() : dOfArrival)
                +" " +
                (tOfDeparture ==null ? LocalTime.now().withNano(0).withSecond(0):tOfDeparture) +" "+
                (tOfArrival ==null ? LocalTime.now().plusMinutes(250).withSecond(0).withNano(0):tOfArrival)+"]");
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

    public Date getdOfDeparture() {
        return dOfDeparture;
    }

    public void setdOfDeparture(Date dOfDeparture) {
        this.dOfDeparture = dOfDeparture;
    }

    public Date getdOfArrival() {
        return dOfArrival;
    }

    public void setdOfArrival(Date dOfArrival) {
        this.dOfArrival = dOfArrival;
    }

    public Time gettOfDeparture() {
        return tOfDeparture;
    }

    public void settOfDeparture(Time tOfDeparture) {
        this.tOfDeparture = tOfDeparture;
    }

    public Time gettOfArrival() {
        return tOfArrival;
    }

    public void settOfArrival(Time tOfArrival) {
        this.tOfArrival = tOfArrival;
    }
}
