package org.northernarc.loanemi.enums;

public enum Role {
    ADMIN,
    MANAGER,
    USER;

    public static Role from(String value) {
        if (value == null || value.isBlank()) {
            return USER;
        }
        return Role.valueOf(value.trim().toUpperCase());
    }
}
