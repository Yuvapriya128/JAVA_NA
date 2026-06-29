package org.example.springsecurity.repo;

import org.example.springsecurity.model.JpaUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepo extends JpaRepository<JpaUser,Integer> {
    public JpaUser findByName(String name);
}
