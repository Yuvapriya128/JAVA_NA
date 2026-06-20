package org.example.associationdemojpa.manyToOne.repository;

import org.example.associationdemojpa.manyToOne.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepo extends JpaRepository<Player,Integer> {
}
