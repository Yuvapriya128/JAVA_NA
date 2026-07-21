package org.northernarc.loanemi.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmiPaymentResponseDTO {
    private Long emiId;
    private Double amountDue;
    private Double amountPaid;
    private Double penaltyAmount;
    private String status;
    private Integer daysPastDue;
    private LocalDate paymentDate;
}
