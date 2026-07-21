package org.northernarc.week5_assess.exception;

public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String message) {
		super(message);
	}
}

