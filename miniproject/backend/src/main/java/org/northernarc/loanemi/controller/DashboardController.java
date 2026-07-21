package org.northernarc.loanemi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.northernarc.loanemi.dto.DashboardStatisticsDTO;
import org.northernarc.loanemi.dto.EmiInsightsDTO;
import org.northernarc.loanemi.dto.LoanDashboardDTO;
import org.northernarc.loanemi.enums.LoanStatus;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.model.EmiSchedule;
import org.northernarc.loanemi.model.Loan;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.repository.EmiPaymentRepository;
import org.northernarc.loanemi.repository.EmiScheduleRepository;
import org.northernarc.loanemi.repository.LoanRepository;
import org.northernarc.loanemi.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Dashboard", description = "Analytics and dashboard APIs")
public class DashboardController {
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final LoanService loanService;
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final EmiPaymentRepository emiPaymentRepository;
    private final EmiScheduleRepository emiScheduleRepository;

    public DashboardController(LoanService loanService, CustomerRepository customerRepository,
                               LoanRepository loanRepository, EmiPaymentRepository emiPaymentRepository,
                               EmiScheduleRepository emiScheduleRepository) {
        this.loanService = loanService;
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.emiPaymentRepository = emiPaymentRepository;
        this.emiScheduleRepository = emiScheduleRepository;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Get top-level dashboard metrics")
    public LoanDashboardDTO getDashboard() {
        log.info("Dashboard metrics requested");
        return loanService.getDashboard();
    }

    @GetMapping("/api/dashboard/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin dashboard endpoint")
    public LoanDashboardDTO getAdminDashboard() {
        log.info("Admin dashboard metrics requested");
        return loanService.getDashboard();
    }

    @GetMapping("/api/dashboard/statistics")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Get comprehensive dashboard statistics")
    public DashboardStatisticsDTO getStatistics() {
        log.info("Comprehensive dashboard statistics requested");
        
        List<Customer> allCustomers = customerRepository.findAll();
        List<Loan> allLoans = loanRepository.findAll();
        
        long activeCustomers = allCustomers.stream().filter(Customer::isActive).count();
        long inactiveCustomers = allCustomers.stream().filter(c -> !c.isActive()).count();
        double avgCreditScore = allCustomers.stream()
                .mapToInt(Customer::getCreditScore)
                .average()
                .orElse(0.0);
        
        List<Customer> customersWithOverdue = customerRepository.findCustomersWithOverdueEmis();
        
        long activeLoans = allLoans.stream()
                .filter(l -> "ACTIVE".equals(l.getLoanStatus()))
                .count();
        long closedLoans = allLoans.stream()
                .filter(l -> "CLOSED".equals(l.getLoanStatus()))
                .count();
        Double avgRate = loanRepository.findAverageInterestRate();
        
        Double totalCollected = loanRepository.findTotalEmiCollected();
        Double totalOutstanding = allLoans.stream()
                .mapToDouble(l -> l.getEmiSchedules().stream()
                        .mapToDouble(e -> "PAID".equals(e.getStatus()) ? 0 : (e.getAmountDue() - e.getAmountPaid()))
                        .sum())
                .sum();
        
        DashboardStatisticsDTO.CustomerStatistics customerStats = new DashboardStatisticsDTO.CustomerStatistics(
                (long) allCustomers.size(),
                activeCustomers,
                inactiveCustomers,
                avgCreditScore,
                (long) customersWithOverdue.size()
        );
        
        DashboardStatisticsDTO.LoanStatistics loanStats = new DashboardStatisticsDTO.LoanStatistics(
                (long) allLoans.size(),
                activeLoans,
                closedLoans,
                avgRate != null ? avgRate : 0.0,
                totalOutstanding
        );
        
        DashboardStatisticsDTO.PaymentStatistics paymentStats = new DashboardStatisticsDTO.PaymentStatistics(
                totalCollected != null ? totalCollected : 0.0,
                allLoans.isEmpty() ? 0.0 : (totalCollected != null ? totalCollected : 0.0) / allLoans.size(),
                0.0,
                totalCollected != null ? totalCollected : 0.0
        );
        
        List<String> topDefaulters = customersWithOverdue.stream()
                .map(c -> c.getCustomerName() + " (ID: " + c.getCustomerId() + ")")
                .toList();
        
        Double collectionPercentage = totalOutstanding > 0 ? 
                ((totalCollected != null ? totalCollected : 0.0) / (totalCollected + totalOutstanding)) * 100 : 0.0;
        
        DashboardStatisticsDTO.RiskStatistics riskStats = new DashboardStatisticsDTO.RiskStatistics(
                (long) customersWithOverdue.size(),
                topDefaulters,
                allLoans.stream().mapToDouble(Loan::getPrincipalAmount).max().orElse(0.0),
                collectionPercentage
        );
        
        return new DashboardStatisticsDTO(customerStats, loanStats, paymentStats, riskStats);
    }

    @GetMapping("/api/dashboard/emi-insights")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Get EMI insights for dashboard widget")
    public EmiInsightsDTO getEmiInsights() {
        log.info("EMI insights requested");
        
        List<Loan> activeLoans = loanRepository.findAll().stream()
                .filter(l -> "ACTIVE".equals(l.getLoanStatus()))
                .toList();
        
        // Calculate EMI statistics from active loans
        Double highestEmi = activeLoans.stream()
                .mapToDouble(Loan::getEmiAmount)
                .max()
                .orElse(0.0);
        
        Double lowestEmi = activeLoans.stream()
                .mapToDouble(Loan::getEmiAmount)
                .min()
                .orElse(0.0);
        
        Double averageEmi = activeLoans.stream()
                .mapToDouble(Loan::getEmiAmount)
                .average()
                .orElse(0.0);
        
        // Total monthly EMI collection (sum of all active loan EMIs)
        Double totalMonthlyEmiCollection = activeLoans.stream()
                .mapToDouble(Loan::getEmiAmount)
                .sum();
        
        // Upcoming EMI amount (EMIs due in next 30 days that are pending)
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        
        Double upcomingEmiAmount = activeLoans.stream()
                .flatMap(loan -> loan.getEmiSchedules().stream())
                .filter(emi -> "PENDING".equals(emi.getStatus()))
                .filter(emi -> emi.getDueDate() != null && !emi.getDueDate().isBefore(today) && !emi.getDueDate().isAfter(thirtyDaysFromNow))
                .mapToDouble(EmiSchedule::getAmountDue)
                .sum();
        
        return new EmiInsightsDTO(highestEmi, lowestEmi, averageEmi, totalMonthlyEmiCollection, upcomingEmiAmount);
    }
}
