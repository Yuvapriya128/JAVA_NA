package org.example.associationdemojpa.manyToOne.repository;

import org.example.associationdemojpa.manyToOne.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepo extends JpaRepository<Team,Integer> {
}
