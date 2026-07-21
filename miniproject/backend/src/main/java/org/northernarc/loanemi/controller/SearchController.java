package org.northernarc.loanemi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.northernarc.loanemi.dto.SearchResultDTO;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.model.Loan;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.northernarc.loanemi.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "Global search APIs")
public class SearchController {
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;

    public SearchController(CustomerRepository customerRepository, LoanRepository loanRepository) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    @Operation(summary = "Global search across customers, loans, payments, and EMIs")
    public SearchResultDTO search(
            @RequestParam String query,
            @RequestParam(defaultValue = "ALL") String type) {
        log.info("Global search requested query={} type={}", query, type);
        
        List<Customer> customers = searchCustomers(query);
        List<Loan> loans = searchLoans(query);
        
        return new SearchResultDTO(
                query,
                type,
                customers.stream().map(Customer::getCustomerName).toList(),
                loans.stream().map(l -> l.getLoanId() + " - " + l.getLoanType()).toList(),
                List.of(),
                List.of()
        );
    }

    private List<Customer> searchCustomers(String query) {
        List<Customer> allCustomers = customerRepository.findAll();
        return allCustomers.stream()
                .filter(c -> c.getCustomerName().toLowerCase().contains(query.toLowerCase())
                        || c.getEmail().toLowerCase().contains(query.toLowerCase())
                        || c.getPhoneNumber().contains(query)
                        || c.getCity().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    private List<Loan> searchLoans(String query) {
        List<Loan> allLoans = loanRepository.findAll();
        return allLoans.stream()
                .filter(l -> l.getLoanId().toString().contains(query)
                        || l.getCustomer().getCustomerName().toLowerCase().contains(query.toLowerCase())
                        || l.getLoanType().toString().contains(query.toUpperCase())
                        || l.getCustomer().getCity().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
}
