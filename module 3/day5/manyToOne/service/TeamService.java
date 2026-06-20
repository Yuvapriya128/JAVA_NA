package org.example.associationdemojpa.manyToOne.service;

import org.example.associationdemojpa.manyToOne.entity.Team;

import java.util.List;

public interface TeamService {

    Team addTeam(Team team);
    List<Team> getAllTeams();
    Team getTeamById(Integer id);
    void deleteTeamById(Integer id);
    Team updateTeamById(Integer id, Team team);
}
