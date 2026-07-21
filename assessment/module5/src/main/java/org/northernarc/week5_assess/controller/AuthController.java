package org.northernarc.week5_assess.controller;

import org.northernarc.week5_assess.dto.AuthRequestDto;
import org.northernarc.week5_assess.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<Object> register(@RequestBody AuthRequestDto authRequestDto) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody AuthRequestDto authRequestDto) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}
}

