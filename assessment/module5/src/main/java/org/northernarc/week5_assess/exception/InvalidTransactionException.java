package org.northernarc.week5_assess.exception;

public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException() {
        super();
    }

    public InvalidTransactionException(String message) {
        super(message);
    }
}

