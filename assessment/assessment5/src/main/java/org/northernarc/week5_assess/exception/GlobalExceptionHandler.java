package org.northernarc.week5_assess.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<Object> handleInvalidRequest(InvalidRequestException ex) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Object> handleUnauthorized(UnauthorizedException ex) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneric(Exception ex) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}

