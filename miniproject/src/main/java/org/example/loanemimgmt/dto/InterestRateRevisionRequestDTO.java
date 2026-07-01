package org.example.loanemimgmt.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record InterestRateRevisionRequestDTO(
        @NotEmpty List<String> loanTypes,
        @NotNull @DecimalMin(value = "0.00", inclusive = true) BigDecimal annualInterestRate
) {
}

