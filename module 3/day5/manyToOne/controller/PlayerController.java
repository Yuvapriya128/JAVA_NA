package org.example.associationdemojpa.manyToOne.controller;


import org.example.associationdemojpa.manyToOne.entity.Player;

import org.example.associationdemojpa.manyToOne.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/otm/player")
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<Player>> findall(){
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> findbyid(@PathVariable int id){
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByid(@PathVariable int id){
        playerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Player> save(@RequestBody Player player ){
        return ResponseEntity.status(201).body(playerService.addPlayer(player));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> update(@PathVariable int id, @RequestBody Player player){
        return ResponseEntity.ok(playerService.updatePlayer(id,player));
    }
}
