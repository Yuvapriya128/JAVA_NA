package org.example.loanemimgmt.dto;

import org.example.loanemimgmt.enums.LoanStatus;
import org.example.loanemimgmt.enums.LoanType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanSummaryDTO(
        Long loanId,
        Long customerId,
        String customerName,
        LoanType loanType,
        BigDecimal principalAmount,
        BigDecimal annualInterestRate,
        Integer tenureMonths,
        BigDecimal emiAmount,
        LocalDate disbursementDate,
        LoanStatus loanStatus,
        Long overdueEmiCount,
        BigDecimal outstandingPrincipal
) {
}

