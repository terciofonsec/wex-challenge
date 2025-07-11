package com.wex.challenge.domain.model;

import com.wex.challenge.domain.exception.InvalidPurchaseException;


public record Description(String value) {
    private static final int MAX_LENGTH = 50;

    public Description {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidPurchaseException("Description cannot be null or empty.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new InvalidPurchaseException("Description must not exceed " + MAX_LENGTH + " characters.");
        }
    }
}
