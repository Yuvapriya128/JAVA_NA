package org.northernarc.assessment4.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AccountRequestDto(
        @NotBlank(message = "Account number is required")
        String accountNumber,

        @Size(max = 50, message = "Account type must not exceed 50 characters")
        String accountType,

        @NotNull(message = "Balance is required")
        @PositiveOrZero(message = "Balance must be zero or positive")
        Double balance,

        @NotNull(message = "Customer id is required")
        Long customerId
) {
}

