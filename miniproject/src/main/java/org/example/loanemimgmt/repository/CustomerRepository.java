package org.example.loanemimgmt.repository;

import org.example.loanemimgmt.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<Customer> findByCityIgnoreCase(String city);

    @Query("""
            select distinct c
            from Customer c
            join c.loans l
            join l.emiSchedules e
            where e.status = org.example.loanemimgmt.enums.EmiStatus.OVERDUE
            """)
    List<Customer> findCustomersWithOverdueEmis();

    @Query("""
            select c.customerId, c.customerName, (sum(e.amountDue) - sum(e.amountPaid) + sum(e.penaltyAmount))
            from Customer c
            join c.loans l
            join l.emiSchedules e
            where e.status = org.example.loanemimgmt.enums.EmiStatus.OVERDUE
            group by c.customerId, c.customerName
            order by (sum(e.amountDue) - sum(e.amountPaid) + sum(e.penaltyAmount)) desc
            """)
    List<Object[]> findTopDefaulterRows(Pageable pageable);

    @Query("""
            select c.customerId, c.customerName, sum(p.amount)
            from EmiPayment p
            join p.emiSchedule e
            join e.loan l
            join l.customer c
            group by c.customerId, c.customerName
            order by sum(p.amount) desc
            """)
    List<Object[]> findHighestPayingCustomerRows(Pageable pageable);
}

