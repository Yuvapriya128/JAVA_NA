package org.northernarc.loanemi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to create a duplicate loan for an application.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateLoanException extends RuntimeException {
    
    private final Long applicationId;
    private final Long existingLoanId;
    
    public DuplicateLoanException(String message) {
        super(message);
        this.applicationId = null;
        this.existingLoanId = null;
    }
    
    public DuplicateLoanException(String message, Long applicationId, Long existingLoanId) {
        super(message);
        this.applicationId = applicationId;
        this.existingLoanId = existingLoanId;
    }
    
    public Long getApplicationId() {
        return applicationId;
    }
    
    public Long getExistingLoanId() {
        return existingLoanId;
    }
}
