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
import org.northernarc.loanemi.enums.NotificationType;

import java.time.LocalDateTime;

/**
 * Entity for user notifications.
 */
@Entity
@Table(name = "loan_notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false)
    private boolean isRead = false;

    @Column
    private Long relatedEntityId;

    @Column
    private String relatedEntityType;

    public Notification(Customer customer, NotificationType type, String title, String message) {
        this.customer = customer;
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(Customer customer, NotificationType type, String title, String message, 
                       Long relatedEntityId, String relatedEntityType) {
        this(customer, type, title, message);
        this.relatedEntityId = relatedEntityId;
        this.relatedEntityType = relatedEntityType;
    }
}
