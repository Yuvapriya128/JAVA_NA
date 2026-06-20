package org.example.personspringdemo.dao;

import org.example.personspringdemo.entity.Person;

import java.util.List;

public interface PersonDao {
    Person save(Person p);
    Person updateById(int id,Person p);
    void deleteById(int id);
    List<Person> findAll();
    Person findById(int id);
}
