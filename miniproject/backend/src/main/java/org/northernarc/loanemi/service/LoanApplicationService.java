package org.northernarc.loanemi.service;

import org.northernarc.loanemi.dto.ApplicationStatusUpdateDTO;
import org.northernarc.loanemi.dto.ApplicationTimelineDTO;
import org.northernarc.loanemi.dto.CreateLoanApplicationRequest;
import org.northernarc.loanemi.dto.LoanApplicationDTO;
import org.northernarc.loanemi.dto.UpdateApplicationRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoanApplicationService {
    LoanApplicationDTO applyForLoan(CreateLoanApplicationRequest request, String email);
    
    LoanApplicationDTO getApplication(Long applicationId, String email);
    
    Page<LoanApplicationDTO> getCustomerApplications(String email, Pageable pageable);
    
    Page<LoanApplicationDTO> getPendingApplications(Pageable pageable);
    
    /**
     * Get all applications with optional status filter for review queue.
     * @param status Optional status filter (null or "ALL" returns all statuses)
     * @param pageable Pagination info
     * @return Page of applications
     */
    Page<LoanApplicationDTO> getApplicationsForReview(String status, Pageable pageable);
    
    /**
     * Update application status (approve/reject).
     * @param applicationId Application ID
     * @param updateDTO Status update request
     * @param reviewerEmail Reviewer's email
     * @return Updated application
     */
    LoanApplicationDTO updateApplicationStatus(Long applicationId, ApplicationStatusUpdateDTO updateDTO, String reviewerEmail);
    
    LoanApplicationDTO approveLoanApplication(Long applicationId, String approverEmail);
    
    LoanApplicationDTO rejectLoanApplication(Long applicationId, String rejectionReason, String approverEmail);
    
    /**
     * Get application timeline/history.
     * @param applicationId Application ID
     * @param email User's email for authorization
     * @return List of timeline events
     */
    List<ApplicationTimelineDTO> getApplicationTimeline(Long applicationId, String email);
    
    /**
     * Re-apply for a previously rejected application.
     * @param applicationId Original application ID
     * @param email User's email
     * @return New application DTO
     */
    LoanApplicationDTO reapply(Long applicationId, String email);
    
    /**
     * Withdraw a pending application.
     * @param applicationId Application ID
     * @param email User's email
     * @return Updated application DTO
     */
    LoanApplicationDTO withdrawApplication(Long applicationId, String email);
    
    /**
     * Update a pending application (edit loan details).
     * @param applicationId Application ID
     * @param updateDTO Update request with new values
     * @param email User's email
     * @return Updated application DTO
     */
    LoanApplicationDTO updateApplication(Long applicationId, UpdateApplicationRequestDTO updateDTO, String email);
    
    /**
     * Mark an approved application as loan-created by linking it to an existing loan.
     * @param applicationId Application ID (must be APPROVED)
     * @param loanId Loan ID to link
     * @param reviewerEmail Reviewer's email
     * @return Updated application DTO
     */
    LoanApplicationDTO markLoanCreated(Long applicationId, Long loanId, String reviewerEmail);
    
    /**
     * Link a loan to an application (called when loan is created with applicationId).
     * @param applicationId Application ID
     * @param loanId Loan ID
     */
    void linkLoanToApplication(Long applicationId, Long loanId);
}
