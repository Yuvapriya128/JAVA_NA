package org.example.associationdemojpa.manyToOne.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "otmjpateam")
public class Team {

    @Id
    @GeneratedValue
    private Integer id;
    private String teamName;

    @OneToMany(mappedBy = "team",cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Player> playerList;

    public Team() {
    }

    public Team(String teamName, List<Player> playerList) {
        this.teamName = teamName;
        this.playerList = playerList;
    }

    public Team(Integer id, String teamName, List<Player> playerList) {
        this.id = id;
        this.teamName = teamName;
        this.playerList = playerList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }
}
