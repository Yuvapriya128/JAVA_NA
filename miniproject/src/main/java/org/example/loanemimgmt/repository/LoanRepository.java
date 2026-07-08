package org.example.loanemimgmt.repository;

import org.example.loanemimgmt.enums.EmiStatus;
import org.example.loanemimgmt.enums.LoanStatus;
import org.example.loanemimgmt.enums.LoanType;
import org.example.loanemimgmt.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    Page<Loan> findByLoanType(LoanType loanType, Pageable pageable);

    Page<Loan> findByCustomerCityIgnoreCase(String city, Pageable pageable);

    Page<Loan> findByLoanStatus(LoanStatus status, Pageable pageable);

    Page<Loan> findByPrincipalAmountGreaterThanEqual(BigDecimal principalAmount, Pageable pageable);

    Page<Loan> findDistinctByEmiSchedulesStatus(EmiStatus status, Pageable pageable);

    long countByLoanStatus(LoanStatus status);

    @Query("select coalesce(avg(l.annualInterestRate), 0) from Loan l")
    BigDecimal getAverageInterestRate();

    @Query("""
            select l
            from Loan l
            where not exists (
                select e
                from EmiSchedule e
                where e.loan = l and e.status = org.example.loanemimgmt.enums.EmiStatus.OVERDUE
            )
            """)
    List<Loan> findLoansWithZeroOverdueEmis();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Loan l
            set l.annualInterestRate = :newAnnualRate
            where l.loanType in :loanTypes
            """)
    int reviseAnnualInterestRateByLoanTypes(@Param("loanTypes") Collection<LoanType> loanTypes,
                                            @Param("newAnnualRate") BigDecimal newAnnualRate);
}

