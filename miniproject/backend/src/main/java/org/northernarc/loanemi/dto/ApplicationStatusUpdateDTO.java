package org.northernarc.loanemi.dto;

import jakarta.validation.constraints.NotNull;
import org.northernarc.loanemi.enums.LoanApplicationStatus;

/**
 * DTO for updating loan application status.
 */
public class ApplicationStatusUpdateDTO {

    @NotNull(message = "Status is required")
    private LoanApplicationStatus status;

    private String rejectionReason;

    public ApplicationStatusUpdateDTO() {
    }

    public ApplicationStatusUpdateDTO(LoanApplicationStatus status, String rejectionReason) {
        this.status = status;
        this.rejectionReason = rejectionReason;
    }

    public LoanApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(LoanApplicationStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
