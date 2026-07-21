package org.northernarc.loanemi.repository;

import org.northernarc.loanemi.model.ApplicationStatusHistory;
import org.northernarc.loanemi.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Long> {
    
    List<ApplicationStatusHistory> findByApplicationOrderByEventTimestampAsc(LoanApplication application);
    
    @Query("SELECT h FROM ApplicationStatusHistory h WHERE h.application.applicationId = :applicationId ORDER BY h.eventTimestamp ASC")
    List<ApplicationStatusHistory> findByApplicationIdOrderByEventTimestampAsc(@Param("applicationId") Long applicationId);
}
