package org.northernarc.loanemi.enums;

/**
 * Types of user notifications.
 */
public enum NotificationType {
    LOAN_APPLICATION("Loan Application"),
    APPLICATION_APPROVED("Application Approved"),
    APPLICATION_REJECTED("Application Rejected"),
    APPLICATION_UNDER_REVIEW("Application Under Review"),
    LOAN_CREATED("Loan Created"),
    EMI_REMINDER("EMI Reminder"),
    EMI_OVERDUE("EMI Overdue"),
    PAYMENT_RECEIVED("Payment Received"),
    GENERAL("General");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
