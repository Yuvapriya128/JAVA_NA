package org.northernarc.loanemi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when idempotency key conflict is detected.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class IdempotencyConflictException extends RuntimeException {
    
    private final String idempotencyKey;
    
    public IdempotencyConflictException(String message) {
        super(message);
        this.idempotencyKey = null;
    }
    
    public IdempotencyConflictException(String message, String idempotencyKey) {
        super(message);
        this.idempotencyKey = idempotencyKey;
    }
    
    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
