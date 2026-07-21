package org.northernarc.week5_assess.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    @NotBlank
    private String accountNumber;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal openingBalance;

    @NotBlank
    private String accountType;

    @NotNull
    private Long customerId;
}

