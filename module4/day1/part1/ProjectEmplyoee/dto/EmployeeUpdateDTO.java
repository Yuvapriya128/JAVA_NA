package org.example.springdatajpademo.ProjectEmplyoee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeUpdateDTO {

    private Long id;
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "designation is required")
    private String designation;
    @NotBlank(message = "department is required")
    private String department;
    @Email(message = "Invalid email")
    private String email;
}
