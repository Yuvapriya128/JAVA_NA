package org.example.associationdemojpa.oneToOne.repository;

import org.example.associationdemojpa.oneToOne.entity.Passport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassportRepo extends JpaRepository<Passport,Long> {
}
