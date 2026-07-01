package org.example.loanemimgmt.repository;

import org.example.loanemimgmt.enums.EmiStatus;
import org.example.loanemimgmt.model.EmiSchedule;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {

    List<EmiSchedule> findByStatusAndDueDateBefore(EmiStatus status, LocalDate currentDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EmiSchedule e where e.emiId = :emiId")
    Optional<EmiSchedule> findByIdForUpdate(@Param("emiId") Long emiId);

    long countByLoanLoanIdAndStatusNot(Long loanId, EmiStatus status);

    long countByLoanLoanIdAndStatus(Long loanId, EmiStatus status);

    @Query("""
            select e
            from EmiSchedule e
            where e.status = org.example.loanemimgmt.enums.EmiStatus.OVERDUE
            order by ((e.amountDue - e.amountPaid) + e.penaltyAmount) desc
            """)
    List<EmiSchedule> findHighestOverdueAmount(Pageable pageable);

    long countByStatus(EmiStatus status);

    @Query("select coalesce(sum(e.penaltyAmount), 0) from EmiSchedule e")
    BigDecimal getTotalPenaltyCollected();

    @Query("""
            select sum(e.principalComponent)
            from EmiSchedule e
            where e.status <> org.example.loanemimgmt.enums.EmiStatus.PAID
            group by e.loan.loanId
            order by sum(e.principalComponent) desc
            """)
    List<BigDecimal> findHighestOutstandingLoan(Pageable pageable);

    @Query("""
            select count(distinct e.loan.loanId)
            from EmiSchedule e
            where e.status = org.example.loanemimgmt.enums.EmiStatus.OVERDUE
            """)
    long countNpaAccounts();
}

