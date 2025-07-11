package com.wex.challenge.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;


public class Purchase {
    private final PurchaseId id;
    private Description description;
    private TransactionDate transactionDate;
    private PurchaseAmount purchaseAmountUsd;

    private Purchase(PurchaseId id, Description description, TransactionDate transactionDate, PurchaseAmount purchaseAmountUsd) {
        this.id = Objects.requireNonNull(id, "PurchaseId cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.transactionDate = Objects.requireNonNull(transactionDate, "TransactionDate cannot be null");
        this.purchaseAmountUsd = Objects.requireNonNull(purchaseAmountUsd, "PurchaseAmountUsd cannot be null");
    }

    public PurchaseId getId() {
        return id;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public TransactionDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(TransactionDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public PurchaseAmount getPurchaseAmountUsd() {
        return purchaseAmountUsd;
    }

    public void setPurchaseAmountUsd(PurchaseAmount purchaseAmountUsd) {
        this.purchaseAmountUsd = purchaseAmountUsd;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;
        return Objects.equals(id, purchase.id) && Objects.equals(description, purchase.description) && Objects.equals(transactionDate, purchase.transactionDate) && Objects.equals(purchaseAmountUsd, purchase.purchaseAmountUsd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, transactionDate, purchaseAmountUsd);
    }

    // Factory method for creating a new Purchase (assigns a new ID)
    public static Purchase newPurchase(String description, LocalDate transactionDate, BigDecimal purchaseAmountUsd) {
        return new Purchase(
                PurchaseId.generate(),
                new Description(description),
                new TransactionDate(transactionDate),
                new PurchaseAmount(purchaseAmountUsd)
        );
    }

    public static Purchase from(String id, String description, LocalDate transactionDate, BigDecimal purchaseAmountUsd) {
        return new Purchase(
                PurchaseId.from(id),
                new Description(description),
                new TransactionDate(transactionDate),
                new PurchaseAmount(purchaseAmountUsd)
        );
    }


    public CurrencyAmount convertTo(BigDecimal exchangeRate, Currency targetCurrency) {
        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be a positive value.");
        }
        BigDecimal convertedValue = this.purchaseAmountUsd.value().multiply(exchangeRate);
        return new CurrencyAmount(convertedValue, targetCurrency);
    }
}
