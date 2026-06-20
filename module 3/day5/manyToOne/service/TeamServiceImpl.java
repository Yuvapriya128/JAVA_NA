package org.example.associationdemojpa.manyToOne.service;

import org.example.associationdemojpa.manyToOne.entity.Team;
import org.example.associationdemojpa.manyToOne.repository.TeamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService{
    @Autowired
    private TeamRepo teamRepo;

    @Override
    public Team addTeam(Team team) {
        return teamRepo.save(team);
    }

    @Override
    public List<Team> getAllTeams() {
        return teamRepo.findAll();
    }

    @Override
    public Team getTeamById(Integer id) {
        return teamRepo.findById(id).get();
    }

    @Override
    public void deleteTeamById(Integer id) {
        teamRepo.deleteById(id);

    }

    @Override
    public Team updateTeamById(Integer id, Team team) {

        Team team1=teamRepo.findById(id).get();
       team1.setTeamName(team.getTeamName());
       team1.setPlayerList(team.getPlayerList());

        return teamRepo.save(team1);
    }
}
