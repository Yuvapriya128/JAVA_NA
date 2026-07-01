package org.example.loanemimgmt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank @Size(max = 100) String customerName,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(min = 6, max = 120) String password,
        @NotBlank @Size(min = 10, max = 15) String phoneNumber,
        @NotBlank @Size(max = 100) String city,
        @Min(300) @Max(900) Integer creditScore
) {
}

