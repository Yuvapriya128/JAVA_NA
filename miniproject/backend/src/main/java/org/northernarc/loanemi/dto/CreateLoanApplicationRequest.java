package org.northernarc.loanemi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.northernarc.loanemi.enums.LoanType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoanApplicationRequest {
    
    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    private Long customerId;

    @Positive(message = "Principal amount must be positive")
    private Double principalAmount;

    @Positive(message = "Tenure must be positive")
    private Integer tenureMonths;

    private Double annualInterestRate;
}
