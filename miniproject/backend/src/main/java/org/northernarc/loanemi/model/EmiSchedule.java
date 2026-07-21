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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.northernarc.loanemi.enums.EmiStatus;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_emi_schedule")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmiSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emiId;

    @Positive
    @Column(nullable = false)
    private Integer installmentNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    @Positive
    @Column(nullable = false)
    private Double amountDue;

    @PositiveOrZero
    @Column(nullable = false)
    private Double principalComponent;

    @PositiveOrZero
    @Column(nullable = false)
    private Double interestComponent;

    @PositiveOrZero
    @Column(nullable = false)
    private Double amountPaid = 0.0;

    private LocalDate paymentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmiStatus status = EmiStatus.PENDING;

    @Min(0)
    @Column(nullable = false)
    private Integer daysPastDue = 0;

    @PositiveOrZero
    @Column(nullable = false)
    private Double penaltyAmount = 0.0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Loan loan;

    @OneToMany(mappedBy = "emiSchedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<EmiPayment> payments = new ArrayList<>();

    public void setLoan(Loan loan) {
        if (this.loan != null) {
            this.loan.getEmiSchedules().remove(this);
        }
        this.loan = loan;
        if (loan != null && loan.getEmiSchedules().stream().noneMatch(e -> e == this)) {
            loan.getEmiSchedules().add(this);
        }
    }

    public void setStatus(String status) {
        this.status = EmiStatus.valueOf(status.trim().toUpperCase());
    }

    public String getStatus() {
        return status == null ? null : status.name();
    }

    public EmiStatus getStatusEnum() {
        return status;
    }

}
