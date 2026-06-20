package org.example.associationdemojpa.oneToOne.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "onepersonjpa")
public class Person {

    @Id
    @GeneratedValue
//    Id should be in object class not primitive
    private Integer id;
    private String fname;
    private String lname;

//    mappedBy must refer to the field name in Passport, not the table name.

    @OneToOne(mappedBy = "person")
    @JsonBackReference
    private Passport passport;

//    jpa requires no arg constructor
    public Person() {
    }

    public Person(Integer id, String fname, String lname, Passport passport) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.passport = passport;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public Passport getPassport() {
        return passport;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
    }
}
