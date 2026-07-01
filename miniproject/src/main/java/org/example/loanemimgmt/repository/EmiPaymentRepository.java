package org.example.loanemimgmt.repository;

import org.example.loanemimgmt.model.EmiPayment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface EmiPaymentRepository extends JpaRepository<EmiPayment, Long> {

    @Query("""
            select c.city, sum(p.amount)
            from EmiPayment p
            join p.emiSchedule e
            join e.loan l
            join l.customer c
            group by c.city
            order by c.city asc
            """)
    List<Object[]> getTotalEmiCollectionByCityRows();

    @Query("select coalesce(sum(p.amount), 0) from EmiPayment p")
    BigDecimal getTotalEmiCollected();

    @Query("""
            select p
            from EmiPayment p
            order by p.paymentDate desc, p.paymentId desc
            """)
    List<EmiPayment> findLatestPayment(Pageable pageable);
}


