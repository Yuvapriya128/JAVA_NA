package org.example.springjpa.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "travels_passenger")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("customerid")
    private Integer id;
    private String name;
    private String email;
    private String phoneno;

    @OneToMany(mappedBy = "passenger",cascade = CascadeType.ALL)
    @JsonManagedReference("passenger-ticket")
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "passenger",cascade = CascadeType.ALL)
    @JsonManagedReference("passenger-luggage")
    private List<Luggage> luggageList;

}
