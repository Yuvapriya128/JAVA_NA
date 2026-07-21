package org.northernarc.loanemi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDashboardDTO {
    private long totalCustomers;
    private long totalLoans;
    private long activeLoans;
    private long closedLoans;
    private long overdueEMIs;
    private double totalEMICollected;
    private double totalPenaltyCollected;
    private double averageInterestRate;
    private String highestOutstandingLoan;
    private String highestPayingCustomer;
    private long NPAAccounts;


}
