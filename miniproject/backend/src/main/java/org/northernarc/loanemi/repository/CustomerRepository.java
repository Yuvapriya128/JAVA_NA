package org.northernarc.loanemi.repository;

import java.util.List;
import java.util.Optional;

import org.northernarc.loanemi.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    Customer findByPhoneNumber(String phoneNumber);

    List<Customer> findByCity(String city);

    List<Customer> findByCreditScoreGreaterThan(Integer creditScore);

    @Query("""
            select distinct c
            from Customer c
            join c.loans l
            join l.emiSchedules e
            where e.status = 'OVERDUE'
            """)
    List<Customer> findCustomersWithOverdueEmis();

    @Query("""
            select c
            from Customer c
            join c.loans l
            join l.emiSchedules e
            where e.status = 'OVERDUE'
            group by c
            order by sum(e.penaltyAmount + e.daysPastDue) desc
            """)
    List<Customer> findTopDefaulters();

    @Query("""
            select c.customerId, c.customerName, coalesce(sum(e.amountPaid), 0)
            from Customer c
            join c.loans l
            join l.emiSchedules e
            group by c.customerId, c.customerName
            order by coalesce(sum(e.amountPaid), 0) desc
            """)
    List<Object[]> findHighestPayingCustomers();
}
