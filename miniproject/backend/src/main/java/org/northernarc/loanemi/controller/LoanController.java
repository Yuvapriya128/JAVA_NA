package org.northernarc.loanemi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.northernarc.loanemi.dto.CreateLoanRequest;
import org.northernarc.loanemi.dto.EmiCalculationRequestDTO;
import org.northernarc.loanemi.dto.EmiCalculationResponseDTO;
import org.northernarc.loanemi.dto.EmiPaymentRequestDTO;
import org.northernarc.loanemi.dto.EmiPaymentResponseDTO;
import org.northernarc.loanemi.dto.EmiPaymentHistoryDTO;
import org.northernarc.loanemi.dto.EmiPaymentReceiptDTO;
import org.northernarc.loanemi.dto.LoanApplicationRequestDTO;
import org.northernarc.loanemi.dto.LoanApplicationResponseDTO;
import org.northernarc.loanemi.dto.LoanDashboardDTO;
import org.northernarc.loanemi.dto.LoanSummaryDTO;
import org.northernarc.loanemi.enums.LoanType;
import org.northernarc.loanemi.model.EmiPayment;
import org.northernarc.loanemi.model.EmiSchedule;
import org.northernarc.loanemi.model.Loan;
import org.northernarc.loanemi.repository.EmiPaymentRepository;
import org.northernarc.loanemi.repository.EmiScheduleRepository;
import org.northernarc.loanemi.repository.LoanRepository;
import org.northernarc.loanemi.service.LoanService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
@Tag(name = "Loans", description = "Loan and EMI APIs")
public class LoanController {
    private static final Logger log = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;
    private final LoanRepository loanRepository;
    private final EmiPaymentRepository emiPaymentRepository;
    private final EmiScheduleRepository emiScheduleRepository;

    public LoanController(LoanService loanService, LoanRepository loanRepository,
                         EmiPaymentRepository emiPaymentRepository,
                         EmiScheduleRepository emiScheduleRepository) {
        this.loanService = loanService;
        this.loanRepository = loanRepository;
        this.emiPaymentRepository = emiPaymentRepository;
        this.emiScheduleRepository = emiScheduleRepository;
    }

    @GetMapping("/loan-products")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get all available loan products")
    public List<LoanProductDTO> getLoanProducts() {
        log.info("Get loan products requested");
        return Arrays.stream(LoanType.values())
                .map(lt -> new LoanProductDTO(lt.name(), lt.getDisplayName(), lt.getDefaultRate()))
                .collect(Collectors.toList());
    }

    @PostMapping("/loans")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Create loan and generate EMI schedule",
            description = "Create a new loan. Supports optional applicationId for linking to approved applications. " +
                    "Use Idempotency-Key header to prevent duplicate loan creation.")
    public LoanSummaryDTO createLoan(
            @Valid @RequestBody CreateLoanRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            Authentication authentication) {
        log.info("Create loan requested for customerId={} loanType={} principal={} tenureMonths={} applicationId={} idempotencyKey={}",
                request.getCustomerId(), request.getLoanType(), request.getPrincipalAmount(), 
                request.getTenureMonths(), request.getApplicationId(), 
                idempotencyKey != null ? idempotencyKey.substring(0, Math.min(8, idempotencyKey.length())) + "..." : "null");
        
        String userId = authentication != null ? authentication.getName() : "system";
        
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            return loanService.createLoanWithIdempotency(request, idempotencyKey, userId);
        }
        return loanService.createLoan(request);
    }

    @PostMapping("/loans/calculate-emi")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Calculate EMI without creating a loan",
            description = "Calculate EMI amount, total interest, and total payment based on provided principal, interest rate, and tenure. No data is persisted.")
    public EmiCalculationResponseDTO calculateEmi(@Valid @RequestBody EmiCalculationRequestDTO request) {
        log.info("EMI calculation requested for principal={} annualInterestRate={} tenureMonths={}",
                request.getPrincipalAmount(), request.getAnnualInterestRate(), request.getTenureMonths());
        return loanService.calculateEmi(request);
    }

    @GetMapping("/loans")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get loans with pagination and sorting")
    public Page<LoanSummaryDTO> getLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get loans requested page={} size={}", page, size);
        return loanService.getLoans(page, size);
    }

    @GetMapping("/loans/{loanId}")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get loan by ID")
    public LoanSummaryDTO getLoan(@PathVariable Long loanId) {
        log.info("Get loan requested for loanId={}", loanId);
        return loanService.getLoan(loanId);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Get dashboard summary")
    public LoanDashboardDTO getDashboard() {
        log.info("Dashboard summary requested");
        return loanService.getDashboard();
    }

    @PatchMapping("/loans/interest-rate")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Bulk update interest rate by loan types")
    public int reviseInterestRates(
            @RequestParam List<String> loanTypes,
            @RequestParam Double annualInterestRate) {
        log.info("Bulk interest-rate revision requested loanTypes={} annualInterestRate={}", loanTypes, annualInterestRate);
        int totalUpdated = 0;
        for (String loanTypeStr : loanTypes) {
            LoanType loanType = LoanType.valueOf(loanTypeStr.toUpperCase());
            totalUpdated += loanRepository.updateInterestRateByLoanType(loanType, annualInterestRate);
        }
        log.info("Bulk interest-rate revision completed totalUpdated={}", totalUpdated);
        return totalUpdated;
    }

    @PutMapping("/loans/{loanId}/interest")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Update a loan interest rate")
    public LoanSummaryDTO updateLoanInterest(@PathVariable Long loanId, @RequestParam Double rate) {
        log.info("Update interest rate requested for loanId={} rate={}", loanId, rate);
        return loanService.updateLoanInterest(loanId, rate);
    }

    @DeleteMapping("/loans/{loanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete loan by ID")
    public void deleteLoan(@PathVariable Long loanId) {
        log.info("Delete loan requested for loanId={}", loanId);
        loanService.deleteLoan(loanId);
    }

    @PostMapping("/emis/pay")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Pay EMI (supports partial payment)")
    public EmiPaymentResponseDTO payEmi(@Valid @RequestBody EmiPaymentRequestDTO request) {
        log.info("Pay EMI requested for emiId={} amount={} paymentMode={}",
                request.getEmiId(), request.getAmount(), request.getPaymentMode());
        return loanService.payEmi(request);
    }

    @GetMapping("/emis/payments")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get EMI payment history with pagination")
    public Page<EmiPaymentHistoryDTO> getEmiPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get EMI payment history requested page={} size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));
        return emiPaymentRepository.findLatestPaymentPage(pageable)
                .map(payment -> mapToEmiPaymentHistoryDTO(payment));
    }

    @GetMapping("/emis/payments/{emiId}/receipt")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get EMI payment receipt")
    public EmiPaymentReceiptDTO getPaymentReceipt(@PathVariable Long emiId) {
        log.info("Get payment receipt requested for emiId={}", emiId);
        EmiSchedule emiSchedule = resolveEmiSchedule(emiId);
        if (emiSchedule == null) {
            throw new RuntimeException("EMI Schedule not found: " + emiId);
        }
        
        return loanService.getPaymentReceipt(emiSchedule);
    }

    private EmiSchedule resolveEmiSchedule(Long requestedEmiId) {
        EmiSchedule direct = emiScheduleRepository.findById(requestedEmiId).orElse(null);
        if (direct != null) {
            return direct;
        }
        if (requestedEmiId != null && requestedEmiId >= 1000) {
            long candidateLoanId = requestedEmiId / 100;
            int candidateInstallmentNumber = (int) (requestedEmiId % 100);
            if (candidateInstallmentNumber > 0) {
                Loan loan = loanRepository.findById(candidateLoanId).orElse(null);
                if (loan != null && loan.getEmiSchedules() != null) {
                    EmiSchedule mapped = loan.getEmiSchedules().stream()
                            .filter(e -> e.getInstallmentNumber() != null
                                    && e.getInstallmentNumber() == candidateInstallmentNumber)
                            .findFirst()
                            .orElse(null);
                    if (mapped != null) {
                        log.info("Resolved composite EMI id {} to emiId={} for receipt lookup",
                                requestedEmiId, mapped.getEmiId());
                    }
                    return mapped;
                }
            }
        }
        return null;
    }

    @GetMapping("/loans/search")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Search loans with filters")
    public Page<LoanSummaryDTO> searchLoans(
            @RequestParam(required = false) String loanId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String loanType,
            @RequestParam(required = false) String loanStatus,
            @RequestParam(required = false) Double minInterestRate,
            @RequestParam(required = false) Double maxInterestRate,
            @RequestParam(required = false) Double minPrincipal,
            @RequestParam(required = false) Double maxPrincipal,
            @RequestParam(required = false) Integer tenure,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "loanId") String sort,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        log.info("Search loans requested loanType={} loanStatus={}", loanType, loanStatus);
        return loanService.searchLoans(loanId, customerName, loanType, loanStatus, minInterestRate, maxInterestRate, 
                                       minPrincipal, maxPrincipal, tenure, page, size, sort, direction);
    }

    @GetMapping("/payments/search")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Search EMI payments with filters")
    public Page<EmiPaymentHistoryDTO> searchPayments(
            @RequestParam(required = false) String referenceNumber,
            @RequestParam(required = false) String paymentMode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sort,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        log.info("Search payments requested referenceNumber={} paymentMode={}", referenceNumber, paymentMode);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        List<EmiPayment> allPayments = emiPaymentRepository.findAll();
        
        List<EmiPayment> filtered = allPayments.stream()
                .filter(p -> referenceNumber == null || (p.getReferenceNumber() != null && p.getReferenceNumber().contains(referenceNumber)))
                .filter(p -> paymentMode == null || p.getPaymentMode().name().equalsIgnoreCase(paymentMode))
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, filtered.size());
        List<EmiPaymentHistoryDTO> content = filtered.subList(start, end).stream()
                .map(this::mapToEmiPaymentHistoryDTO)
                .toList();
        
        return new PageImpl<>(content, pageable, filtered.size());
    }

    private EmiPaymentHistoryDTO mapToEmiPaymentHistoryDTO(EmiPayment payment) {
        EmiSchedule emiSchedule = payment.getEmiSchedule();
        
        // Get customer info from loan
        Long customerId = null;
        String customerName = null;
        if (emiSchedule != null && emiSchedule.getLoan() != null && emiSchedule.getLoan().getCustomer() != null) {
            customerId = emiSchedule.getLoan().getCustomer().getCustomerId();
            customerName = emiSchedule.getLoan().getCustomer().getCustomerName();
        }
        
        return new EmiPaymentHistoryDTO(
               emiSchedule.getEmiId(),
               payment.getAmount(),
               payment.getPaymentMode().name(),
               payment.getReferenceNumber(),
               emiSchedule.getStatus(),
               emiSchedule.getPenaltyAmount(),
               emiSchedule.getDaysPastDue(),
               payment.getPaymentDate(),
               customerId,
               customerName
        );
    }

    // Inner DTO for loan products
    public static class LoanProductDTO {
        private String code;
        private String displayName;
        private double defaultRate;

        public LoanProductDTO(String code, String displayName, double defaultRate) {
            this.code = code;
            this.displayName = displayName;
            this.defaultRate = defaultRate;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public double getDefaultRate() {
            return defaultRate;
        }

        public void setDefaultRate(double defaultRate) {
            this.defaultRate = defaultRate;
        }
    }
}
