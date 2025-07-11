package com.wex.challenge.domain.model;

import com.wex.challenge.domain.exception.InvalidPurchaseException;

import java.time.LocalDate;

public record TransactionDate(LocalDate value) {

    public TransactionDate {

        if (value == null) {
            throw new InvalidPurchaseException("Transaction date cannot be null.");
        }

        if (value.isAfter(LocalDate.now())) {
            throw new InvalidPurchaseException("Transaction date cannot be in the future");
        }
    }
}
