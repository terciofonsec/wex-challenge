package com.wex.challenge.domain.model;

import com.wex.challenge.domain.exception.InvalidPurchaseException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;


public record CurrencyAmount(BigDecimal value, Currency currency) {
    private static final int DECIMAL_PLACES = 2;

    public CurrencyAmount(BigDecimal value, Currency currency) {
        if (value == null) {
            throw new InvalidPurchaseException("Currency amount value cannot be null.");
        }
        if (currency == null) {
            throw new InvalidPurchaseException("Currency cannot be null.");
        }
        this.value = value.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
        this.currency = currency;
    }
}
