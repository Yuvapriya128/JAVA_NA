package org.northernarc.employeedemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @Positive(message = "Salary must be greater than 0")
    private double salary;
}
