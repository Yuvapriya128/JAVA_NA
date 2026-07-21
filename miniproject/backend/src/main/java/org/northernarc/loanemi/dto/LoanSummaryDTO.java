package org.northernarc.loanemi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanSummaryDTO {
    private Long loanId;
    private String loanType;
    private Double principalAmount;
    private Double annualInterestRate;
    private Integer tenureMonths;
    private Double emiAmount;
    private String loanStatus;
    private Long customerId;
    private String customerName;
    private String city;

}
