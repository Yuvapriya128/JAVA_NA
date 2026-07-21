package org.northernarc.loanemi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.northernarc.loanemi.dto.ReportDTO;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.model.Loan;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Report generation APIs")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;

    public ReportController(CustomerRepository customerRepository, LoanRepository loanRepository) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/collection-report")
    @Operation(summary = "Get collection report")
    public Page<ReportDTO> getCollectionReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Collection report requested startDate={} endDate={}", startDate, endDate);
        Pageable pageable = PageRequest.of(page, size);
        List<ReportDTO> reports = List.of(
                new ReportDTO("COLLECTION_REPORT", LocalDateTime.now(), new HashMap<>(), 1L)
        );
        return new PageImpl<>(reports, pageable, reports.size());
    }

    @GetMapping("/loan-report")
    @Operation(summary = "Get loan report")
    public Page<ReportDTO> getLoanReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String loanType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Loan report requested loanType={} status={}", loanType, status);
        Pageable pageable = PageRequest.of(page, size);
        List<ReportDTO> reports = List.of(
                new ReportDTO("LOAN_REPORT", LocalDateTime.now(), new HashMap<>(), 1L)
        );
        return new PageImpl<>(reports, pageable, reports.size());
    }

    @GetMapping("/customer-report")
    @Operation(summary = "Get customer report")
    public Page<ReportDTO> getCustomerReport(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer creditScoreMin,
            @RequestParam(required = false) Integer creditScoreMax,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Customer report requested active={} city={}", active, city);
        Pageable pageable = PageRequest.of(page, size);
        List<ReportDTO> reports = List.of(
                new ReportDTO("CUSTOMER_REPORT", LocalDateTime.now(), new HashMap<>(), 1L)
        );
        return new PageImpl<>(reports, pageable, reports.size());
    }

    @GetMapping("/emi-report")
    @Operation(summary = "Get EMI report")
    public Page<ReportDTO> getEmiReport(
            @RequestParam(required = false) String loanId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("EMI report requested loanId={} status={}", loanId, status);
        Pageable pageable = PageRequest.of(page, size);
        List<ReportDTO> reports = List.of(
                new ReportDTO("EMI_REPORT", LocalDateTime.now(), new HashMap<>(), 1L)
        );
        return new PageImpl<>(reports, pageable, reports.size());
    }

    @GetMapping("/penalty-report")
    @Operation(summary = "Get penalty report")
    public Page<ReportDTO> getPenaltyReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Double amountMin,
            @RequestParam(required = false) Double amountMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Penalty report requested startDate={} endDate={}", startDate, endDate);
        Pageable pageable = PageRequest.of(page, size);
        List<ReportDTO> reports = List.of(
                new ReportDTO("PENALTY_REPORT", LocalDateTime.now(), new HashMap<>(), 1L)
        );
        return new PageImpl<>(reports, pageable, reports.size());
    }

    @GetMapping("/overdue-report")
    @Operation(summary = "Get overdue EMI report")
    public Page<ReportDTO> getOverdueReport(
            @RequestParam(defaultValue = "true") boolean overdueOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Overdue report requested overdueOnly={}", overdueOnly);
        Pageable pageable = PageRequest.of(page, size);
        List<ReportDTO> reports = List.of(
                new ReportDTO("OVERDUE_REPORT", LocalDateTime.now(), new HashMap<>(), 1L)
        );
        return new PageImpl<>(reports, pageable, reports.size());
    }

    @GetMapping("/dashboard-summary")
    @Operation(summary = "Get dashboard summary report")
    public ReportDTO getDashboardSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Dashboard summary report requested startDate={} endDate={}", startDate, endDate);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCustomers", customerRepository.count());
        summary.put("totalLoans", loanRepository.count());
        summary.put("activeCustomers", customerRepository.findAll().stream().filter(Customer::isActive).count());
        summary.put("activeLoans", loanRepository.findAll().stream()
                .filter(l -> "ACTIVE".equals(l.getLoanStatus())).count());
        
        return new ReportDTO("DASHBOARD_SUMMARY", LocalDateTime.now(), summary, 1L);
    }
}
