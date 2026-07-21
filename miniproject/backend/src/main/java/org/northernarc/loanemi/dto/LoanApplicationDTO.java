package org.northernarc.loanemi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.northernarc.loanemi.enums.LoanApplicationStatus;
import org.northernarc.loanemi.enums.LoanType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationDTO {
    private Long applicationId;
    private Long customerId;
    private String customerName;
    private LoanType loanType;
    private Double principalAmount;
    private Integer tenureMonths;
    private Double annualInterestRate;
    private LoanApplicationStatus applicationStatus;
    private LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
    private LocalDateTime lastUpdatedAt;
    private String rejectionReason;
    
    // Loan linkage fields
    private Long loanId;
    private LocalDateTime loanCreatedAt;
}
