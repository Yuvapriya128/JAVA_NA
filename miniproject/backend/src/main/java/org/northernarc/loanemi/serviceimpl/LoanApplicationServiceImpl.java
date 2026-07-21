package org.northernarc.loanemi.serviceimpl;

import org.northernarc.loanemi.dto.ApplicationStatusUpdateDTO;
import org.northernarc.loanemi.dto.ApplicationTimelineDTO;
import org.northernarc.loanemi.dto.CreateLoanApplicationRequest;
import org.northernarc.loanemi.dto.CreateLoanRequest;
import org.northernarc.loanemi.dto.LoanApplicationDTO;
import org.northernarc.loanemi.dto.LoanSummaryDTO;
import org.northernarc.loanemi.dto.UpdateApplicationRequestDTO;
import org.northernarc.loanemi.enums.ApplicationEventType;
import org.northernarc.loanemi.enums.LoanApplicationStatus;
import org.northernarc.loanemi.enums.NotificationType;
import org.northernarc.loanemi.enums.Role;
import org.northernarc.loanemi.exception.CustomerNotFoundException;
import org.northernarc.loanemi.exception.ValidationException;
import org.northernarc.loanemi.model.ApplicationStatusHistory;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.model.Loan;
import org.northernarc.loanemi.model.LoanApplication;
import org.northernarc.loanemi.model.Notification;
import org.northernarc.loanemi.repository.ApplicationStatusHistoryRepository;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.repository.LoanApplicationRepository;
import org.northernarc.loanemi.repository.LoanRepository;
import org.northernarc.loanemi.repository.NotificationRepository;
import org.northernarc.loanemi.service.LoanApplicationService;
import org.northernarc.loanemi.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanApplicationServiceImpl implements LoanApplicationService {
    private static final Logger log = LoggerFactory.getLogger(LoanApplicationServiceImpl.class);

    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRepository customerRepository;
    private final LoanService loanService;
    private final LoanRepository loanRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final NotificationRepository notificationRepository;

    public LoanApplicationServiceImpl(
            LoanApplicationRepository loanApplicationRepository,
            CustomerRepository customerRepository,
            LoanService loanService,
            LoanRepository loanRepository,
            ApplicationStatusHistoryRepository statusHistoryRepository,
            NotificationRepository notificationRepository) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.customerRepository = customerRepository;
        this.loanService = loanService;
        this.loanRepository = loanRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public LoanApplicationDTO applyForLoan(CreateLoanApplicationRequest request, String email) {
        log.info("Processing loan application for email={} loanType={}", email, request.getLoanType());
        
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));

        if (!customer.isActive()) {
            throw new IllegalStateException("Cannot apply for loan. Customer account is inactive.");
        }

        // Check for duplicate active application
        if (loanApplicationRepository.existsActiveApplicationByCustomerAndLoanType(customer, request.getLoanType())) {
            throw new IllegalStateException("An active application already exists for this loan type.");
        }

        LoanApplication application = new LoanApplication();
        application.setCustomer(customer);
        application.setLoanType(request.getLoanType());
        application.setPrincipalAmount(request.getPrincipalAmount());
        application.setTenureMonths(request.getTenureMonths());
        application.setAnnualInterestRate(request.getAnnualInterestRate());
        application.setApplicationStatus(LoanApplicationStatus.PENDING);

        LoanApplication saved = loanApplicationRepository.save(application);
        
        // Record timeline event
        recordTimelineEvent(saved, ApplicationEventType.SUBMITTED, customer, 
                "Application submitted for " + request.getLoanType() + " loan", null, "PENDING");
        
        log.info("Loan application created applicationId={} customerId={}", saved.getApplicationId(), customer.getCustomerId());
        
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanApplicationDTO getApplication(Long applicationId, String email) {
        log.info("Fetching application applicationId={} for email={}", applicationId, email);
        
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));

        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        if (!application.getCustomer().getCustomerId().equals(customer.getCustomerId()) 
            && !customer.getRole().equals(Role.MANAGER) 
            && !customer.getRole().equals(Role.ADMIN)) {
            throw new IllegalAccessError("Not authorized to view this application");
        }

        return mapToDTO(application);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanApplicationDTO> getCustomerApplications(String email, Pageable pageable) {
        log.info("Fetching applications for email={}", email);
        
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));

        return loanApplicationRepository.findByCustomer(customer, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanApplicationDTO> getPendingApplications(Pageable pageable) {
        log.info("Fetching pending applications");
        return loanApplicationRepository.findByApplicationStatus(LoanApplicationStatus.PENDING, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanApplicationDTO> getApplicationsForReview(String status, Pageable pageable) {
        log.info("Fetching applications for review with status filter={}", status);
        
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            // Return all applications
            return loanApplicationRepository.findAll(pageable).map(this::mapToDTO);
        }
        
        try {
            LoanApplicationStatus statusEnum = LoanApplicationStatus.valueOf(status.toUpperCase());
            return loanApplicationRepository.findByApplicationStatus(statusEnum, pageable).map(this::mapToDTO);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status filter provided: {}", status);
            throw new ValidationException("Invalid status: " + status + ". Valid values are: PENDING, UNDER_REVIEW, APPROVED, REJECTED, WITHDRAWN, ALL");
        }
    }

    @Override
    public LoanApplicationDTO updateApplicationStatus(Long applicationId, ApplicationStatusUpdateDTO updateDTO, String reviewerEmail) {
        log.info("Updating application status applicationId={} newStatus={} by reviewer={}", 
                applicationId, updateDTO.getStatus(), reviewerEmail);
        
        Customer reviewer = customerRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new CustomerNotFoundException("Reviewer not found: " + reviewerEmail));

        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        // Validate current status allows transition
        LoanApplicationStatus currentStatus = application.getApplicationStatus();
        if (currentStatus != LoanApplicationStatus.PENDING && currentStatus != LoanApplicationStatus.UNDER_REVIEW) {
            throw new ValidationException("Cannot update status. Only PENDING or UNDER_REVIEW applications can be modified. Current status: " + currentStatus);
        }

        LoanApplicationStatus newStatus = updateDTO.getStatus();
        
        // Handle status transitions
        switch (newStatus) {
            case APPROVED:
                return processApproval(application, reviewer);
                
            case REJECTED:
                if (updateDTO.getRejectionReason() == null || updateDTO.getRejectionReason().isBlank()) {
                    throw new ValidationException("Rejection reason is required when rejecting an application");
                }
                return processRejection(application, updateDTO.getRejectionReason(), reviewer);
                
            case UNDER_REVIEW:
                String previousStatus = application.getApplicationStatus().name();
                application.setApplicationStatus(LoanApplicationStatus.UNDER_REVIEW);
                LoanApplication saved = loanApplicationRepository.save(application);
                
                recordTimelineEvent(saved, ApplicationEventType.UNDER_REVIEW, reviewer, 
                        "Application is being reviewed by " + reviewer.getCustomerName(), previousStatus, "UNDER_REVIEW");
                
                // Notify customer
                createNotification(application.getCustomer(), NotificationType.APPLICATION_UNDER_REVIEW,
                        "Application Under Review", 
                        "Your " + application.getLoanType() + " loan application is now under review.",
                        applicationId, "LOAN_APPLICATION");
                
                log.info("AUDIT: APPLICATION_STATUS_UPDATE - applicationId={}, newStatus=UNDER_REVIEW, reviewerId={}, timestamp={}", 
                        applicationId, reviewer.getCustomerId(), LocalDateTime.now());
                return mapToDTO(saved);
                
            default:
                throw new ValidationException("Invalid status transition. Only APPROVED, REJECTED, or UNDER_REVIEW are allowed.");
        }
    }

    @Override
    public LoanApplicationDTO approveLoanApplication(Long applicationId, String approverEmail) {
        log.info("Approving application applicationId={} by approver={}", applicationId, approverEmail);
        
        Customer approver = customerRepository.findByEmail(approverEmail)
                .orElseThrow(() -> new CustomerNotFoundException("Approver not found: " + approverEmail));

        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        LoanApplicationStatus currentStatus = application.getApplicationStatus();
        if (currentStatus != LoanApplicationStatus.PENDING && currentStatus != LoanApplicationStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Can only approve PENDING or UNDER_REVIEW applications");
        }

        return processApproval(application, approver);
    }

    @Override
    public LoanApplicationDTO rejectLoanApplication(Long applicationId, String rejectionReason, String approverEmail) {
        log.info("Rejecting application applicationId={} by approver={}", applicationId, approverEmail);
        
        Customer approver = customerRepository.findByEmail(approverEmail)
                .orElseThrow(() -> new CustomerNotFoundException("Approver not found: " + approverEmail));

        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        LoanApplicationStatus currentStatus = application.getApplicationStatus();
        if (currentStatus != LoanApplicationStatus.PENDING && currentStatus != LoanApplicationStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Can only reject PENDING or UNDER_REVIEW applications");
        }

        return processRejection(application, rejectionReason, approver);
    }

    private LoanApplicationDTO processApproval(LoanApplication application, Customer approver) {
        String previousStatus = application.getApplicationStatus().name();
        
        application.setApplicationStatus(LoanApplicationStatus.APPROVED);
        application.setApprovalDate(LocalDateTime.now());
        application.setApprovedBy(approver);
        application.setRejectionReason(null);

        // Auto-create loan for approval
        CreateLoanRequest createLoanRequest = new CreateLoanRequest();
        createLoanRequest.setCustomerId(application.getCustomer().getCustomerId());
        createLoanRequest.setLoanType(application.getLoanType());
        createLoanRequest.setPrincipalAmount(application.getPrincipalAmount());
        createLoanRequest.setTenureMonths(application.getTenureMonths());
        createLoanRequest.setAnnualInterestRate(application.getAnnualInterestRate());

        LoanSummaryDTO createdLoan = loanService.createLoan(createLoanRequest);
        log.info("Loan created for approved application loanId={}", createdLoan.getLoanId());

        // Link the created loan to the application
        Loan loan = loanRepository.findById(createdLoan.getLoanId()).orElse(null);
        application.setCreatedLoan(loan);

        LoanApplication saved = loanApplicationRepository.save(application);
        
        // Record approval event
        recordTimelineEvent(saved, ApplicationEventType.APPROVED, approver, 
                "Application approved by " + approver.getCustomerName(), previousStatus, "APPROVED");
        
        // Record loan creation event
        recordTimelineEvent(saved, ApplicationEventType.LOAN_CREATED, approver, 
                "Loan #" + createdLoan.getLoanId() + " created", "APPROVED", "APPROVED");
        
        // Notify customer
        createNotification(application.getCustomer(), NotificationType.APPLICATION_APPROVED,
                "Loan Application Approved", 
                "Congratulations! Your " + application.getLoanType() + " loan application has been approved. Loan ID: " + createdLoan.getLoanId(),
                application.getApplicationId(), "LOAN_APPLICATION");
        
        log.info("AUDIT: APPLICATION_APPROVED - applicationId={}, reviewerId={}, customerId={}, loanId={}, timestamp={}", 
                application.getApplicationId(), approver.getCustomerId(), 
                application.getCustomer().getCustomerId(), createdLoan.getLoanId(), LocalDateTime.now());
        
        return mapToDTO(saved);
    }

    private LoanApplicationDTO processRejection(LoanApplication application, String rejectionReason, Customer approver) {
        String previousStatus = application.getApplicationStatus().name();
        
        application.setApplicationStatus(LoanApplicationStatus.REJECTED);
        application.setApprovalDate(LocalDateTime.now());
        application.setRejectionReason(rejectionReason);
        application.setApprovedBy(approver);

        LoanApplication saved = loanApplicationRepository.save(application);
        
        // Record timeline event
        recordTimelineEvent(saved, ApplicationEventType.REJECTED, approver, 
                "Application rejected by " + approver.getCustomerName() + ". Reason: " + rejectionReason, previousStatus, "REJECTED");
        
        // Notify customer
        createNotification(application.getCustomer(), NotificationType.APPLICATION_REJECTED,
                "Loan Application Rejected", 
                "Your " + application.getLoanType() + " loan application has been rejected. Reason: " + rejectionReason,
                application.getApplicationId(), "LOAN_APPLICATION");
        
        log.info("AUDIT: APPLICATION_REJECTED - applicationId={}, reviewerId={}, customerId={}, reason={}, timestamp={}", 
                application.getApplicationId(), approver.getCustomerId(), 
                application.getCustomer().getCustomerId(), rejectionReason, LocalDateTime.now());
        
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationTimelineDTO> getApplicationTimeline(Long applicationId, String email) {
        log.info("Fetching timeline for applicationId={} by email={}", applicationId, email);
        
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));

        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        // Check authorization
        if (!application.getCustomer().getCustomerId().equals(customer.getCustomerId()) 
            && !customer.getRole().equals(Role.MANAGER) 
            && !customer.getRole().equals(Role.ADMIN)) {
            throw new IllegalAccessError("Not authorized to view this application timeline");
        }

        List<ApplicationStatusHistory> history = statusHistoryRepository.findByApplicationIdOrderByEventTimestampAsc(applicationId);
        
        return history.stream()
                .map(this::mapToTimelineDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LoanApplicationDTO reapply(Long applicationId, String email) {
        log.info("Re-applying for applicationId={} by email={}", applicationId, email);
        
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));

        LoanApplication originalApplication = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        // Verify ownership
        if (!originalApplication.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new IllegalAccessError("Not authorized to reapply for this application");
        }

        // Only rejected or withdrawn applications can be reapplied
        LoanApplicationStatus status = originalApplication.getApplicationStatus();
        if (status != LoanApplicationStatus.REJECTED && status != LoanApplicationStatus.WITHDRAWN) {
            throw new ValidationException("Can only reapply for REJECTED or WITHDRAWN applications. Current status: " + status);
        }

        // Check for existing active application of same type
        if (loanApplicationRepository.existsActiveApplicationByCustomerAndLoanType(customer, originalApplication.getLoanType())) {
            throw new IllegalStateException("An active application already exists for this loan type.");
        }

        // Create new application from original
        LoanApplication newApplication = new LoanApplication();
        newApplication.setCustomer(customer);
        newApplication.setLoanType(originalApplication.getLoanType());
        newApplication.setPrincipalAmount(originalApplication.getPrincipalAmount());
        newApplication.setTenureMonths(originalApplication.getTenureMonths());
        newApplication.setAnnualInterestRate(originalApplication.getAnnualInterestRate());
        newApplication.setApplicationStatus(LoanApplicationStatus.PENDING);

        LoanApplication saved = loanApplicationRepository.save(newApplication);
        
        // Record timeline event
        recordTimelineEvent(saved, ApplicationEventType.REAPPLIED, customer, 
                "Re-application submitted (original application #" + applicationId + ")", null, "PENDING");
        
        log.info("Re-application created applicationId={} from original={}", saved.getApplicationId(), applicationId);
        
        return mapToDTO(saved);
    }

    @Override
    public LoanApplicationDTO withdrawApplication(Long applicationId, String email) {
        log.info("Withdrawing applicationId={} by email={}", applicationId, email);
        
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));

        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        // Verify ownership
        if (!application.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new IllegalAccessError("Not authorized to withdraw this application");
        }

        // Only pending applications can be withdrawn
        if (application.getApplicationStatus() != LoanApplicationStatus.PENDING) {
            throw new ValidationException("Can only withdraw PENDING applications. Current status: " + application.getApplicationStatus());
        }

        String previousStatus = application.getApplicationStatus().name();
        application.setApplicationStatus(LoanApplicationStatus.WITHDRAWN);

        LoanApplication saved = loanApplicationRepository.save(application);
        
        // Record timeline event
        recordTimelineEvent(saved, ApplicationEventType.WITHDRAWN, customer, 
                "Application withdrawn by applicant", previousStatus, "WITHDRAWN");
        
        log.info("AUDIT: APPLICATION_WITHDRAWN - applicationId={}, customerId={}, timestamp={}", 
                applicationId, customer.getCustomerId(), LocalDateTime.now());
        
        return mapToDTO(saved);
    }

    @Override
    public LoanApplicationDTO updateApplication(Long applicationId, UpdateApplicationRequestDTO updateDTO, String email) {
        log.info("Updating applicationId={} by email={}", applicationId, email);
        
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email));

        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        // Verify ownership
        if (!application.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new IllegalAccessError("Not authorized to update this application");
        }

        // Only pending applications can be edited
        if (application.getApplicationStatus() != LoanApplicationStatus.PENDING) {
            throw new ValidationException("Can only edit PENDING applications. Current status: " + application.getApplicationStatus());
        }

        StringBuilder changes = new StringBuilder("Application updated: ");
        boolean hasChanges = false;

        // Update fields if provided
        if (updateDTO.getPrincipalAmount() != null) {
            changes.append("Principal ₹").append(application.getPrincipalAmount())
                   .append(" → ₹").append(updateDTO.getPrincipalAmount()).append("; ");
            application.setPrincipalAmount(updateDTO.getPrincipalAmount());
            hasChanges = true;
        }
        if (updateDTO.getTenureMonths() != null) {
            changes.append("Tenure ").append(application.getTenureMonths())
                   .append(" → ").append(updateDTO.getTenureMonths()).append(" months; ");
            application.setTenureMonths(updateDTO.getTenureMonths());
            hasChanges = true;
        }
        if (updateDTO.getAnnualInterestRate() != null) {
            changes.append("Interest ").append(application.getAnnualInterestRate())
                   .append("% → ").append(updateDTO.getAnnualInterestRate()).append("%; ");
            application.setAnnualInterestRate(updateDTO.getAnnualInterestRate());
            hasChanges = true;
        }

        if (!hasChanges) {
            throw new ValidationException("No changes provided");
        }

        LoanApplication saved = loanApplicationRepository.save(application);
        
        // Record timeline event
        recordTimelineEvent(saved, ApplicationEventType.UPDATED, customer, 
                changes.toString(), "PENDING", "PENDING");
        
        log.info("AUDIT: APPLICATION_UPDATED - applicationId={}, customerId={}, changes={}, timestamp={}", 
                applicationId, customer.getCustomerId(), changes, LocalDateTime.now());
        
        return mapToDTO(saved);
    }

    private void recordTimelineEvent(LoanApplication application, ApplicationEventType eventType, 
                                      Customer actor, String message, String previousStatus, String newStatus) {
        ApplicationStatusHistory history = new ApplicationStatusHistory();
        history.setApplication(application);
        history.setEventType(eventType);
        history.setActor(actor);
        history.setActorName(actor != null ? actor.getCustomerName() : "System");
        history.setActorRole(actor != null && actor.getRole() != null ? actor.getRole().name() : null);
        history.setMessage(message);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setEventTimestamp(LocalDateTime.now());
        
        statusHistoryRepository.save(history);
    }

    private void createNotification(Customer customer, NotificationType type, String title, String message,
                                    Long relatedEntityId, String relatedEntityType) {
        Notification notification = new Notification(customer, type, title, message, relatedEntityId, relatedEntityType);
        notificationRepository.save(notification);
    }

    private ApplicationTimelineDTO mapToTimelineDTO(ApplicationStatusHistory history) {
        return new ApplicationTimelineDTO(
                history.getId(),
                history.getEventType(),
                history.getEventTimestamp(),
                history.getActorRole(),
                history.getActorName(),
                history.getMessage(),
                history.getPreviousStatus(),
                history.getNewStatus()
        );
    }

    private LoanApplicationDTO mapToDTO(LoanApplication application) {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setApplicationId(application.getApplicationId());
        dto.setCustomerId(application.getCustomer().getCustomerId());
        dto.setCustomerName(application.getCustomer().getCustomerName());
        dto.setLoanType(application.getLoanType());
        dto.setPrincipalAmount(application.getPrincipalAmount());
        dto.setTenureMonths(application.getTenureMonths());
        dto.setAnnualInterestRate(application.getAnnualInterestRate());
        dto.setApplicationStatus(application.getApplicationStatus());
        dto.setApplicationDate(application.getApplicationDate());
        dto.setApprovalDate(application.getApprovalDate());
        dto.setLastUpdatedAt(application.getLastUpdatedAt());
        dto.setRejectionReason(application.getRejectionReason());
        
        // Loan linkage
        if (application.getCreatedLoan() != null) {
            dto.setLoanId(application.getCreatedLoan().getLoanId());
            dto.setLoanCreatedAt(application.getCreatedLoan().getDisbursementDate() != null 
                    ? application.getCreatedLoan().getDisbursementDate().atStartOfDay() 
                    : application.getApprovalDate());
        }
        
        return dto;
    }

    @Override
    public LoanApplicationDTO markLoanCreated(Long applicationId, Long loanId, String reviewerEmail) {
        log.info("Marking application applicationId={} as loan-created with loanId={} by={}", 
                applicationId, loanId, reviewerEmail);
        
        Customer reviewer = customerRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new CustomerNotFoundException("Reviewer not found: " + reviewerEmail));

        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        // Only APPROVED applications can be marked as loan-created
        if (application.getApplicationStatus() != LoanApplicationStatus.APPROVED) {
            throw new ValidationException("Only APPROVED applications can be marked as loan-created. Current status: " 
                    + application.getApplicationStatus());
        }

        // Check if already linked to a loan (duplicate linking)
        if (application.getCreatedLoan() != null) {
            throw new IllegalStateException("Application is already linked to loan #" 
                    + application.getCreatedLoan().getLoanId() + ". Duplicate linking not allowed.");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));

        // Link the loan
        application.setCreatedLoan(loan);
        LoanApplication saved = loanApplicationRepository.save(application);
        
        // Record timeline event
        recordTimelineEvent(saved, ApplicationEventType.LOAN_CREATED, reviewer, 
                "Loan #" + loanId + " linked to application by " + reviewer.getCustomerName(), 
                "APPROVED", "APPROVED");
        
        // Notify customer
        createNotification(application.getCustomer(), NotificationType.APPLICATION_APPROVED,
                "Loan Created", 
                "Your " + application.getLoanType() + " loan has been created. Loan ID: " + loanId,
                application.getApplicationId(), "LOAN_APPLICATION");
        
        log.info("AUDIT: LOAN_LINKED - applicationId={}, loanId={}, reviewerId={}, timestamp={}", 
                applicationId, loanId, reviewer.getCustomerId(), LocalDateTime.now());
        
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void linkLoanToApplication(Long applicationId, Long loanId) {
        log.info("Linking loanId={} to applicationId={}", loanId, applicationId);
        
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));

        // Only APPROVED applications can be linked
        if (application.getApplicationStatus() != LoanApplicationStatus.APPROVED) {
            throw new ValidationException("Only APPROVED applications can be linked to loans. Current status: " 
                    + application.getApplicationStatus());
        }

        // Check if already linked
        if (application.getCreatedLoan() != null) {
            throw new IllegalStateException("Application is already linked to loan #" 
                    + application.getCreatedLoan().getLoanId());
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));

        application.setCreatedLoan(loan);
        loanApplicationRepository.save(application);
        
        // Record timeline event (system action)
        recordTimelineEvent(application, ApplicationEventType.LOAN_CREATED, null, 
                "Loan #" + loanId + " created and linked automatically", "APPROVED", "APPROVED");
        
        // Notify customer
        createNotification(application.getCustomer(), NotificationType.APPLICATION_APPROVED,
                "Loan Created", 
                "Your " + application.getLoanType() + " loan has been created. Loan ID: " + loanId,
                application.getApplicationId(), "LOAN_APPLICATION");
        
        log.info("AUDIT: LOAN_AUTO_LINKED - applicationId={}, loanId={}, timestamp={}", 
                applicationId, loanId, LocalDateTime.now());
    }
}
