package org.example.springdatajpademo.ProjectEmplyoee.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mtm_project_employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private String designation;
    private String department;
    @Email
    private String email;

    @ManyToMany(mappedBy = "employeeList")
    @JsonIgnore
//    @JsonIgnore or use JsonManagedReference here and JsonBackReference in other
//    This reference json will not work for mtm
     private List<Project> project;

}
