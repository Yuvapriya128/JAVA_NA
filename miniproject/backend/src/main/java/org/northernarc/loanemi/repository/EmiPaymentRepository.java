package org.northernarc.loanemi.repository;

import java.util.List;

import org.northernarc.loanemi.model.EmiPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmiPaymentRepository extends JpaRepository<EmiPayment, Long> {

    EmiPayment findByReferenceNumber(String referenceNumber);

    @Query("select p from EmiPayment p order by p.paymentDate desc, p.paymentId desc")
    List<EmiPayment> findLatestPayment(Pageable pageable);

    @Query("select p from EmiPayment p order by p.paymentDate desc, p.paymentId desc")
    Page<EmiPayment> findLatestPaymentPage(Pageable pageable);
    
    @Modifying
    @Query("DELETE FROM EmiPayment p WHERE p.emiSchedule.emiId IN " +
           "(SELECT e.emiId FROM EmiSchedule e WHERE e.loan.loanId = :loanId)")
    void deleteByLoanId(@Param("loanId") Long loanId);
}
