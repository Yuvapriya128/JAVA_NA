package org.northernarc.loanemi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.northernarc.loanemi.dto.EmiPaymentHistoryDTO;
import org.northernarc.loanemi.dto.EmiScheduleDTO;
import org.northernarc.loanemi.enums.EmiStatus;
import org.northernarc.loanemi.model.EmiPayment;
import org.northernarc.loanemi.model.EmiSchedule;
import org.northernarc.loanemi.model.Loan;
import org.northernarc.loanemi.repository.EmiPaymentRepository;
import org.northernarc.loanemi.repository.EmiScheduleRepository;
import org.northernarc.loanemi.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/emis")
@Tag(name = "EMI Management", description = "EMI and Schedule management APIs")
public class EmiController {
    private static final Logger log = LoggerFactory.getLogger(EmiController.class);

    private final EmiScheduleRepository emiScheduleRepository;
    private final EmiPaymentRepository emiPaymentRepository;
    private final LoanRepository loanRepository;

    public EmiController(EmiScheduleRepository emiScheduleRepository, EmiPaymentRepository emiPaymentRepository,
                        LoanRepository loanRepository) {
        this.emiScheduleRepository = emiScheduleRepository;
        this.emiPaymentRepository = emiPaymentRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get upcoming EMIs")
    public Page<EmiScheduleDTO> getUpcomingEmis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get upcoming EMIs requested page={} size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        LocalDate today = LocalDate.now();
        List<EmiSchedule> allEmis = emiScheduleRepository.findAll();
        
        List<EmiSchedule> upcoming = allEmis.stream()
                .filter(e -> e.getDueDate().isAfter(today))
                .filter(e -> !"PAID".equals(e.getStatus()))
                .toList();
        
        return mapToPage(upcoming, pageable);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get pending EMIs")
    public Page<EmiScheduleDTO> getPendingEmis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get pending EMIs requested page={} size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        List<EmiSchedule> allEmis = emiScheduleRepository.findAll();
        
        List<EmiSchedule> pending = allEmis.stream()
                .filter(e -> "PENDING".equals(e.getStatus()))
                .toList();
        
        return mapToPage(pending, pageable);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get overdue EMIs")
    public Page<EmiScheduleDTO> getOverdueEmis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get overdue EMIs requested page={} size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        List<EmiSchedule> allEmis = emiScheduleRepository.findAll();
        
        List<EmiSchedule> overdue = allEmis.stream()
                .filter(e -> "OVERDUE".equals(e.getStatus()))
                .toList();
        
        return mapToPage(overdue, pageable);
    }

    @GetMapping("/due-today")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get EMIs due today")
    public Page<EmiScheduleDTO> getEmisDueToday(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get EMIs due today requested page={} size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        LocalDate today = LocalDate.now();
        List<EmiSchedule> allEmis = emiScheduleRepository.findAll();
        
        List<EmiSchedule> dueToday = allEmis.stream()
                .filter(e -> e.getDueDate().equals(today))
                .filter(e -> !"PAID".equals(e.getStatus()))
                .toList();
        
        return mapToPage(dueToday, pageable);
    }

    @GetMapping("/due-this-week")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get EMIs due this week")
    public Page<EmiScheduleDTO> getEmisDueThisWeek(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get EMIs due this week requested page={} size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        LocalDate today = LocalDate.now();
        LocalDate weekEnd = today.plusDays(7);
        List<EmiSchedule> allEmis = emiScheduleRepository.findAll();
        
        List<EmiSchedule> dueWeek = allEmis.stream()
                .filter(e -> e.getDueDate().isAfter(today) && e.getDueDate().isBefore(weekEnd))
                .filter(e -> !"PAID".equals(e.getStatus()))
                .toList();
        
        return mapToPage(dueWeek, pageable);
    }

    @GetMapping("/by-loan/{loanId}")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get EMIs for a specific loan")
    public Page<EmiScheduleDTO> getEmisByLoan(
            @PathVariable Long loanId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get EMIs for loanId={} requested page={} size={}", loanId, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));
        
        List<EmiSchedule> emis = loan.getEmiSchedules() != null ? loan.getEmiSchedules() : List.of();
        
        return mapToPage(emis, pageable);
    }

    @GetMapping("/by-customer/{customerId}")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get EMIs for a specific customer")
    public Page<EmiScheduleDTO> getEmisByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get EMIs for customerId={} requested page={} size={}", customerId, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        List<EmiSchedule> allEmis = emiScheduleRepository.findAll();
        
        List<EmiSchedule> customerEmis = allEmis.stream()
                .filter(e -> e.getLoan().getCustomer().getCustomerId().equals(customerId))
                .toList();
        
        return mapToPage(customerEmis, pageable);
    }

    private Page<EmiScheduleDTO> mapToPage(List<EmiSchedule> emis, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), emis.size());
        
        List<EmiScheduleDTO> content = emis.subList(start, end).stream()
                .map(this::mapToDTO)
                .toList();
        
        return new PageImpl<>(content, pageable, emis.size());
    }

    private EmiScheduleDTO mapToDTO(EmiSchedule schedule) {
        return new EmiScheduleDTO(
                schedule.getEmiId(),
                schedule.getLoan().getLoanId(),
                schedule.getInstallmentNumber(),
                schedule.getDueDate(),
                schedule.getPrincipalComponent(),
                schedule.getInterestComponent(),
                schedule.getAmountDue(),
                schedule.getAmountPaid(),
                schedule.getStatus(),
                schedule.getPenaltyAmount(),
                schedule.getDaysPastDue()
        );
    }
}
