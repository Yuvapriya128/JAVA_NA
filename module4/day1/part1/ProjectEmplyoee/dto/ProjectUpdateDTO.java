package org.example.springdatajpademo.ProjectEmplyoee.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectUpdateDTO {
    private Integer id;
    @Column(unique = true)
    private String name;
}
