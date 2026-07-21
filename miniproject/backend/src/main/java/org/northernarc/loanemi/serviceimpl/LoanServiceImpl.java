package org.northernarc.loanemi.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.northernarc.loanemi.dto.CreateLoanRequest;
import org.northernarc.loanemi.dto.EmiCalculationRequestDTO;
import org.northernarc.loanemi.dto.EmiCalculationResponseDTO;
import org.northernarc.loanemi.dto.EmiPaymentReceiptDTO;
import org.northernarc.loanemi.dto.EmiPaymentRequestDTO;
import org.northernarc.loanemi.dto.EmiPaymentResponseDTO;
import org.northernarc.loanemi.dto.LoanDashboardDTO;
import org.northernarc.loanemi.dto.LoanSummaryDTO;
import org.northernarc.loanemi.enums.EmiStatus;
import org.northernarc.loanemi.enums.LoanApplicationStatus;
import org.northernarc.loanemi.enums.LoanStatus;
import org.northernarc.loanemi.enums.LoanType;
import org.northernarc.loanemi.exception.CustomerNotFoundException;
import org.northernarc.loanemi.exception.DuplicateLoanException;
import org.northernarc.loanemi.exception.IdempotencyConflictException;
import org.northernarc.loanemi.exception.LoanNotFoundException;
import org.northernarc.loanemi.exception.ValidationException;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.model.EmiPayment;
import org.northernarc.loanemi.model.EmiSchedule;
import org.northernarc.loanemi.model.IdempotencyRecord;
import org.northernarc.loanemi.model.Loan;
import org.northernarc.loanemi.model.LoanApplication;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.repository.EmiPaymentRepository;
import org.northernarc.loanemi.repository.EmiScheduleRepository;
import org.northernarc.loanemi.repository.IdempotencyRepository;
import org.northernarc.loanemi.repository.LoanApplicationRepository;
import org.northernarc.loanemi.repository.LoanRepository;
import org.northernarc.loanemi.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanServiceImpl implements LoanService {
    private static final Logger log = LoggerFactory.getLogger(LoanServiceImpl.class);
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final EmiScheduleRepository emiScheduleRepository;
    private final EmiPaymentRepository emiPaymentRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;
    
    public LoanServiceImpl(LoanRepository loanRepository, CustomerRepository customerRepository,
                           EmiScheduleRepository emiScheduleRepository,
                           EmiPaymentRepository emiPaymentRepository,
                           LoanApplicationRepository loanApplicationRepository,
                           IdempotencyRepository idempotencyRepository,
                           ObjectMapper objectMapper) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.emiScheduleRepository = emiScheduleRepository;
        this.emiPaymentRepository = emiPaymentRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public LoanSummaryDTO createLoan(CreateLoanRequest request) {
        return createLoanInternal(request);
    }
    
    @Override
    @Transactional
    public LoanSummaryDTO createLoanWithIdempotency(CreateLoanRequest request, String idempotencyKey, String userId) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return createLoanInternal(request);
        }
        
        // Check for existing idempotency record
        Optional<IdempotencyRecord> existingRecord = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
        if (existingRecord.isPresent()) {
            IdempotencyRecord record = existingRecord.get();
            
            // Verify payload hash matches
            String currentPayloadHash = computePayloadHash(request);
            if (!currentPayloadHash.equals(record.getPayloadHash())) {
                log.warn("Idempotency conflict: key={} has different payload", idempotencyKey);
                throw new IdempotencyConflictException(
                        "Idempotency key already used with different request payload", idempotencyKey);
            }
            
            // Return cached result
            log.info("Returning cached idempotent response for key={} loanId={}", 
                    idempotencyKey, record.getResultEntityId());
            return getLoan(record.getResultEntityId());
        }
        
        // Create the loan
        LoanSummaryDTO result = createLoanInternal(request);
        
        // Store idempotency record
        try {
            String resultJson = objectMapper.writeValueAsString(result);
            IdempotencyRecord newRecord = new IdempotencyRecord(
                    idempotencyKey,
                    userId,
                    "CREATE_LOAN",
                    computePayloadHash(request),
                    result.getLoanId(),
                    resultJson,
                    201
            );
            idempotencyRepository.save(newRecord);
            log.info("AUDIT: IDEMPOTENCY_RECORDED - key={} loanId={} userId={}", 
                    idempotencyKey, result.getLoanId(), userId);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize idempotency result: {}", e.getMessage());
        }
        
        return result;
    }
    
    private LoanSummaryDTO createLoanInternal(CreateLoanRequest request) {
        log.info("Creating loan for customerId={} loanType={} principal={} tenureMonths={} applicationId={}",
                request.getCustomerId(), request.getLoanType(), request.getPrincipalAmount(), 
                request.getTenureMonths(), request.getApplicationId());
        
        // Validation
        if (request.getPrincipalAmount() <= 0) {
            log.warn("Loan creation rejected: invalid principalAmount={}", request.getPrincipalAmount());
            throw new IllegalArgumentException("Loan cannot be approved if principalAmount <= 0");
        }
        if (request.getTenureMonths() <= 0) {
            log.warn("Loan creation rejected: invalid tenureMonths={}", request.getTenureMonths());
            throw new IllegalArgumentException("Tenure must be greater than zero");
        }
        
        // If applicationId is provided, validate and lock the application first
        LoanApplication application = null;
        if (request.getApplicationId() != null) {
            application = validateAndLockApplication(request.getApplicationId());
        }
        
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + request.getCustomerId()));
        
        Loan loan = new Loan();
        loan.setLoanType(request.getLoanType());
        loan.setPrincipalAmount(request.getPrincipalAmount());
        loan.setAnnualInterestRate(request.getAnnualInterestRate());
        loan.setTenureMonths(request.getTenureMonths());
        loan.setLoanStatus("ACTIVE");
        loan.setDisbursementDate(LocalDate.now());
        loan.setCustomer(customer);
        double emi = calculateEmi(loan.getPrincipalAmount(), loan.getAnnualInterestRate(), loan.getTenureMonths());
        loan.setEmiAmount(emi);
        generateEmiSchedule(loan);
        
        Loan saved;
        try {
            saved = loanRepository.save(loan);
        } catch (DataIntegrityViolationException e) {
            log.error("Loan creation failed due to constraint violation: {}", e.getMessage());
            throw new DuplicateLoanException("Failed to create loan: constraint violation");
        }
        
        log.info("Loan created successfully loanId={} customerId={} emiAmount={}",
                saved.getLoanId(), saved.getCustomer().getCustomerId(), saved.getEmiAmount());
        
        // Link to application if provided (atomically within same transaction)
        if (application != null) {
            linkApplicationToLoan(application, saved);
            log.info("AUDIT: LOAN_CREATED_FROM_APPLICATION - applicationId={} loanId={} customerId={} timestamp={}",
                    application.getApplicationId(), saved.getLoanId(), customer.getCustomerId(), LocalDateTime.now());
        }
        
        return toSummary(saved);
    }
    
    /**
     * Validate and acquire pessimistic lock on application for loan creation.
     * Throws DuplicateLoanException if loan already linked.
     */
    private LoanApplication validateAndLockApplication(Long applicationId) {
        // Use pessimistic lock to prevent concurrent loan creation
        LoanApplication application = loanApplicationRepository.findByIdWithLock(applicationId)
                .orElseThrow(() -> new LoanNotFoundException("Application not found: " + applicationId));
        
        // Validate application status
        LoanApplicationStatus status = application.getApplicationStatus();
        if (status != LoanApplicationStatus.APPROVED) {
            throw new ValidationException(
                    "Only APPROVED applications can have loans created. Current status: " + status);
        }
        
        // Check if loan already linked (duplicate prevention)
        if (application.getCreatedLoan() != null) {
            Long existingLoanId = application.getCreatedLoan().getLoanId();
            log.warn("Duplicate loan attempt blocked for applicationId={} existingLoanId={}", 
                    applicationId, existingLoanId);
            throw new DuplicateLoanException(
                    "Loan already created for this application. Existing loan ID: " + existingLoanId,
                    applicationId, existingLoanId);
        }
        
        return application;
    }
    
    /**
     * Link application to loan atomically.
     */
    private void linkApplicationToLoan(LoanApplication application, Loan loan) {
        application.setCreatedLoan(loan);
        // Update status if you want to use LOAN_CREATED status (optional)
        // application.setApplicationStatus(LoanApplicationStatus.LOAN_CREATED);
        
        try {
            loanApplicationRepository.save(application);
        } catch (DataIntegrityViolationException e) {
            // Unique constraint violation - race condition caught
            log.error("Race condition detected: loan already linked to application applicationId={}", 
                    application.getApplicationId());
            throw new DuplicateLoanException(
                    "Loan already created for this application (concurrent request detected)",
                    application.getApplicationId(), null);
        }
    }
    
    private String computePayloadHash(CreateLoanRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.warn("Failed to compute payload hash: {}", e.getMessage());
            return "UNKNOWN";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryDTO> getLoans(int page, int size) {
        log.info("Fetching loans page={} size={}", page, size);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("principalAmount").descending());
        return loanRepository.findAll(pageable).map(this::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanSummaryDTO getLoan(Long loanId) {
        log.info("Fetching loan details loanId={}", loanId);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));
        return toSummary(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanDashboardDTO getDashboard() {
        log.info("Building dashboard metrics");
        long totalCustomers = customerRepository.count();
        long totalLoans = loanRepository.count();
        long activeLoans = loanRepository.findActiveLoans().size();
        long closedLoans = loanRepository.findByLoanStatus(LoanStatus.CLOSED).size();
        long overdueCount = defaultZeroLong(emiScheduleRepository.countOverdueEmis());
        double totalEmiCollected = defaultZero(loanRepository.findTotalEmiCollected());
        double totalPenaltyCollected = defaultZero(loanRepository.findTotalPenaltyCollected());
        double averageInterestRate = defaultZero(loanRepository.findAverageInterestRate());
        String highestOutstandingLoan = extractHighestOutstandingLoan();
        String highestPayingCustomer = extractHighestPayingCustomer();
        long npaAccounts = defaultZeroLong(emiScheduleRepository.countNpaAccounts());
        return new LoanDashboardDTO(totalCustomers, totalLoans, activeLoans, closedLoans, overdueCount,
                totalEmiCollected, totalPenaltyCollected, averageInterestRate, highestOutstandingLoan,
                highestPayingCustomer, npaAccounts);
    }

    @Override
    @Transactional
    public void recalculateOverduePenalties(LocalDate currentDate) {
        List<EmiSchedule> schedules = emiScheduleRepository.findUnpaidPastDueSchedules(currentDate);
        log.info("Recalculating overdue penalties for schedulesCount={} currentDate={}", schedules.size(), currentDate);
        for (EmiSchedule schedule : schedules) {
            long dpd = ChronoUnit.DAYS.between(schedule.getDueDate(), currentDate);
            schedule.setDaysPastDue((int) dpd);
            schedule.setStatus("OVERDUE");
            double penalty = (schedule.getAmountDue() * 0.02) + (schedule.getDaysPastDue() * 50);
            schedule.setPenaltyAmount(penalty);
        }
    }

    @Override
    @Transactional
    public EmiPaymentResponseDTO payEmi(EmiPaymentRequestDTO request) {
        log.info("Processing EMI payment emiId={} amount={} paymentMode={}",
                request.getEmiId(), request.getAmount(), request.getPaymentMode());
        EmiSchedule schedule = resolveScheduleForPayment(request.getEmiId());
        if (schedule == null) {
            log.warn("EMI payment rejected: schedule not found emiId={}", request.getEmiId());
            throw new LoanNotFoundException("EMI schedule not found: " + request.getEmiId());
        }
        EmiPayment existingPayment = emiPaymentRepository.findByReferenceNumber(request.getReferenceNumber());
        if (existingPayment != null) {
            Long existingEmiId = existingPayment.getEmiSchedule() != null
                    ? existingPayment.getEmiSchedule().getEmiId()
                    : null;
            if (existingEmiId != null && existingEmiId.equals(schedule.getEmiId())) {
                log.info("EMI payment retry accepted as idempotent referenceNumber={} emiId={} paymentId={}",
                        request.getReferenceNumber(), existingEmiId, existingPayment.getPaymentId());
                return toPaymentResponse(schedule);
            }
            log.warn("EMI payment rejected: duplicate referenceNumber={} existingEmiId={} requestedEmiId={}",
                    request.getReferenceNumber(), existingEmiId, schedule.getEmiId());
            throw new IllegalArgumentException("Duplicate payment reference number");
        }
        if (EmiStatus.PAID.equals(schedule.getStatusEnum())) {
            log.warn("EMI payment rejected: EMI already paid emiId={}", schedule.getEmiId());
            throw new IllegalStateException("EMI is already paid: " + schedule.getEmiId());
        }
        if (LoanStatus.CLOSED.equals(schedule.getLoan().getLoanStatusEnum())) {
            log.warn("EMI payment rejected: loan already closed loanId={}", schedule.getLoan().getLoanId());
            throw new IllegalArgumentException("Closed loans cannot accept further EMI payments");
        }
        double outstanding = payableForSchedule(schedule);
        if (request.getAmount() > outstanding) {
            log.warn("EMI payment rejected: amount exceeds outstanding emiId={} requested={} outstanding={}",
                    request.getEmiId(), request.getAmount(), outstanding);
            throw new IllegalArgumentException("Payment exceeds payable amount");
        }
        EmiPayment payment = new EmiPayment();
        payment.setAmount(request.getAmount());
        payment.setPaymentMode(request.getPaymentMode());
        payment.setPaymentDate(LocalDate.now());
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setEmiSchedule(schedule);
        try {
            emiPaymentRepository.save(payment);
        } catch (DataIntegrityViolationException ex) {
            String causeMessage = ex.getMostSpecificCause() != null
                    ? ex.getMostSpecificCause().getMessage()
                    : ex.getMessage();
            if (causeMessage != null && causeMessage.contains("reference_number")) {
                log.warn("EMI payment rejected: duplicate referenceNumber={}", request.getReferenceNumber());
                throw new IllegalArgumentException("Duplicate payment reference number");
            }
            log.error("EMI payment persistence conflict emiId={} referenceNumber={} cause={}",
                    request.getEmiId(), request.getReferenceNumber(), causeMessage);
            throw new IllegalStateException("Payment could not be recorded due to data conflict");
        }
        double newPaid = defaultZero(schedule.getAmountPaid()) + request.getAmount();
        schedule.setAmountPaid(newPaid);
        schedule.setPaymentDate(LocalDate.now());
        if (newPaid >= schedule.getAmountDue() + schedule.getPenaltyAmount()) {
            schedule.setStatus("PAID");
            schedule.setPenaltyAmount(0.0);
            schedule.setDaysPastDue(0);
            closeLoanIfCompleted(schedule.getLoan().getLoanId());
        } else {
            schedule.setStatus("PENDING");
        }
        log.info("EMI payment processed emiId={} updatedStatus={} amountPaid={}",
                request.getEmiId(), schedule.getStatus(), schedule.getAmountPaid());
        return toPaymentResponse(schedule);
    }

    private EmiSchedule resolveScheduleForPayment(Long requestedEmiId) {
        EmiSchedule schedule = emiScheduleRepository.findByIdForUpdate(requestedEmiId);
        if (schedule != null) {
            return schedule;
        }
        // Some clients send display IDs like loanId*100 + installmentNumber (e.g., 4001),
        // so resolve that composite to the actual EMI primary key.
        if (requestedEmiId != null && requestedEmiId >= 1000) {
            long candidateLoanId = requestedEmiId / 100;
            int candidateInstallmentNumber = (int) (requestedEmiId % 100);
            if (candidateInstallmentNumber > 0) {
                Loan loan = loanRepository.findById(candidateLoanId).orElse(null);
                if (loan != null && loan.getEmiSchedules() != null) {
                    EmiSchedule matched = loan.getEmiSchedules().stream()
                            .filter(e -> e.getInstallmentNumber() != null
                                    && e.getInstallmentNumber() == candidateInstallmentNumber)
                            .findFirst()
                            .orElse(null);
                    if (matched != null) {
                        EmiSchedule locked = emiScheduleRepository.findByIdForUpdate(matched.getEmiId());
                        if (locked != null) {
                            log.info("Resolved composite EMI id {} to emiId={} (loanId={} installment={})",
                                    requestedEmiId, locked.getEmiId(), candidateLoanId, candidateInstallmentNumber);
                            return locked;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    @Transactional
    public LoanSummaryDTO updateLoanInterest(Long loanId, Double rate) {
        log.info("Updating loan interest rate loanId={} rate={}", loanId, rate);
        if (rate == null || rate <= 0) {
            log.warn("Loan interest update rejected: invalid rate={} loanId={}", rate, loanId);
            throw new IllegalArgumentException("Interest rate must be greater than zero");
        }
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));
        loan.setAnnualInterestRate(rate);
        return toSummary(loan);
    }

    @Override
    @Transactional
    public void deleteLoan(Long loanId) {
        log.info("Deleting loan loanId={}", loanId);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));
        
        // Delete associated EMI payments first (due to FK constraint)
        emiPaymentRepository.deleteByLoanId(loanId);
        
        // Delete associated EMI schedules
        emiScheduleRepository.deleteByLoanLoanId(loanId);
        
        // Delete the loan
        loanRepository.delete(loan);
        
        log.info("AUDIT: LOAN_DELETED - loanId={}, customerId={}, timestamp={}", 
                loanId, loan.getCustomer().getCustomerId(), java.time.LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public EmiCalculationResponseDTO calculateEmi(EmiCalculationRequestDTO request) {
        log.info("Calculating EMI for principal={} annualInterestRate={} tenureMonths={}",
                request.getPrincipalAmount(), request.getAnnualInterestRate(), request.getTenureMonths());
        
        double emiAmount = calculateEmiAmount(request.getPrincipalAmount(), 
                                              request.getAnnualInterestRate(), 
                                              request.getTenureMonths());
        
        double totalPayment = emiAmount * request.getTenureMonths();
        double totalInterest = totalPayment - request.getPrincipalAmount();
        
        EmiCalculationResponseDTO response = new EmiCalculationResponseDTO(emiAmount, totalInterest, totalPayment);
        
        log.info("EMI calculation completed: emiAmount={} totalInterest={} totalPayment={}",
                emiAmount, totalInterest, totalPayment);
        
        return response;
    }

    private double calculateEmiAmount(double principal, double annualInterestRate, int tenureMonths) {
        double monthlyRate = annualInterestRate / (12 * 100);
        if (monthlyRate == 0) {
            return principal / tenureMonths;
        }
        double factor = Math.pow(1 + monthlyRate, tenureMonths);
        return (principal * monthlyRate * factor) / (factor - 1);
    }

    private double calculateEmi(double principal, double annualInterestRate, int tenureMonths) {
        return calculateEmiAmount(principal, annualInterestRate, tenureMonths);
    }

    private void generateEmiSchedule(Loan loan) {
        double outstanding = loan.getPrincipalAmount();
        double monthlyRate = loan.getAnnualInterestRate() / (12 * 100);
        LocalDate startDate = loan.getDisbursementDate();
        for (int installment = 1; installment <= loan.getTenureMonths(); installment++) {
            double interestComponent = outstanding * monthlyRate;
            double principalComponent = loan.getEmiAmount() - interestComponent;
            if (installment == loan.getTenureMonths()) {
                principalComponent = outstanding;
            }
            double amountDue = principalComponent + interestComponent;
            outstanding = Math.max(0, outstanding - principalComponent);

            EmiSchedule schedule = new EmiSchedule();
            schedule.setInstallmentNumber(installment);
            schedule.setDueDate(startDate.plusMonths(installment));
            schedule.setAmountDue(amountDue);
            schedule.setPrincipalComponent(principalComponent);
            schedule.setInterestComponent(interestComponent);
            schedule.setAmountPaid(0.0);
            schedule.setStatus("PENDING");
            schedule.setDaysPastDue(0);
            schedule.setPenaltyAmount(0.0);
            loan.addEmiSchedule(schedule);
        }
    }

    private LoanSummaryDTO toSummary(Loan loan) {
        return new LoanSummaryDTO(
                loan.getLoanId(),
                loan.getLoanType(),
                loan.getPrincipalAmount(),
                loan.getAnnualInterestRate(),
                loan.getTenureMonths(),
                loan.getEmiAmount(),
                loan.getLoanStatus(),
                loan.getCustomer().getCustomerId(),
                loan.getCustomer().getCustomerName(),
                loan.getCustomer().getCity()
        );
    }

    private EmiPaymentResponseDTO toPaymentResponse(EmiSchedule schedule) {
        return new EmiPaymentResponseDTO(
                schedule.getEmiId(),
                schedule.getAmountDue(),
                schedule.getAmountPaid(),
                schedule.getPenaltyAmount(),
                schedule.getStatus(),
                schedule.getDaysPastDue(),
                schedule.getPaymentDate()
        );
    }

    private double defaultZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private long defaultZeroLong(Long value) {
        return value == null ? 0L : value;
    }

    private void closeLoanIfCompleted(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));
        boolean hasPending = loan.getEmiSchedules().stream().anyMatch(e -> !EmiStatus.PAID.equals(e.getStatusEnum()));
        if (!hasPending) {
            loan.setLoanStatus("CLOSED");
            log.info("Loan marked as CLOSED loanId={}", loanId);
        }
    }

    private double payableForSchedule(EmiSchedule schedule) {
        if (EmiStatus.OVERDUE.equals(schedule.getStatusEnum())) {
            recalculatePenalty(schedule, LocalDate.now());
        }
        return (schedule.getAmountDue() + schedule.getPenaltyAmount()) - defaultZero(schedule.getAmountPaid());
    }

    private void recalculatePenalty(EmiSchedule schedule, LocalDate currentDate) {
        if (schedule.getDueDate().isBefore(currentDate)) {
            long dpd = ChronoUnit.DAYS.between(schedule.getDueDate(), currentDate);
            schedule.setDaysPastDue((int) dpd);
            schedule.setPenaltyAmount((schedule.getAmountDue() * 0.02) + (schedule.getDaysPastDue() * 50));
            schedule.setStatus("OVERDUE");
        }
    }

    private String extractHighestOutstandingLoan() {
        List<Object[]> ranking = loanRepository.findLoanOutstandingRanking();
        if (ranking.isEmpty()) {
            return "N/A";
        }
        Object[] row = ranking.get(0);
        return "Loan#" + row[0] + " (" + row[1] + ")";
    }

    private String extractHighestPayingCustomer() {
        List<Object[]> ranking = customerRepository.findHighestPayingCustomers();
        if (ranking.isEmpty()) {
            return "N/A";
        }
        Object[] row = ranking.get(0);
        return "Customer#" + row[0] + " (" + row[1] + ")";
    }

    @Override
    @Transactional(readOnly = true)
    public EmiPaymentReceiptDTO getPaymentReceipt(EmiSchedule emiSchedule) {
        log.info("Generating payment receipt for emiId={}", emiSchedule.getEmiId());
        
        Loan loan = emiSchedule.getLoan();
        Customer customer = loan.getCustomer();
        
        EmiPaymentReceiptDTO receipt = new EmiPaymentReceiptDTO();
        receipt.setReceiptNumber("RECEIPT-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        receipt.setGeneratedAt(LocalDateTime.now());
        
        receipt.setEmiId(emiSchedule.getEmiId());
        receipt.setInstallmentNumber(emiSchedule.getInstallmentNumber());
        receipt.setDueDate(emiSchedule.getDueDate());
        receipt.setAmountDue(emiSchedule.getAmountDue());
        receipt.setPrincipalComponent(emiSchedule.getPrincipalComponent());
        receipt.setInterestComponent(emiSchedule.getInterestComponent());
        receipt.setPenaltyAmount(emiSchedule.getPenaltyAmount());
        receipt.setEmiStatus(emiSchedule.getStatusEnum());
        
        if (emiSchedule.getPayments() != null && !emiSchedule.getPayments().isEmpty()) {
            EmiPayment payment = emiSchedule.getPayments().get(emiSchedule.getPayments().size() - 1);
            receipt.setAmountPaid(payment.getAmount());
            receipt.setPaymentDate(payment.getPaymentDate());
            receipt.setPaymentMode(payment.getPaymentMode());
            receipt.setReferenceNumber(payment.getReferenceNumber());
        } else {
            receipt.setAmountPaid(0.0);
        }
        
        receipt.setLoanId(loan.getLoanId());
        receipt.setLoanType(loan.getLoanTypeEnum());
        receipt.setLoanPrincipal(loan.getPrincipalAmount());
        receipt.setLoanInterestRate(loan.getAnnualInterestRate());
        receipt.setLoanTenureMonths(loan.getTenureMonths());
        receipt.setDisbursementDate(loan.getDisbursementDate());
        
        receipt.setCustomerId(customer.getCustomerId());
        receipt.setCustomerName(customer.getCustomerName());
        receipt.setCustomerEmail(customer.getEmail());
        receipt.setCustomerPhone(customer.getPhoneNumber());
        receipt.setCustomerCity(customer.getCity());
        
        return receipt;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryDTO> searchLoans(String loanId, String customerName, String loanType, String loanStatus,
                                           Double minInterestRate, Double maxInterestRate, Double minPrincipal,
                                           Double maxPrincipal, Integer tenure, int page, int size, String sort, Sort.Direction direction) {
        log.info("Searching loans loanType={} loanStatus={} minRate={} maxRate={}", loanType, loanStatus, minInterestRate, maxInterestRate);
        
        org.springframework.data.domain.Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        List<Loan> allLoans = loanRepository.findAll();
        
        List<Loan> filtered = allLoans.stream()
                .filter(l -> loanId == null || l.getLoanId().toString().equals(loanId))
                .filter(l -> customerName == null || l.getCustomer().getCustomerName().toLowerCase().contains(customerName.toLowerCase()))
                .filter(l -> loanType == null || (l.getLoanType() != null && l.getLoanType().equalsIgnoreCase(loanType)))
                .filter(l -> loanStatus == null || l.getLoanStatus().equalsIgnoreCase(loanStatus))
                .filter(l -> minInterestRate == null || l.getAnnualInterestRate() >= minInterestRate)
                .filter(l -> maxInterestRate == null || l.getAnnualInterestRate() <= maxInterestRate)
                .filter(l -> minPrincipal == null || l.getPrincipalAmount() >= minPrincipal)
                .filter(l -> maxPrincipal == null || l.getPrincipalAmount() <= maxPrincipal)
                .filter(l -> tenure == null || l.getTenureMonths().equals(tenure))
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, filtered.size());
        List<LoanSummaryDTO> content = filtered.subList(start, end).stream()
                .map(this::toSummary)
                .toList();
        
        return new org.springframework.data.domain.PageImpl<>(content, pageable, filtered.size());
    }
}
