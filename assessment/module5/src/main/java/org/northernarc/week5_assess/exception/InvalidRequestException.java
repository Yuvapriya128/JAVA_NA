package org.northernarc.week5_assess.exception;

public class InvalidRequestException extends RuntimeException {

	public InvalidRequestException() {
		super();
	}

	public InvalidRequestException(String message) {
		super(message);
	}
}

