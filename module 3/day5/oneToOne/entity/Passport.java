package org.example.associationdemojpa.oneToOne.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "onepassportjpa")
public class Passport {
    @Id
    @GeneratedValue
    private Long id;


    //    removes the person also
    @OneToOne(cascade = CascadeType.ALL)

//    Person will NOT appear.
//Actually JsonIgnore is hiding it.

//   @JsonIgnore
//    with this, it returns only passport properties, not the person associated with it
//    without this, it keeps repeating person & passport in one key value pair

//    Hibernate will create a join column automatically.
    @JoinColumn(name = "person_id")

    @JsonManagedReference
    private Person person;

    public Passport() {
    }

    public Passport(Long id, Person person) {
        this.id = id;
        this.person = person;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
