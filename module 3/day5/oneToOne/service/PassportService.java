package org.example.associationdemojpa.oneToOne.service;

import org.example.associationdemojpa.oneToOne.entity.Passport;

import java.util.List;

public interface PassportService {

    Passport addPassport(Passport passport);

    List<Passport> getAllPassports();

    Passport getPassportById(Long passportNumber);

    Passport updatePassport(Long passportNumber, Passport passport);

    void deletePassport(Long passportNumber);

}
