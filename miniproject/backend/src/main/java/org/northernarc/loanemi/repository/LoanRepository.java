package org.northernarc.loanemi.repository;

import java.util.List;

import org.northernarc.loanemi.enums.LoanStatus;
import org.northernarc.loanemi.enums.LoanType;
import org.northernarc.loanemi.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByLoanType(LoanType loanType);

    default List<Loan> findByLoanType(String loanType) {
        return findByLoanType(LoanType.valueOf(loanType.trim().toUpperCase()));
    }

    List<Loan> findByCustomerCity(String city);

    List<Loan> findByLoanStatus(LoanStatus loanStatus);

    List<Loan> findByPrincipalAmountGreaterThan(Double principalAmount);

    boolean existsByCustomerCustomerIdAndLoanStatus(Long customerId, LoanStatus loanStatus);

    @Query("""
            select c.city, coalesce(sum(e.amountPaid), 0.0)
            from Loan l
            join l.customer c
            join l.emiSchedules e
            group by c.city
            """)
    List<Object[]> findTotalEmiCollectionByCity();

    @Query("""
            select l
            from Loan l
            where not exists (
                select 1 from EmiSchedule e
                where e.loan = l and e.status = 'OVERDUE'
            )
            """)
    List<Loan> findLoansWithZeroOverdueEmis();

    @Query("select l from Loan l where l.loanStatus = 'ACTIVE'")
    List<Loan> findActiveLoans();

    @Query("""
            select c.city, count(l)
            from Loan l
            join l.customer c
            group by c.city
            """)
    List<Object[]> findLoanCountPerCity();

    @Query("select avg(l.annualInterestRate) from Loan l")
    Double findAverageInterestRate();

    @Query("""
            select l
            from Loan l
            where l.principalAmount = (
                select max(l2.principalAmount) from Loan l2
            )
            """)
    Loan findHighestOutstandingLoan();

    @Query("select coalesce(sum(e.penaltyAmount), 0.0) from EmiSchedule e")
    Double findTotalPenaltyCollected();

    @Query("select coalesce(sum(e.amountPaid), 0.0) from EmiSchedule e")
    Double findTotalEmiCollected();

    @Query("""
            select l.loanId, l.loanType, sum(case when e.status = 'PAID' then 0 else coalesce(e.principalComponent, 0) end)
            from Loan l
            join l.emiSchedules e
            group by l.loanId, l.loanType
            order by sum(case when e.status = 'PAID' then 0 else coalesce(e.principalComponent, 0) end) desc
            """)
    List<Object[]> findLoanOutstandingRanking();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Loan l
            set l.annualInterestRate = :newRate
            where l.loanType = :loanType
            """)
    int updateInterestRateByLoanType(@Param("loanType") LoanType loanType, @Param("newRate") Double newRate);

    default int updateInterestRateByLoanType(String loanType, Double newRate) {
        try {
            LoanType parsed = LoanType.valueOf(loanType.trim().toUpperCase());
            return updateInterestRateByLoanType(parsed, newRate);
        } catch (IllegalArgumentException ex) {
            return 0;
        }
    }
}
