package org.example.jpademo.repository;

import org.example.jpademo.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepo extends JpaRepository<Person,Integer> {

}
