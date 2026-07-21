package org.northernarc.loanemi.service;

import org.northernarc.loanemi.dto.CreateLoanRequest;
import org.northernarc.loanemi.dto.EmiCalculationRequestDTO;
import org.northernarc.loanemi.dto.EmiCalculationResponseDTO;
import org.northernarc.loanemi.dto.EmiPaymentReceiptDTO;
import org.northernarc.loanemi.dto.EmiPaymentRequestDTO;
import org.northernarc.loanemi.dto.EmiPaymentResponseDTO;
import org.northernarc.loanemi.dto.LoanDashboardDTO;
import org.northernarc.loanemi.dto.LoanSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

import org.northernarc.loanemi.model.EmiSchedule;

public interface LoanService {

    LoanSummaryDTO createLoan(CreateLoanRequest request);
    
    /**
     * Create loan with idempotency support.
     * @param request Loan creation request
     * @param idempotencyKey Optional idempotency key for duplicate detection
     * @param userId User ID for idempotency tracking
     * @return Loan summary (either new or cached if idempotent retry)
     */
    LoanSummaryDTO createLoanWithIdempotency(CreateLoanRequest request, String idempotencyKey, String userId);

    Page<LoanSummaryDTO> getLoans(int page, int size);

    LoanSummaryDTO getLoan(Long loanId);

    LoanDashboardDTO getDashboard();

    void recalculateOverduePenalties(LocalDate currentDate);

    EmiPaymentResponseDTO payEmi(EmiPaymentRequestDTO request);

    LoanSummaryDTO updateLoanInterest(Long loanId, Double rate);

    void deleteLoan(Long loanId);

    EmiPaymentReceiptDTO getPaymentReceipt(EmiSchedule emiSchedule);

    Page<LoanSummaryDTO> searchLoans(String loanId, String customerName, String loanType, String loanStatus,
                                     Double minInterestRate, Double maxInterestRate, Double minPrincipal,
                                     Double maxPrincipal, Integer tenure, int page, int size, String sort, Sort.Direction direction);

    /**
     * Calculate EMI without creating a loan.
     * This method performs EMI calculation based on provided parameters.
     * No data is persisted.
     *
     * @param request EmiCalculationRequestDTO containing principal, interest rate, and tenure
     * @return EmiCalculationResponseDTO with emi amount, total interest, and total payment
     */
    EmiCalculationResponseDTO calculateEmi(EmiCalculationRequestDTO request);
}
