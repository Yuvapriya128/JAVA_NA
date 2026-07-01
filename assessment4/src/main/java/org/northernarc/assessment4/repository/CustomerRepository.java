package org.northernarc.assessment4.repository;

import org.northernarc.assessment4.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Task 3: Derived Query Method
    List<Customer> findByBranch(String branch);

    // Security Helper
    java.util.Optional<Customer> findByEmail(String email);

    @Query("""
        select c from Customer c
        join c.accounts a
        group by c
        having count(a) > 1
    """)
    List<Customer> findCustomersWithMultipleAccounts();


    @Query("""
        select c.branch, sum(a.balance) from Customer c
        join c.accounts a
        group by c.branch
    """)
    List<Object[]> findTotalBalancePerBranch();

    @Query("""
        select c from Customer c
        where (select coalesce(sum(a.balance), 0.0) from c.accounts a) > :threshold
    """)
    List<Customer> findRichCustomers(@Param("threshold") double threshold);

    @Query("""
        select count(c),
               (select count(a) from Account a),
               (select coalesce(sum(a.balance), 0.0) from Account a)
        from Customer c
    """)
    List<Object[]> fetchDashboardCoreMetrics();

    @Query("""
        select c.branch, coalesce(sum(a.balance), 0.0) as total
        from Customer c
        join fetch c.accounts a
        group by c.branch
        order by total desc
    """)
    List<Object[]> findTopBranchByTotalBalance(Pageable pageable);

    @Query("""
        select c.customerName, coalesce(sum(a.balance), 0.0) as total
        from Customer c
        left join c.accounts a
        group by c.customerId, c.customerName
        order by total desc
    """)
    List<Object[]> findHighestBalanceCustomer(Pageable pageable);
}
