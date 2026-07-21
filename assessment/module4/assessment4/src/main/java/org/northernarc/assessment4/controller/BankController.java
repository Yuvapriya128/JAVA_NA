package org.northernarc.assessment4.controller;


import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.northernarc.assessment4.dto.CustomerSummaryDTO;
import org.northernarc.assessment4.dto.DashboardResponse;
import org.northernarc.assessment4.model.Account;
import org.northernarc.assessment4.model.Customer;
import org.northernarc.assessment4.model.Transaction;
import org.northernarc.assessment4.service.BankService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Banking", description = "Core banking operations")
@SecurityRequirement(name = "bearerAuth")
public class BankController {

    private final BankService bankService;

    // --- Core Onboarding & Setup Endpoints ---
    @PostMapping("/customers")
    @Operation(summary = "Create customer")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        return new ResponseEntity<>(bankService.saveCustomer(customer), HttpStatus.CREATED);
    }

    @PostMapping("/accounts")
    @Operation(summary = "Create account")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) {
        return new ResponseEntity<>(bankService.saveAccount(account), HttpStatus.CREATED);
    }

    // --- Task 6: Pagination & Sorting (Default: Balance DESC) ---
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @GetMapping("/accounts")
    @Operation(summary = "Get paginated accounts", description = "Default sort is balance descending")
    public ResponseEntity<Page<Account>> getAllAccounts(
            @PageableDefault(page = 0, size = 10, sort = "balance", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(bankService.getAllAccountsPaginated(pageable), HttpStatus.OK);
    }

    // --- Task 9: Role Based Access Control Endpoints ---
    @DeleteMapping("/accounts/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN')") // Task 9: ADMIN can delete accounts
    @Operation(summary = "Delete account (ADMIN only)")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        bankService.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/accounts/{accountNumber}/balance")
    @PreAuthorize("hasRole('MANAGER')") // Task 9: MANAGER can update accounts
    @Operation(summary = "Increase account balance (MANAGER only)")
    public ResponseEntity<Void> updateAccountBalance(
            @PathVariable String accountNumber,
            @RequestParam double amount) {
        bankService.increaseAccountBalance(accountNumber, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/accounts/view")
    @PreAuthorize("hasRole('USER')") // Task 9: USER can only view accounts
    @Operation(summary = "View accounts by type (USER only)")
    public ResponseEntity<List<Account>> viewAccountsByType(@RequestParam String type) {
        return ResponseEntity.ok(bankService.getAccountsByType(type));
    }

    // --- Task 7: DTO Projection Aggregation ---
    @GetMapping("/customers/{customerId}/summary")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get customer summary DTO")
    public ResponseEntity<CustomerSummaryDTO> getCustomerSummary(@PathVariable Long customerId) {
        return ResponseEntity.ok(bankService.getCustomerSummary(customerId));
    }

    // --- Task 3 & 4: Query Operations ---
    @GetMapping("/customers/rich")
    public ResponseEntity<List<Customer>> getRichCustomers(@RequestParam double threshold) {
        return ResponseEntity.ok(bankService.getRichCustomers(threshold));
    }

    @GetMapping("/branches/balances")
    public ResponseEntity<Map<String, Double>> getBranchBalances() {
        return ResponseEntity.ok(bankService.getTotalBalancePerBranch());
    }

    @GetMapping("/customers/multiple-accounts")
    public ResponseEntity<List<Customer>> getCustomersWithMultipleAccounts() {
        return ResponseEntity.ok(bankService.getCustomersWithMultipleAccounts());
    }

    @GetMapping("/transactions/latest")
    public ResponseEntity<Transaction> getLatestTransaction() {
        return ResponseEntity.ok(bankService.getLatestTransaction());
    }

    @GetMapping("/accounts/idle")
    public ResponseEntity<List<Account>> getIdleAccounts() {
        return ResponseEntity.ok(bankService.getAccountsWithNoTransactions());
    }

    // --- Final Challenge: Metrics Dashboard Generation ---
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard metrics")
    public ResponseEntity<DashboardResponse> getDashboardMetrics() {
        return ResponseEntity.ok(bankService.getDashboardMetrics());
    }
}
