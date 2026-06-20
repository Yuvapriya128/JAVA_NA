package org.example.associationdemojpa.manyToOne.service;

import org.example.associationdemojpa.manyToOne.entity.Player;
import org.example.associationdemojpa.manyToOne.entity.Team;
import org.example.associationdemojpa.manyToOne.repository.PlayerRepo;
import org.example.associationdemojpa.manyToOne.repository.TeamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService{

   @Autowired
   private PlayerRepo playerRepo;
    @Autowired
    private TeamRepo teamRepo;

    @Override
    public Player addPlayer(Player player) {
        Integer teamid=player.getTeam().getId();
        Team team=teamRepo.findById(teamid).orElseThrow(()->new RuntimeException("Team not found"));

        player.setTeam(team);

        return playerRepo.save(player);
    }

    @Override
    public List<Player> getAllPlayers() {
        return playerRepo.findAll();
    }

    @Override
    public Player getPlayerById(Integer id) {
        return playerRepo.findById(id).get();
    }

    @Override
    public void deleteById(Integer id) {
        playerRepo.deleteById(id);

    }

    @Override
    public Player updatePlayer(Integer id, Player player) {
        Player player1=playerRepo.findById(id).orElseThrow(()->new RuntimeException("Team not found"));

        Integer teamid=player.getTeam().getId();
        Team team=teamRepo.findById(teamid).orElseThrow(()->new RuntimeException("Team not found"));

        player1.setTeam(team);

        player1.setAge(player.getAge());
        player1.setName(player.getName());

        return playerRepo.save(player1);

    }
}
