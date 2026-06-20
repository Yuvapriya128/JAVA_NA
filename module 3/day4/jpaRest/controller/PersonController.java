package org.example.jpademo.controller;


import org.example.jpademo.model.Person;
import org.example.jpademo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personsjpa")
public class PersonController {
    @Autowired
    public PersonService personService;

    @GetMapping("/{id}")
    public ResponseEntity<Person> getById(@PathVariable int id){
        return ResponseEntity.ok(personService.findByIdPerson(id));
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAll(){

        return ResponseEntity.ok(personService.findAllPerson());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id){
        personService.deleteByIdPerson(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/add")
    public ResponseEntity<Person> add(@RequestBody Person p){
        return ResponseEntity.status(201).body(personService.savePerson(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable int id,@RequestBody Person p){
        personService.updateByIdPerson(id,p);
        return ResponseEntity.ok(p);
    }


}
