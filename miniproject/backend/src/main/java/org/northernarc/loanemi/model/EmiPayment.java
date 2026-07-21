package org.northernarc.loanemi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.northernarc.loanemi.enums.PaymentMode;

import java.time.LocalDate;

@Entity
@Table(name = "loan_emi_payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmiPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Positive
    @Column(nullable = false)
    private Double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMode paymentMode;

    @NotNull
    @Column(nullable = false)
    private LocalDate paymentDate;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emi_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EmiSchedule emiSchedule;

    public void setEmiSchedule(EmiSchedule emiSchedule) {
        if (this.emiSchedule != null) {
            this.emiSchedule.getPayments().remove(this);
        }
        this.emiSchedule = emiSchedule;
        if (emiSchedule != null && emiSchedule.getPayments().stream().noneMatch(p -> p == this)) {
            emiSchedule.getPayments().add(this);
        }
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = PaymentMode.valueOf(paymentMode.trim().toUpperCase());
    }

}
