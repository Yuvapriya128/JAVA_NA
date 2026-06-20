package org.example.associationdemojpa.manyToOne.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "mtojpaplayer")
public class Player {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private int age;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    private Team team;

    public Player() {
    }

    public Player(String name, int age, Team team) {
        this.name = name;
        this.age = age;
        this.team = team;
    }

    public Player(Integer id, String name, int age, Team team) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.team = team;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
