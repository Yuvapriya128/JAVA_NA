package org.example.springdatajpademo.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    CASH_ON_DELIVERY,
    CARD,
    UPI;

    /**
     * Custom deserialization to handle payment method aliases.
     * Accepts: CASH, COD, CASH_ON_DELIVERY -> all map to CASH_ON_DELIVERY
     * Accepts: CARD, UPI (direct mapping)
     *
     * @param value the payment method string from frontend
     * @return the corresponding PaymentMethod enum value
     * @throws IllegalArgumentException if the value is not recognized
     */
    @JsonCreator
    public static PaymentMethod fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        String normalized = value.toUpperCase().trim();

        // Handle aliases for CASH_ON_DELIVERY
        switch (normalized) {
            case "CASH":
            case "COD":
            case "CASH_ON_DELIVERY":
                return CASH_ON_DELIVERY;
            case "CARD":
                return CARD;
            case "UPI":
                return UPI;
            default:
                throw new IllegalArgumentException(
                        "Invalid payment method: " + value +
                        ". Accepted values: CASH, COD, CASH_ON_DELIVERY, CARD, UPI"
                );
        }
    }
}

