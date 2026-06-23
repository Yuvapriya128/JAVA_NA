package org.example.springdatajpademo.ProjectEmplyoee.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProjectRequestDTO {
    @Column(unique = true)
    private String name;
}
