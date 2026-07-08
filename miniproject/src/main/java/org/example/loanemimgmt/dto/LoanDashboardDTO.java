package org.example.loanemimgmt.dto;

import org.example.loanemimgmt.enums.LoanStatus;
import org.example.loanemimgmt.enums.LoanType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanDashboardDTO(
        Long loanId,
        String customerName,
        String city,
        LoanType loanType,
        LoanStatus loanStatus,
        BigDecimal principalAmount,
        BigDecimal emiAmount,
        Long overdueEmiCount,
        BigDecimal outstandingPrincipal,
        BigDecimal totalOverdueAmount,
        LocalDate latestPaymentDate
) {
}

