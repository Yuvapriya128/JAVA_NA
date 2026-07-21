package org.northernarc.loanemi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.northernarc.loanemi.enums.LoanStatus;
import org.northernarc.loanemi.enums.LoanType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.PrePersist;

@Entity
@Table(name = "loan_loan")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Positive
    @Column(nullable = false)
    private Double principalAmount;

    @Positive
    @Column(nullable = false)
    private Double annualInterestRate;

    @Positive
    @Column(nullable = false)
    private Integer tenureMonths;

    @Positive
    @Column(nullable = false)
    private Double emiAmount;

    @NotNull
    @Column(nullable = false)
    private LocalDate disbursementDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus loanStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<EmiSchedule> emiSchedules = new ArrayList<>();

    public void addEmiSchedule(EmiSchedule emiSchedule) {
        if (emiSchedules.stream().noneMatch(e -> e == emiSchedule)) {
            emiSchedules.add(emiSchedule);
        }
        if (emiSchedule.getLoan() != this) {
            emiSchedule.setLoan(this);
        }
    }

    public void setCustomer(Customer customer) {
        if (this.customer != null) {
            this.customer.getLoans().remove(this);
        }
        this.customer = customer;
        if (customer != null && customer.getLoans().stream().noneMatch(l -> l == this)) {
            customer.getLoans().add(this);
        }
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = LoanStatus.valueOf(loanStatus.trim().toUpperCase());
    }

    public String getLoanStatus() {
        return loanStatus == null ? null : loanStatus.name();
    }

    public LoanStatus getLoanStatusEnum() {
        return loanStatus;
    }

    public void setLoanType(String loanType) {
        if (loanType == null || loanType.trim().isEmpty()) {
            throw new IllegalArgumentException("Loan type cannot be null or empty");
        }
        this.loanType = LoanType.valueOf(loanType.trim().toUpperCase());
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public String getLoanType() {
        return loanType == null ? null : loanType.name();
    }

    public LoanType getLoanTypeEnum() {
        return loanType;
    }

    @PrePersist
    void beforePersist() {
        if (emiAmount == null && principalAmount != null && annualInterestRate != null && tenureMonths != null
                && tenureMonths > 0) {
            double monthlyRate = annualInterestRate / (12 * 100);
            if (monthlyRate == 0) {
                emiAmount = principalAmount / tenureMonths;
            } else {
                double factor = Math.pow(1 + monthlyRate, tenureMonths);
                emiAmount = (principalAmount * monthlyRate * factor) / (factor - 1);
            }
        }
    }

}
