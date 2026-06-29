package org.example.jwtintro.repo;

import org.example.jwtintro.model.JpaUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepo extends JpaRepository<JpaUser,Integer> {
   public JpaUser findByUsername(String user);
}
