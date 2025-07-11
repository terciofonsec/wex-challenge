package com.wex.challenge.domain.model;

import com.wex.challenge.domain.exception.InvalidPurchaseException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record PurchaseAmount(BigDecimal value) {
    private static final int DECIMAL_PLACES = 2; // Cents

    public PurchaseAmount(BigDecimal value) {
        if (value == null) {
            throw new InvalidPurchaseException("Purchase amount cannot be null.");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPurchaseException("Purchase amount must be a positive value.");
        }
        this.value = value.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
}
