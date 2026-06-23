package org.example.springjpa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PassengerUpdateDTO {
    private Integer id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Name is required")
    private String email;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number is invalid"
    )
    private String phoneno;
}
