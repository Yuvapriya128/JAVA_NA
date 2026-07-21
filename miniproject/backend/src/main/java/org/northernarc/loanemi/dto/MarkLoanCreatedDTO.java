package org.northernarc.loanemi.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for marking an approved application as loan-created.
 */
public class MarkLoanCreatedDTO {

    @NotNull(message = "Loan ID is required")
    private Long loanId;

    public MarkLoanCreatedDTO() {
    }

    public MarkLoanCreatedDTO(Long loanId) {
        this.loanId = loanId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
}
