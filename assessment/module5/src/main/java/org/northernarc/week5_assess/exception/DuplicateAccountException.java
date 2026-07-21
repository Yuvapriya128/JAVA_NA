package org.northernarc.week5_assess.exception;

public class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException() {
        super();
    }

    public DuplicateAccountException(String message) {
        super(message);
    }
}

