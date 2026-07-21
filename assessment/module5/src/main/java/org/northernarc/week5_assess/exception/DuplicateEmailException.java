package org.northernarc.week5_assess.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super();
    }

    public DuplicateEmailException(String message) {
        super(message);
    }
}

