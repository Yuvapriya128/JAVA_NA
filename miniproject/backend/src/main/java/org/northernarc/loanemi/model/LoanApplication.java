package org.northernarc.loanemi.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.northernarc.loanemi.enums.LoanApplicationStatus;
import org.northernarc.loanemi.enums.LoanType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_application", indexes = {
    @Index(name = "idx_loan_app_created_loan", columnList = "created_loan_id", unique = true)
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Positive
    @Column(nullable = false)
    private Double principalAmount;

    @Positive
    @Column(nullable = false)
    private Integer tenureMonths;

    @Positive
    @Column(nullable = false)
    private Double annualInterestRate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanApplicationStatus applicationStatus = LoanApplicationStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime applicationDate = LocalDateTime.now();

    @Column
    private LocalDateTime approvalDate;

    @Column
    private LocalDateTime lastUpdatedAt = LocalDateTime.now();

    @Column
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer approvedBy;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "created_loan_id", unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Loan createdLoan;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ApplicationStatusHistory> statusHistory = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
