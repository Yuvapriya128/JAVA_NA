package org.example.loanemimgmt.serviceImpl;

import org.example.loanemimgmt.dto.EmiPaymentRequestDTO;
import org.example.loanemimgmt.dto.LoanDashboardDTO;
import org.example.loanemimgmt.dto.LoanSummaryDTO;
import org.example.loanemimgmt.dto.SystemDashboardDTO;
import org.example.loanemimgmt.enums.EmiStatus;
import org.example.loanemimgmt.enums.LoanStatus;
import org.example.loanemimgmt.exception.BusinessRuleException;
import org.example.loanemimgmt.exception.LoanNotFoundException;
import org.example.loanemimgmt.model.EmiPayment;
import org.example.loanemimgmt.model.EmiSchedule;
import org.example.loanemimgmt.model.Loan;
import org.example.loanemimgmt.repository.CustomerRepository;
import org.example.loanemimgmt.repository.EmiPaymentRepository;
import org.example.loanemimgmt.repository.EmiScheduleRepository;
import org.example.loanemimgmt.repository.LoanRepository;
import org.example.loanemimgmt.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal TWELVE = new BigDecimal("12");
    private static final BigDecimal PENALTY_RATE = new BigDecimal("0.02");
    private static final BigDecimal DAILY_PENALTY = new BigDecimal("50");
    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    private final LoanRepository loanRepository;
    private final EmiScheduleRepository emiScheduleRepository;
    private final EmiPaymentRepository emiPaymentRepository;
    private final CustomerRepository customerRepository;

    public LoanServiceImpl(LoanRepository loanRepository,
                           EmiScheduleRepository emiScheduleRepository,
                           EmiPaymentRepository emiPaymentRepository,
                           CustomerRepository customerRepository) {
        this.loanRepository = loanRepository;
        this.emiScheduleRepository = emiScheduleRepository;
        this.emiPaymentRepository = emiPaymentRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public LoanSummaryDTO approveLoan(Long loanId) {
        logger.info("Approving loan with ID: {}", loanId);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> {
                    logger.error("Loan not found for id: {}", loanId);
                    return new LoanNotFoundException("Loan not found for id: " + loanId);
                });

        if (loan.getPrincipalAmount() == null || loan.getPrincipalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Loan approval failed for ID: {} - Invalid principal amount: {}", loanId, loan.getPrincipalAmount());
            throw new BusinessRuleException("Loan cannot be approved when principalAmount is less than or equal to zero");
        }
        if (loan.getTenureMonths() == null || loan.getTenureMonths() <= 0) {
            logger.error("Loan approval failed for ID: {} - Invalid tenure: {}", loanId, loan.getTenureMonths());
            throw new BusinessRuleException("Loan tenure must be greater than zero");
        }

        BigDecimal emi = calculateEmi(loan.getPrincipalAmount(), loan.getAnnualInterestRate(), loan.getTenureMonths());
        logger.info("Calculated EMI for Loan ID: {} - Principal: {}, Rate: {}, Tenure: {}, EMI: {}",
            loanId, loan.getPrincipalAmount(), loan.getAnnualInterestRate(), loan.getTenureMonths(), emi);
        loan.setEmiAmount(emi);
        loan.setLoanStatus(LoanStatus.ON_PROGRESS);

        List<EmiSchedule> schedule = generateEmiSchedule(loan);
        logger.info("Generated {} EMI schedules for Loan ID: {}", schedule.size(), loanId);
        loan.getEmiSchedules().clear();
        loan.getEmiSchedules().addAll(schedule);

        Loan saved = loanRepository.save(loan);
        logger.info("Loan {} approved successfully", loanId);
        return toSummary(saved);
    }

    @Override
    public LoanSummaryDTO recordEmiPayment(Long emiId, EmiPaymentRequestDTO request) {
        logger.info("Recording EMI payment for EMI ID: {}, Amount: {}, Payment Date: {}",
            emiId, request.amount(), request.paymentDate());
        EmiSchedule emiSchedule = emiScheduleRepository.findByIdForUpdate(emiId)
                .orElseThrow(() -> {
                    logger.error("EMI schedule not found for id: {}", emiId);
                    return new BusinessRuleException("EMI schedule not found for id: " + emiId);
                });

        Loan loan = emiSchedule.getLoan();
        if (loan.getLoanStatus() == LoanStatus.CLOSED) {
            logger.warn("Payment attempt for closed loan. EMI ID: {}, Loan ID: {}", emiId, loan.getLoanId());
            throw new BusinessRuleException("Closed loans cannot accept further EMI payments");
        }

        refreshPenaltyAndStatus(emiSchedule, request.paymentDate());

        if (emiSchedule.getStatus() == EmiStatus.PAID) {
            logger.warn("Payment attempt for already paid EMI. EMI ID: {}", emiId);
            throw new BusinessRuleException("EMI is already paid");
        }

        BigDecimal payable = emiSchedule.getAmountDue()
                .add(emiSchedule.getPenaltyAmount())
                .subtract(emiSchedule.getAmountPaid());

        if (request.amount().compareTo(payable) > 0) {
            logger.error("Payment amount exceeds payable. EMI ID: {}, Payment: {}, Payable: {}",
                emiId, request.amount(), payable);
            throw new BusinessRuleException("Payment amount exceeds total payable for EMI");
        }

        EmiPayment payment = EmiPayment.builder()
                .amount(request.amount())
                .paymentMode(request.paymentMode())
                .paymentDate(request.paymentDate())
                .referenceNumber(request.referenceNumber())
                .emiSchedule(emiSchedule)
                .build();

        emiPaymentRepository.save(payment);
        logger.debug("EMI payment saved. Payment ID record created for EMI ID: {}", emiId);

        emiSchedule.setAmountPaid(emiSchedule.getAmountPaid().add(request.amount()));
        emiSchedule.setPaymentDate(request.paymentDate());
        refreshPenaltyAndStatus(emiSchedule, request.paymentDate());
        emiScheduleRepository.save(emiSchedule);

        if (emiScheduleRepository.countByLoanLoanIdAndStatusNot(loan.getLoanId(), EmiStatus.PAID) == 0) {
            loan.setLoanStatus(LoanStatus.CLOSED);
            loanRepository.save(loan);
            logger.info("Loan {} closed - all EMIs paid", loan.getLoanId());
        }

        logger.info("EMI payment recorded successfully. EMI ID: {}, Loan ID: {}", emiId, loan.getLoanId());
        return toSummary(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanSummaryDTO> getLoans(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "principalAmount"));
        return loanRepository.findAll(pageable).map(this::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDashboardDTO> getLoanDashboards() {
        List<LoanDashboardDTO> dashboard = new ArrayList<>();
        List<Loan> loans = loanRepository.findAll();

        for (Loan loan : loans) {
            dashboard.add(toDashboard(loan));
        }

        return dashboard;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanSummaryDTO> getLoansWithZeroOverdueEmis() {
        return loanRepository.findLoansWithZeroOverdueEmis().stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getTotalEmiCollectionByCity() {
        return emiPaymentRepository.getTotalEmiCollectionByCityRows().stream()
                .map(row -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("city", String.valueOf(row[0]));
                    result.put("totalCollection", toBigDecimal(row[1]).toPlainString());
                    return result;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getCustomersWithOverdueEmis() {
        return customerRepository.findCustomersWithOverdueEmis().stream()
                .map(customer -> {
                    Map<String, String> row = new HashMap<>();
                    row.put("customerId", String.valueOf(customer.getCustomerId()));
                    row.put("customerName", customer.getCustomerName());
                    row.put("email", customer.getEmail());
                    row.put("city", customer.getCity());
                    return row;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Map<String, String>> getHighestOverdueEmi() {
        return emiScheduleRepository.findHighestOverdueAmount(PageRequest.of(0, 1)).stream()
                .findFirst()
                .map(emi -> {
                    Map<String, String> row = new HashMap<>();
                    row.put("emiId", String.valueOf(emi.getEmiId()));
                    row.put("loanId", String.valueOf(emi.getLoan().getLoanId()));
                    row.put("overdueAmount", emi.getAmountDue().subtract(emi.getAmountPaid()).add(emi.getPenaltyAmount()).toPlainString());
                    return row;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Map<String, String>> getLatestPayment() {
        return emiPaymentRepository.findLatestPayment(PageRequest.of(0, 1)).stream()
                .findFirst()
                .map(payment -> {
                    Map<String, String> row = new HashMap<>();
                    row.put("paymentId", String.valueOf(payment.getPaymentId()));
                    row.put("emiId", String.valueOf(payment.getEmiSchedule().getEmiId()));
                    row.put("amount", payment.getAmount().toPlainString());
                    row.put("paymentDate", String.valueOf(payment.getPaymentDate()));
                    row.put("referenceNumber", payment.getReferenceNumber());
                    return row;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getTopDefaulters(int limit) {
        return customerRepository.findTopDefaulterRows(PageRequest.of(0, limit)).stream()
                .map(row -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("customerId", String.valueOf(((Number) row[0]).longValue()));
                    result.put("customerName", String.valueOf(row[1]));
                    result.put("totalOverdueAmount", toBigDecimal(row[2]).toPlainString());
                    return result;
                })
                .toList();
    }

    @Override
    public int reviseAnnualInterestRates(List<String> loanTypes, BigDecimal newAnnualRate) {
        logger.info("Revising annual interest rates for loan types: {}, New Rate: {}", loanTypes, newAnnualRate);
        if (loanTypes == null || loanTypes.isEmpty()) {
            logger.error("Interest rate revision failed - loanTypes must not be empty");
            throw new BusinessRuleException("loanTypes must not be empty");
        }
        if (newAnnualRate == null || newAnnualRate.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Interest rate revision failed - annualInterestRate must be zero or positive: {}", newAnnualRate);
            throw new BusinessRuleException("annualInterestRate must be zero or positive");
        }

        int updatedCount = loanRepository.reviseAnnualInterestRateByLoanTypes(loanTypes, newAnnualRate);
        logger.info("Successfully revised interest rates for {} loans. New Rate: {}", updatedCount, newAnnualRate);
        return updatedCount;
    }

    private LoanSummaryDTO toSummary(Loan loan) {
        long overdueCount = loan.getEmiSchedules().stream()
                .filter(emi -> emi.getStatus() == EmiStatus.OVERDUE)
                .count();

        BigDecimal outstandingPrincipal = loan.getEmiSchedules().stream()
                .filter(emi -> emi.getStatus() != EmiStatus.PAID)
                .map(EmiSchedule::getPrincipalComponent)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new LoanSummaryDTO(
                loan.getLoanId(),
                loan.getCustomer().getCustomerId(),
                loan.getCustomer().getCustomerName(),
                loan.getLoanType(),
                loan.getPrincipalAmount(),
                loan.getAnnualInterestRate(),
                loan.getTenureMonths(),
                loan.getEmiAmount(),
                loan.getDisbursementDate(),
                loan.getLoanStatus(),
                overdueCount,
                outstandingPrincipal
        );
    }

    private LoanDashboardDTO toDashboard(Loan loan) {
        BigDecimal outstandingPrincipal = BigDecimal.ZERO;
        BigDecimal totalOverdueAmount = BigDecimal.ZERO;
        long overdueCount = 0L;
        LocalDate latestPaymentDate = null;

        for (EmiSchedule emi : loan.getEmiSchedules()) {
            if (emi.getStatus() != EmiStatus.PAID) {
                outstandingPrincipal = outstandingPrincipal.add(emi.getPrincipalComponent());
            }
            if (emi.getStatus() == EmiStatus.OVERDUE) {
                overdueCount++;
                totalOverdueAmount = totalOverdueAmount.add(
                        emi.getAmountDue().subtract(emi.getAmountPaid()).add(emi.getPenaltyAmount())
                );
            }
            Optional<LocalDate> maxPaymentDate = emi.getEmiPayments().stream()
                    .map(EmiPayment::getPaymentDate)
                    .filter(Objects::nonNull)
                    .max(LocalDate::compareTo);
            if (maxPaymentDate.isPresent() && (latestPaymentDate == null || maxPaymentDate.get().isAfter(latestPaymentDate))) {
                latestPaymentDate = maxPaymentDate.get();
            }
        }

        return new LoanDashboardDTO(
                loan.getLoanId(),
                loan.getCustomer().getCustomerName(),
                loan.getCustomer().getCity(),
                loan.getLoanType(),
                loan.getLoanStatus(),
                loan.getPrincipalAmount(),
                loan.getEmiAmount(),
                overdueCount,
                outstandingPrincipal,
                totalOverdueAmount,
                latestPaymentDate
        );
    }

    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualInterestRate, Integer tenureMonths) {
        if (annualInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = annualInterestRate
                .divide(TWELVE.multiply(HUNDRED, MC), 12, RoundingMode.HALF_UP);

        BigDecimal onePlusRPowerN = BigDecimal.ONE.add(monthlyRate, MC).pow(tenureMonths, MC);
        BigDecimal numerator = principal.multiply(monthlyRate, MC).multiply(onePlusRPowerN, MC);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE, MC);

        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private List<EmiSchedule> generateEmiSchedule(Loan loan) {
        List<EmiSchedule> schedules = new ArrayList<>();
        BigDecimal monthlyRate = loan.getAnnualInterestRate().divide(TWELVE.multiply(HUNDRED, MC), 12, RoundingMode.HALF_UP);
        BigDecimal outstandingPrincipal = loan.getPrincipalAmount();

        for (int installment = 1; installment <= loan.getTenureMonths(); installment++) {
            BigDecimal interest = outstandingPrincipal.multiply(monthlyRate, MC).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalComponent = loan.getEmiAmount().subtract(interest).setScale(2, RoundingMode.HALF_UP);

            if (installment == loan.getTenureMonths() || principalComponent.compareTo(outstandingPrincipal) > 0) {
                principalComponent = outstandingPrincipal.setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal amountDue = principalComponent.add(interest).setScale(2, RoundingMode.HALF_UP);
            outstandingPrincipal = outstandingPrincipal.subtract(principalComponent).max(BigDecimal.ZERO);

            EmiSchedule emiSchedule = EmiSchedule.builder()
                    .installmentNumber(installment)
                    .dueDate(loan.getDisbursementDate().plusMonths(installment))
                    .amountDue(amountDue)
                    .principalComponent(principalComponent)
                    .interestComponent(interest)
                    .amountPaid(BigDecimal.ZERO)
                    .paymentDate(null)
                    .status(EmiStatus.PENDING)
                    .daysPastDue(0)
                    .penaltyAmount(BigDecimal.ZERO)
                    .loan(loan)
                    .build();

            schedules.add(emiSchedule);
        }

        return schedules;
    }

    private void refreshPenaltyAndStatus(EmiSchedule emiSchedule, LocalDate currentDate) {
        BigDecimal amountDue = emiSchedule.getAmountDue();
        BigDecimal amountPaid = emiSchedule.getAmountPaid() == null ? BigDecimal.ZERO : emiSchedule.getAmountPaid();

        if (amountPaid.compareTo(amountDue) >= 0) {
            emiSchedule.setStatus(EmiStatus.PAID);
            emiSchedule.setDaysPastDue(0);
            emiSchedule.setPenaltyAmount(BigDecimal.ZERO);
            return;
        }

        if (currentDate.isAfter(emiSchedule.getDueDate())) {
            int dpd = (int) ChronoUnit.DAYS.between(emiSchedule.getDueDate(), currentDate);
            emiSchedule.setDaysPastDue(dpd);
            BigDecimal penalty = amountDue.multiply(PENALTY_RATE, MC)
                    .add(DAILY_PENALTY.multiply(BigDecimal.valueOf(dpd), MC))
                    .setScale(2, RoundingMode.HALF_UP);
            emiSchedule.setPenaltyAmount(penalty);

            if (amountPaid.compareTo(BigDecimal.ZERO) == 0) {
                emiSchedule.setStatus(EmiStatus.OVERDUE);
            } else {
                emiSchedule.setStatus(EmiStatus.PENDING);
            }
            return;
        }

        emiSchedule.setStatus(EmiStatus.PENDING);
        emiSchedule.setDaysPastDue(0);
        emiSchedule.setPenaltyAmount(BigDecimal.ZERO);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        return BigDecimal.valueOf(((Number) value).doubleValue()).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(readOnly = true)
    public SystemDashboardDTO getSystemDashboard() {
        logger.info("Fetching system dashboard metrics");
        long totalCustomers = customerRepository.count();
        long totalLoans = loanRepository.count();
        long activeLoans = loanRepository.countByLoanStatus(LoanStatus.ON_PROGRESS);
        long closedLoans = loanRepository.countByLoanStatus(LoanStatus.CLOSED);
        long overdueEmis = emiScheduleRepository.countByStatus(EmiStatus.OVERDUE);

        BigDecimal totalEmiCollected = nvl(emiPaymentRepository.getTotalEmiCollected());
        BigDecimal totalPenaltyCollected = nvl(emiScheduleRepository.getTotalPenaltyCollected());
        BigDecimal averageInterestRate = nvl(loanRepository.getAverageInterestRate());

        BigDecimal highestOutstandingLoan = emiScheduleRepository.findHighestOutstandingLoan(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(this::nvl)
                .orElse(BigDecimal.ZERO);

        Map<String, Object> highestPayingCustomer = customerRepository.findHighestPayingCustomerRows(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(row -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("customerId", ((Number) row[0]).longValue());
                    result.put("customerName", String.valueOf(row[1]));
                    result.put("totalPaid", toBigDecimal(row[2]));
                    return result;
                })
                .orElse(null);

        long npaAccounts = emiScheduleRepository.countNpaAccounts();

        logger.debug("Dashboard metrics: Customers={}, Loans={}, Active={}, Closed={}, OverdueEMIs={}, TotalCollected={}, NPA={}",
            totalCustomers, totalLoans, activeLoans, closedLoans, overdueEmis, totalEmiCollected, npaAccounts);

        return new SystemDashboardDTO(
                totalCustomers,
                totalLoans,
                activeLoans,
                closedLoans,
                overdueEmis,
                totalEmiCollected,
                totalPenaltyCollected,
                averageInterestRate,
                highestOutstandingLoan,
                highestPayingCustomer,
                npaAccounts
        );
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
