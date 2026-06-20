package org.example.jpademo.repository;

import org.example.jpademo.model.Lease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaseRepo extends JpaRepository<Lease, Integer> {
}
