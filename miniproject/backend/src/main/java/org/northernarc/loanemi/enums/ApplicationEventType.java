package org.northernarc.loanemi.enums;

/**
 * Event types for loan application timeline.
 */
public enum ApplicationEventType {
    SUBMITTED("Application Submitted"),
    UPDATED("Application Updated"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Application Approved"),
    REJECTED("Application Rejected"),
    LOAN_CREATED("Loan Created"),
    WITHDRAWN("Application Withdrawn"),
    REAPPLIED("Re-application Submitted");

    private final String displayName;

    ApplicationEventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
