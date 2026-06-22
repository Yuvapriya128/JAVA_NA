package org.example.springdatajpademo.ProjectEmplyoee.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "mtmProject")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String name;
/*@OneToMany(
    cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE,
        CascadeType.REFRESH
    }
)
private List<Employee> employees;
*/
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Employee> employeeList;
}
