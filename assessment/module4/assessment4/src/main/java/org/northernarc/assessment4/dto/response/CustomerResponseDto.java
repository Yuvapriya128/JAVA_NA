package org.northernarc.assessment4.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CustomerResponseDto(
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

        @NotNull(message = "Account numbers are required")
        List<Long> accountNumbers
) {
}

