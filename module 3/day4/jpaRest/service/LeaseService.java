package org.example.jpademo.service;

import org.example.jpademo.model.Lease;
import org.example.jpademo.model.Product;

import java.util.List;

public interface LeaseService {
    Lease save(Lease lease);
    void deleteById(int id);
    Lease findById(int id);
    List<Lease> findAll();
    Lease updateById(int id,Lease lease);
}
