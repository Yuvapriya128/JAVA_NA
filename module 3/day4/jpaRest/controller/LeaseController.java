package org.example.jpademo.controller;

import org.example.jpademo.model.Lease;

import org.example.jpademo.repository.LeaseRepo;
import org.example.jpademo.service.LeaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leasejpa")
public class LeaseController {
    @Autowired
    LeaseService leaseService;

    @GetMapping
    public ResponseEntity<List<Lease>> findall(){
        return ResponseEntity.ok(leaseService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Lease> findByid(@PathVariable int id){
        return ResponseEntity.ok(leaseService.findById(id));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id){
        leaseService.deleteById(id);
        return  ResponseEntity.noContent().build();
    }
    @PostMapping
    public ResponseEntity<Lease> save(@RequestBody Lease lease){
        return ResponseEntity.status(201).body(leaseService.save(lease));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lease> update(@RequestBody Lease lease,@PathVariable int id){
        return ResponseEntity.ok(leaseService.updateById(id,lease));
    }

}
