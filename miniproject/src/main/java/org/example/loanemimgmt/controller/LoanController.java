package org.example.loanemimgmt.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.loanemimgmt.dto.EmiPaymentRequestDTO;
import org.example.loanemimgmt.dto.InterestRateRevisionRequestDTO;
import org.example.loanemimgmt.dto.LoanDashboardDTO;
import org.example.loanemimgmt.dto.LoanSummaryDTO;
import org.example.loanemimgmt.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/loans")
@Tag(name = "Loans", description = "Loan, EMI, and analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @Operation(summary = "List loans", description = "Returns paginated loans sorted by principal amount desc")
    public Page<LoanSummaryDTO> getLoans(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return loanService.getLoans(page, size);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Loan dashboard", description = "Returns per-loan dashboard cards")
    public List<LoanDashboardDTO> getLoanDashboards() {
        return loanService.getLoanDashboards();
    }

    @GetMapping("/zero-overdue")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Loans without overdue EMIs")
    public List<LoanSummaryDTO> getLoansWithZeroOverdue() {
        return loanService.getLoansWithZeroOverdueEmis();
    }

    @GetMapping("/collections-by-city")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "EMI collections by city")
    public List<Map<String, String>> getCollectionsByCity() {
        return loanService.getTotalEmiCollectionByCity();
    }

    @GetMapping("/analytics/customers-overdue")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Customers with overdue EMIs")
    public List<Map<String, String>> getCustomersWithOverdueEmis() {
        return loanService.getCustomersWithOverdueEmis();
    }

    @GetMapping("/analytics/highest-overdue")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Highest overdue EMI")
    public Optional<Map<String, String>> getHighestOverdueEmi() {
        return loanService.getHighestOverdueEmi();
    }

    @GetMapping("/analytics/latest-payment")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Latest EMI payment")
    public Optional<Map<String, String>> getLatestPayment() {
        return loanService.getLatestPayment();
    }

    @GetMapping("/top-defaulters")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Top defaulters")
    public List<Map<String, String>> getTopDefaulters(@RequestParam(defaultValue = "5") int limit) {
        return loanService.getTopDefaulters(limit);
    }

    @PostMapping("/{loanId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Approve loan", description = "Approves loan and generates EMI schedule")
    @ApiResponse(responseCode = "200", description = "Loan approved")
    @ApiResponse(responseCode = "400", description = "Business rule violation")
    public LoanSummaryDTO approveLoan(@PathVariable Long loanId) {
        logger.info("API call: Approve Loan - Loan ID: {}", loanId);
        return loanService.approveLoan(loanId);
    }

    @PostMapping("/emis/{emiId}/payments")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @Operation(summary = "Record EMI payment")
    @ApiResponse(responseCode = "200", description = "Payment recorded")
    @ApiResponse(responseCode = "400", description = "Invalid payment")
    public LoanSummaryDTO recordPayment(@PathVariable Long emiId,
                                        @Valid @RequestBody EmiPaymentRequestDTO request) {
        logger.info("API call: Record EMI Payment - EMI ID: {}, Amount: {}", emiId, request.amount());
        return loanService.recordEmiPayment(emiId, request);
    }

    @PatchMapping("/interest-rate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revise annual interest rate by loan type")
    public Map<String, Integer> reviseInterestRate(@Valid @RequestBody InterestRateRevisionRequestDTO request) {
        logger.info("API call: Revise Interest Rate - Loan Types: {}, New Rate: {}", request.loanTypes(), request.annualInterestRate());
        int updated = loanService.reviseAnnualInterestRates(request.loanTypes(), request.annualInterestRate());
        return Map.of("updatedLoans", updated);
    }
}

