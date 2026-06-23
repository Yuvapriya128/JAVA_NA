package org.example.springjpa.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Entity
@Data
@Table(name = "travels_flight")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("flightid")
    private Integer id;
    private String src;
    private String dest;
    private Date dod;
    private Date doa;
    private Time tod;
    private Time toa;


    @OneToMany(mappedBy = "flight",cascade = CascadeType.ALL)
    @JsonManagedReference("flight-ticket")
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "flight",cascade = CascadeType.ALL)
    @JsonManagedReference("flight-luggage")
    private List<Luggage> luggageList;


}
