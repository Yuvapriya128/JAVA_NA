package org.northernarc.week5_assess.controller;

import org.northernarc.week5_assess.dto.AccountDto;
import org.northernarc.week5_assess.dto.TransferRequestDto;
import org.northernarc.week5_assess.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping
	public ResponseEntity<Object> createAccount(@RequestBody AccountDto accountDto) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@GetMapping
	public ResponseEntity<Object> getAllAccounts() {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getAccountById(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDto) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteAccount(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@PostMapping("/deposit")
	public ResponseEntity<Object> deposit(@RequestBody TransferRequestDto transferRequestDto) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@PostMapping("/withdraw")
	public ResponseEntity<Object> withdraw(@RequestBody TransferRequestDto transferRequestDto) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@PostMapping("/transfer")
	public ResponseEntity<Object> transfer(@RequestBody TransferRequestDto transferRequestDto) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}
}

