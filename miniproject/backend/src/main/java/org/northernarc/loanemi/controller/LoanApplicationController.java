package org.northernarc.loanemi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.northernarc.loanemi.dto.ApplicationStatusUpdateDTO;
import org.northernarc.loanemi.dto.ApplicationTimelineDTO;
import org.northernarc.loanemi.dto.CreateLoanApplicationRequest;
import org.northernarc.loanemi.dto.LoanApplicationDTO;
import org.northernarc.loanemi.dto.MarkLoanCreatedDTO;
import org.northernarc.loanemi.dto.UpdateApplicationRequestDTO;
import org.northernarc.loanemi.service.LoanApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/loan-products")
@Tag(name = "Loan Applications", description = "Loan application management APIs")
public class LoanApplicationController {
    private static final Logger log = LoggerFactory.getLogger(LoanApplicationController.class);

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping("/apply")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Apply for a loan product")
    public LoanApplicationDTO applyForLoan(
            @Valid @RequestBody CreateLoanApplicationRequest request,
            Authentication authentication) {
        log.info("Loan application requested for loanType={} email={}", 
                request.getLoanType(), authentication.getName());
        return loanApplicationService.applyForLoan(request, authentication.getName());
    }

    @GetMapping("/applications/{applicationId}")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get loan application by ID")
    public LoanApplicationDTO getApplication(
            @PathVariable Long applicationId,
            Authentication authentication) {
        log.info("Get application requested for applicationId={}", applicationId);
        return loanApplicationService.getApplication(applicationId, authentication.getName());
    }

    @GetMapping("/my-applications")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get current user's loan applications")
    public Page<LoanApplicationDTO> getMyApplications(
            Authentication authentication,
            Pageable pageable) {
        log.info("Get my applications requested for email={}", authentication.getName());
        return loanApplicationService.getCustomerApplications(authentication.getName(), pageable);
    }

    @GetMapping("/my-applications/{applicationId}/timeline")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Get timeline/history for a specific application")
    public List<ApplicationTimelineDTO> getApplicationTimeline(
            @PathVariable Long applicationId,
            Authentication authentication) {
        log.info("Get application timeline requested for applicationId={} by={}", applicationId, authentication.getName());
        return loanApplicationService.getApplicationTimeline(applicationId, authentication.getName());
    }

    @PatchMapping("/my-applications/{applicationId}/withdraw")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Withdraw a pending loan application")
    public LoanApplicationDTO withdrawApplication(
            @PathVariable Long applicationId,
            Authentication authentication) {
        log.info("Withdraw application requested for applicationId={} by={}", applicationId, authentication.getName());
        return loanApplicationService.withdrawApplication(applicationId, authentication.getName());
    }

    @PatchMapping("/my-applications/{applicationId}")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Edit a pending loan application (update principal, tenure, interest rate)")
    public LoanApplicationDTO updateApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationRequestDTO updateDTO,
            Authentication authentication) {
        log.info("Update application requested for applicationId={} by={}", applicationId, authentication.getName());
        return loanApplicationService.updateApplication(applicationId, updateDTO, authentication.getName());
    }

    @PostMapping("/applications/{applicationId}/reapply")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Re-apply for a previously rejected application")
    public LoanApplicationDTO reapply(
            @PathVariable Long applicationId,
            Authentication authentication) {
        log.info("Reapply requested for applicationId={} by={}", applicationId, authentication.getName());
        return loanApplicationService.reapply(applicationId, authentication.getName());
    }

    @GetMapping("/pending-applications")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Get pending loan applications for review")
    public Page<LoanApplicationDTO> getPendingApplications(Pageable pageable) {
        log.info("Get pending applications requested");
        return loanApplicationService.getPendingApplications(pageable);
    }

    @GetMapping("/applications")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Get loan applications for review queue with optional status filter")
    public Page<LoanApplicationDTO> getApplicationsForReview(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        log.info("Get applications for review requested with status={} page={} size={}", status, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "applicationDate"));
        return loanApplicationService.getApplicationsForReview(status, pageable);
    }

    @PatchMapping("/applications/{applicationId}/status")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Update loan application status (approve/reject)")
    public LoanApplicationDTO updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationStatusUpdateDTO updateDTO,
            Authentication authentication) {
        log.info("Update application status requested for applicationId={} newStatus={} by={}", 
                applicationId, updateDTO.getStatus(), authentication.getName());
        return loanApplicationService.updateApplicationStatus(applicationId, updateDTO, authentication.getName());
    }

    @PatchMapping("/applications/{applicationId}/mark-loan-created")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @Operation(summary = "Mark an approved application as loan-created by linking to an existing loan")
    public LoanApplicationDTO markLoanCreated(
            @PathVariable Long applicationId,
            @Valid @RequestBody MarkLoanCreatedDTO request,
            Authentication authentication) {
        log.info("Mark loan-created requested for applicationId={} loanId={} by={}", 
                applicationId, request.getLoanId(), authentication.getName());
        return loanApplicationService.markLoanCreated(applicationId, request.getLoanId(), authentication.getName());
    }
}
