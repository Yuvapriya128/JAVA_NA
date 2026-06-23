package org.example.springjpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Entity
@Data
@Table(name = "travels_ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("ticketid")
    private Integer id;
    private int seatno;
    private double seatfare;
    private Date boardingDate;
    private Time boardingTime;
    private String status;

    @ManyToOne
    @JsonBackReference("passenger-ticket")
    private Passenger passenger;

    @ManyToOne
    @JsonBackReference("flight-ticket")
    private Flight flight;

}
