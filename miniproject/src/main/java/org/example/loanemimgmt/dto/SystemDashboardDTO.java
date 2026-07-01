package org.example.loanemimgmt.dto;

import java.math.BigDecimal;
import java.util.Map;

public record SystemDashboardDTO(
        long totalCustomers,
        long totalLoans,
        long activeLoans,
        long closedLoans,
        long overdueEMIs,
        BigDecimal totalEMICollected,
        BigDecimal totalPenaltyCollected,
        BigDecimal averageInterestRate,
        BigDecimal highestOutstandingLoan,
        Map<String, Object> highestPayingCustomer,
        long NPAAccounts
) {
}

