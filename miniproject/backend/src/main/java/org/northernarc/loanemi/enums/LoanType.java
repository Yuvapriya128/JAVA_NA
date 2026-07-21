package org.northernarc.loanemi.enums;

public enum LoanType {
    PERSONAL("Personal Loan", 0.10),
    HOME("Home Loan", 0.065),
    VEHICLE("Vehicle Loan", 0.08),
    AUTO("Auto Loan", 0.08),
    EDUCATION("Education Loan", 0.075),
    BUSINESS("Business Loan", 0.12),
    GOLD("Gold Loan", 0.12),
    SECURED("Secured Loan", 0.09),
    UNSECURED("Unsecured Loan", 0.14);

    private final String displayName;
    private final double defaultRate;

    LoanType(String displayName, double defaultRate) {
        this.displayName = displayName;
        this.defaultRate = defaultRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultRate() {
        return defaultRate;
    }
}
