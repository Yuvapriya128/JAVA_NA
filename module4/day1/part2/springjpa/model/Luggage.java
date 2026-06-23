package org.example.springjpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "travels_luggage")
public class Luggage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("luggageid")
    private Integer id;
    private double weight;
    private double fare;

    @ManyToOne
    @JsonBackReference("passenger-luggage")
    private Passenger passenger;

    @ManyToOne
    @JsonBackReference("flight-luggage")
    private Flight flight;

}
