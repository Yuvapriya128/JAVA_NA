package org.example.associationdemojpa.oneToOne.repository;

import org.example.associationdemojpa.oneToOne.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepo extends JpaRepository<Person,Integer> {
}
