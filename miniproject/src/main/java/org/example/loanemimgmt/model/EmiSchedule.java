package org.example.loanemimgmt.model;

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
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.loanemimgmt.enums.EmiStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emi_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emiId;

    @Version
    private Long version;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer installmentNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountDue;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalComponent;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal interestComponent;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    private LocalDate paymentDate;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmiStatus status = EmiStatus.PENDING;

    @NotNull
    @Min(0)
    @Builder.Default
    @Column(nullable = false)
    private Integer daysPastDue = 0;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Builder.Default
    @OneToMany(mappedBy = "emiSchedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EmiPayment> emiPayments = new ArrayList<>();

    public void setLoan(Loan loan) {
        if (this.loan == loan) {
            return;
        }
        Loan previousLoan = this.loan;
        this.loan = loan;

        if (previousLoan != null) {
            previousLoan.getEmiSchedules().remove(this);
        }
        if (loan != null && !loan.getEmiSchedules().contains(this)) {
            loan.getEmiSchedules().add(this);
        }
    }


    public void addEmiPayment(EmiPayment emiPayment) {
        if (emiPayment == null || emiPayments.contains(emiPayment)) {
            return;
        }
        emiPayments.add(emiPayment);
        emiPayment.setEmiSchedule(this);
    }

    public void removeEmiPayment(EmiPayment emiPayment) {
        if (emiPayment == null) {
            return;
        }
        if (emiPayments.remove(emiPayment)) {
            emiPayment.setEmiSchedule(null);
        }
    }
}
