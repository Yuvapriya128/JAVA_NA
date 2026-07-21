package org.northernarc.loanemi.model;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.northernarc.loanemi.enums.ApplicationEventType;

import java.time.LocalDateTime;

/**
 * Entity to track status history/timeline for loan applications.
 */
@Entity
@Table(name = "loan_application_status_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private LoanApplication application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationEventType eventType;

    @Column(nullable = false)
    private LocalDateTime eventTimestamp = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer actor;

    @Column
    private String actorRole;

    @Column
    private String actorName;

    @Column(length = 1000)
    private String message;

    @Column
    private String previousStatus;

    @Column
    private String newStatus;

    public ApplicationStatusHistory(LoanApplication application, ApplicationEventType eventType, 
                                    Customer actor, String message) {
        this.application = application;
        this.eventType = eventType;
        this.actor = actor;
        this.actorName = actor != null ? actor.getCustomerName() : "System";
        this.actorRole = actor != null && actor.getRole() != null ? actor.getRole().name() : null;
        this.message = message;
        this.eventTimestamp = LocalDateTime.now();
    }
}
