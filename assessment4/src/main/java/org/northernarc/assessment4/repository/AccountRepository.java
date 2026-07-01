package org.northernarc.assessment4.repository;

import org.northernarc.assessment4.model.Account;
import org.northernarc.assessment4.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    // Task 3: Derived Query Methods
    List<Account> findByAccountType(AccountType accountType);

    default List<Account> findByAccountType(String accountType) {
        return findByAccountType(AccountType.valueOf(accountType.toUpperCase()));
    }

    List<Account> findByBalanceGreaterThan(double amount);

    // Task 4: JPQL Custom Query - Find accounts with no transactions
    @Query("""
        select a from Account a
        where a.transactions is empty
    """)
    List<Account> findAccountsWithNoTransactions();


    // Task 5: JPQL Update Query - Increase account balance
    @Modifying
    @Transactional
    @Query("""
        update Account a set a.balance = a.balance + :amount
        where a.accountNumber = :accountNumber
    """)
    int increaseBalance(@Param("accountNumber") String accountNumber, @Param("amount") double amount);
}
