package org.example.loanemimgmt.dto;

import org.example.loanemimgmt.enums.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanDashboardDTO(
        Long loanId,
        String customerName,
        String city,
        String loanType,
        LoanStatus loanStatus,
        BigDecimal principalAmount,
        BigDecimal emiAmount,
        Long overdueEmiCount,
        BigDecimal outstandingPrincipal,
        BigDecimal totalOverdueAmount,
        LocalDate latestPaymentDate
) {
}

