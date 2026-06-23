package org.example.springjpa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PassengerRequestDTO {

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
