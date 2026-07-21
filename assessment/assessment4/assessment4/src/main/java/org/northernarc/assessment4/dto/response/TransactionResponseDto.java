package org.northernarc.assessment4.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TransactionResponseDto(
        @NotNull(message = "Transaction id is required")
        Long transactionId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        Double amount,

        @NotBlank(message = "Transaction type is required")
        @Size(max = 30, message = "Transaction type must not exceed 30 characters")
        String transactionType,

        @NotNull(message = "Transaction date is required")
        LocalDate transactionDate,

        @NotNull(message = "Account number is required")
        Long accountNumber
) {
}

