package org.example.personspringdemo.service;

import org.example.personspringdemo.entity.Person;

import java.util.List;

public interface PersonService {
    Person savePerson(Person p);
    Person updateByIdPerson(int id,Person p);
    void deleteByIdPerson(int id);
    List<Person> findAllPerson();
    Person findByIdPerson(int id);
}
