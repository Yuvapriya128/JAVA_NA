package org.northernarc.loanemi.repository;

import jakarta.persistence.LockModeType;
import org.northernarc.loanemi.enums.LoanApplicationStatus;
import org.northernarc.loanemi.enums.LoanType;
import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.model.LoanApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    Page<LoanApplication> findByCustomer(Customer customer, Pageable pageable);
    
    Page<LoanApplication> findByApplicationStatus(LoanApplicationStatus status, Pageable pageable);
    
    Optional<LoanApplication> findByApplicationIdAndCustomer(Long applicationId, Customer customer);
    
    @Query("SELECT la FROM LoanApplication la WHERE la.applicationStatus IN :statuses")
    Page<LoanApplication> findByApplicationStatusIn(@Param("statuses") List<LoanApplicationStatus> statuses, Pageable pageable);
    
    @Query("SELECT COUNT(la) > 0 FROM LoanApplication la " +
           "WHERE la.customer = :customer AND la.loanType = :loanType " +
           "AND la.applicationStatus IN ('PENDING', 'UNDER_REVIEW')")
    boolean existsActiveApplicationByCustomerAndLoanType(
            @Param("customer") Customer customer, 
            @Param("loanType") LoanType loanType);
    
    /**
     * Find application by ID with pessimistic write lock (SELECT ... FOR UPDATE).
     * Used to prevent concurrent loan creation for the same application.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT la FROM LoanApplication la WHERE la.applicationId = :applicationId")
    Optional<LoanApplication> findByIdWithLock(@Param("applicationId") Long applicationId);
    
    /**
     * Check if a loan is already linked to an application.
     */
    @Query("SELECT COUNT(la) > 0 FROM LoanApplication la WHERE la.createdLoan.loanId = :loanId")
    boolean existsByCreatedLoanId(@Param("loanId") Long loanId);
}
