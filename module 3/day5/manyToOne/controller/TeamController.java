package org.example.associationdemojpa.manyToOne.controller;

import org.example.associationdemojpa.manyToOne.entity.Team;
import org.example.associationdemojpa.manyToOne.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/otm/team")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @GetMapping
    public ResponseEntity<List<Team>> findall(){
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> findbyid(@PathVariable int id){
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByid(@PathVariable int id){
        teamService.deleteTeamById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Team> save(@RequestBody Team team ){
        return ResponseEntity.status(201).body(teamService.addTeam(team));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> update(@PathVariable int id, @RequestBody Team team){
        return ResponseEntity.ok(teamService.updateTeamById(id,team));
    }
}
