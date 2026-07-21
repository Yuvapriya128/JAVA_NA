package org.northernarc.assessment4.dto.update;

import jakarta.validation.constraints.*;

public record CustomerUpdateDto(

        @NotNull(message = "Customer id is required")
        Long customerId,

        @NotBlank(message = "Customer name is required")
        @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
        String customerName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
        String phoneNumber,

        @NotBlank(message = "Branch is required")
        @Size(min = 2, max = 100, message = "Branch must be between 2 and 100 characters")
        String branch,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,64}$",
                message = "Password must include uppercase, lowercase, number, and special character"
        )
        String password
) {
}

