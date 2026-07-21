package org.northernarc.assessment4.serviceimpl;

import org.northernarc.assessment4.dto.CustomerSummaryDTO;
import org.northernarc.assessment4.dto.DashboardResponse;
import org.northernarc.assessment4.exception.AccountNotFoundException;
import org.northernarc.assessment4.exception.CustomerNotFoundException;
import org.northernarc.assessment4.exception.ValidationException;
import org.northernarc.assessment4.model.Account;
import org.northernarc.assessment4.model.Role;
import org.northernarc.assessment4.model.Customer;
import org.northernarc.assessment4.model.Transaction;
import org.northernarc.assessment4.repository.AccountRepository;
import org.northernarc.assessment4.repository.CustomerRepository;
import org.northernarc.assessment4.repository.TransactionRepository;
import org.northernarc.assessment4.service.BankService;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankServiceImpl implements BankService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    // --- Core Entity Writing Persistence Methods ---
    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        if (customer.getPassword() != null && !customer.getPassword().startsWith("$2")) {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }
        if (customer.getRole() == null) {
            customer.setRole(String.valueOf(Role.USER));
        }
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(String accountNumber) {
        if (!accountRepository.existsById(accountNumber)) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        accountRepository.deleteById(accountNumber);
    }

    @Override
    public List<Account> getAccountsByType(String accountType) {
        return accountRepository.findByAccountType(accountType);
    }

    @Override
    public List<Customer> getCustomersByBranch(String branch) {
        return customerRepository.findByBranch(branch);
    }

    @Override
    public List<Transaction> getTransactionsByType(String transactionType) {
        return transactionRepository.findByTransactionType(transactionType);
    }

    @Override
    public List<Account> getAccountsWithBalanceGreaterThan(double amount) {
        return accountRepository.findByBalanceGreaterThan(amount);
    }

    @Override
    public List<Customer> getRichCustomers(double threshold) {
        return customerRepository.findRichCustomers(threshold);
    }

    @Override
    public Map<String, Double> getTotalBalancePerBranch() {
        List<Object[]> rows = customerRepository.findTotalBalancePerBranch();
        Map<String, Double> balanceByBranch = new LinkedHashMap<>();
        for (Object[] row : rows) {
            balanceByBranch.put((String) row[0], (Double) row[1]);
        }
        return balanceByBranch;
    }

    @Override
    public List<Customer> getCustomersWithMultipleAccounts() {
        return customerRepository.findCustomersWithMultipleAccounts();
    }

    @Override
    public Transaction getLatestTransaction() {
        return transactionRepository.findLatestTransaction(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Account> getAccountsWithNoTransactions() {
        return accountRepository.findAccountsWithNoTransactions();
    }

    @Override
    @Transactional
    public void increaseAccountBalance(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }
        if (!accountRepository.existsById(accountNumber)) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        accountRepository.increaseBalance(accountNumber, amount);
    }

    @Override
    public Page<Account> getAllAccountsPaginated(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public CustomerSummaryDTO getCustomerSummary(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        long numberOfAccounts = customer.getAccounts().size();
        double totalBalance = customer.getAccounts().stream()
                .mapToDouble(Account::getBalance)
                .sum();
        return new CustomerSummaryDTO(customer.getCustomerName(), customer.getBranch(), numberOfAccounts, totalBalance);
    }

    @Override
    public DashboardResponse getDashboardMetrics() {
        Object[] core = customerRepository.fetchDashboardCoreMetrics().stream().findFirst().orElse(null);

        long totalCustomers = core == null || core[0] == null ? 0L : ((Number) core[0]).longValue();
        long totalAccounts = core == null || core[1] == null ? 0L : ((Number) core[1]).longValue();
        double totalBalance = core == null || core[2] == null ? 0.0 : ((Number) core[2]).doubleValue();

        String topBranch = customerRepository.findTopBranchByTotalBalance(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(r -> (String) r[0])
                .orElse("N/A");

        String highestBalanceCustomer = customerRepository.findHighestBalanceCustomer(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(r -> (String) r[0])
                .orElse("N/A");

        return new DashboardResponse(totalCustomers, totalAccounts, totalBalance, topBranch, highestBalanceCustomer);
    }


}
