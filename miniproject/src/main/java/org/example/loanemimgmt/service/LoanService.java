package org.example.loanemimgmt.service;

import org.example.loanemimgmt.dto.EmiPaymentRequestDTO;
import org.example.loanemimgmt.dto.LoanDashboardDTO;
import org.example.loanemimgmt.dto.LoanSummaryDTO;
import org.example.loanemimgmt.dto.SystemDashboardDTO;
import org.example.loanemimgmt.enums.LoanType;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoanService {

    LoanSummaryDTO approveLoan(Long loanId);

    LoanSummaryDTO recordEmiPayment(Long emiId, EmiPaymentRequestDTO request);

    Page<LoanSummaryDTO> getLoans(int page, int size);

    List<LoanDashboardDTO> getLoanDashboards();

    List<LoanSummaryDTO> getLoansWithZeroOverdueEmis();

    List<Map<String, String>> getTotalEmiCollectionByCity();

    List<Map<String, String>> getCustomersWithOverdueEmis();

    Optional<Map<String, String>> getHighestOverdueEmi();

    Optional<Map<String, String>> getLatestPayment();

    List<Map<String, String>> getTopDefaulters(int limit);

    int reviseAnnualInterestRates(List<LoanType> loanTypes, BigDecimal newAnnualRate);

    SystemDashboardDTO getSystemDashboard();
}

