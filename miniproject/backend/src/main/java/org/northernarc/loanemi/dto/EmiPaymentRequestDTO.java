package org.northernarc.loanemi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmiPaymentRequestDTO {

    @Positive
    private Double amount;

    @NotBlank
    private String paymentMode;

    @NotBlank
    private String referenceNumber;

    @NotNull
    private Long emiId;

}
