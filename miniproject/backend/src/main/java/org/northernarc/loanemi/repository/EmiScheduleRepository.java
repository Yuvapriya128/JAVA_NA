package org.northernarc.loanemi.repository;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.LockModeType;
import org.northernarc.loanemi.enums.EmiStatus;
import org.northernarc.loanemi.model.EmiSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {

    List<EmiSchedule> findByStatus(EmiStatus status);

    List<EmiSchedule> findByDueDateBefore(LocalDate dueDate);

    List<EmiSchedule> findByLoanLoanId(Long loanId);
    
    @Modifying
    @Query("DELETE FROM EmiSchedule e WHERE e.loan.loanId = :loanId")
    void deleteByLoanLoanId(@Param("loanId") Long loanId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EmiSchedule e where e.emiId = :emiId")
    EmiSchedule findByIdForUpdate(@Param("emiId") Long emiId);

    @Query("""
            select e
            from EmiSchedule e
            where e.status = 'OVERDUE'
              and (e.amountDue + e.penaltyAmount - e.amountPaid) = (
                  select max(e2.amountDue + e2.penaltyAmount - e2.amountPaid)
                  from EmiSchedule e2
                  where e2.status = 'OVERDUE'
              )
            """)
    EmiSchedule findHighestOverdueEmi();

    long countByStatus(EmiStatus status);

    @Query("select coalesce(sum(e.amountDue + e.penaltyAmount - e.amountPaid), 0.0) from EmiSchedule e where e.status = 'OVERDUE'")
    Double findTotalOverdueAmount();

    @Query("select coalesce(sum(case when e.status = 'PAID' then 0 else e.principalComponent end), 0.0) from EmiSchedule e")
    Double findOutstandingPrincipal();

    @Query("""
            select e
            from EmiSchedule e
            where e.status <> 'PAID' and e.loan.loanStatus <> 'CLOSED' and e.dueDate < :currentDate
            """)
    List<EmiSchedule> findUnpaidPastDueSchedules(@Param("currentDate") LocalDate currentDate);

    @Query("select count(e) from EmiSchedule e where e.status = 'OVERDUE'")
    Long countOverdueEmis();

    @Query("select count(distinct e.loan.customer.customerId) from EmiSchedule e where e.status = 'OVERDUE' and e.daysPastDue >= 90")
    Long countNpaAccounts();
}
