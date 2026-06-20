package org.example.associationdemojpa.oneToOne.controller;

import org.example.associationdemojpa.oneToOne.entity.Passport;
import org.example.associationdemojpa.oneToOne.service.PassportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
while checking for update: do not add any generated value fields ever
give the correct json format
* */
@RestController
@RequestMapping("/api/onetoone/passport")
public class PassportController {

    @Autowired
    private PassportService passportService;

    @GetMapping
    public ResponseEntity<List<Passport>> findAll(){
        return ResponseEntity.ok(passportService.getAllPassports());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Passport> findbyid(@PathVariable long id){
        return ResponseEntity.ok(passportService.getPassportById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id){
        passportService.deletePassport(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping
    public ResponseEntity<Passport> save(@RequestBody Passport passport){
        return ResponseEntity.status(201).body(passportService.addPassport(passport));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Passport> update(@PathVariable long id,@RequestBody Passport passport){

        return ResponseEntity.ok(passportService.updatePassport(id,passport));
    }




}
