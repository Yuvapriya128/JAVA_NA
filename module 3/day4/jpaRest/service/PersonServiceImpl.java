package org.example.jpademo.service;


import org.example.jpademo.model.Person;
import org.example.jpademo.repository.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepo personRepo;

    @Override
    public Person savePerson(Person p) {

        return personRepo.save(p);
    }

    @Override
    public Person updateByIdPerson(int id, Person p) {

        Person person=personRepo.findById(id).get();

                person.setAge(p.getAge());
                person.setFname(p.getFname());
                person.setLname(p.getLname());
                person.setId(p.getId());

        return personRepo.save(person);
    }

    @Override
    public void deleteByIdPerson(int id) {

        personRepo.deleteById(id);
    }

    @Override
    public List<Person> findAllPerson() {

        return personRepo.findAll();
    }

    @Override
    public Person findByIdPerson(int id) {

        return personRepo.findById(id).get();
    }
}
