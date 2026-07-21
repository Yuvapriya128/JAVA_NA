package org.northernarc.week5_assess.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException() {
        super();
    }

    public AccountNotFoundException(String message) {
        super(message);
    }
}

