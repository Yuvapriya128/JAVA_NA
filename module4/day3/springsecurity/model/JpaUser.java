package org.example.springsecurity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class JpaUser {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String name;

    private String password;

    private String role;

}
