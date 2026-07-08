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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.loanemimgmt.enums.LoanType;
import org.example.loanemimgmt.enums.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualInterestRate;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer tenureMonths;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal emiAmount = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private LocalDate disbursementDate = LocalDate.now();

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus loanStatus = LoanStatus.ON_PROGRESS;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Builder.Default
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EmiSchedule> emiSchedules = new ArrayList<>();

    /**
     * Custom setter for customer that synchronizes the bidirectional relationship.
     * This ensures both sides of the relationship are properly maintained and enables
     * safe removal of loans without constraint violations.
     *
     * @param newCustomer the new customer to associate with this loan, or null to remove association
     */
    public void setCustomer(Customer newCustomer) {
        // If the new customer is the same as the current customer, do nothing
        if (this.customer == newCustomer) {
            return;
        }

        // Remove this loan from the old customer's loans list (if there was an old customer)
        if (this.customer != null) {
            this.customer.getLoans().remove(this);
        }

        // Set the new customer
        this.customer = newCustomer;

        // Add this loan to the new customer's loans list (if new customer is not null)
        if (newCustomer != null && !newCustomer.getLoans().contains(this)) {
            newCustomer.getLoans().add(this);
        }
    }

    public void addEmiSchedule(EmiSchedule emiSchedule) {
        if (emiSchedule == null || emiSchedules.contains(emiSchedule)) {
            return;
        }
        emiSchedules.add(emiSchedule);
        emiSchedule.setLoan(this);
    }

    public void removeEmiSchedule(EmiSchedule emiSchedule) {
        if (emiSchedule == null) {
            return;
        }
        if (emiSchedules.remove(emiSchedule)) {
            emiSchedule.setLoan(null);
        }
    }
}
