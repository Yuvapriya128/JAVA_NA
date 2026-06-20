package org.example.personspringdemo.service;

import org.example.personspringdemo.dao.PersonDao;
import org.example.personspringdemo.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonDao personDao;

    @Override
    public Person savePerson(Person p) {
        return personDao.save(p);
    }

    @Override
    public Person updateByIdPerson(int id, Person p) {
         return personDao.updateById(id,p);
    }

    @Override
    public void deleteByIdPerson(int id) {
       personDao.deleteById(id);
    }

    @Override
    public List<Person> findAllPerson() {
        return personDao.findAll();
    }

    @Override
    public Person findByIdPerson(int id) {
        return personDao.findById(id);
    }
}
