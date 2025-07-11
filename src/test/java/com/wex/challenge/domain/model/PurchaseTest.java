package com.wex.challenge.domain.model;

import com.wex.challenge.domain.exception.InvalidPurchaseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class PurchaseTest {

    @Test
    void newPurchase_shouldCreatePurchaseWithGeneratedId() {
        Purchase purchase = Purchase.newPurchase("Test Item", LocalDate.now(), new BigDecimal("100.00"));
        assertNotNull(purchase.getId());
        assertNotNull(purchase.getId().getValue());
        assertEquals("Test Item", purchase.getDescription().value());
        assertEquals(LocalDate.now(), purchase.getTransactionDate().value());
        assertEquals(new BigDecimal("100.00"), purchase.getPurchaseAmountUsd().value());
    }

    @Test
    void from_shouldCreatePurchaseWithGivenId() {
        String id = "some-predefined-id";
        Purchase purchase = Purchase.from(id, "Existing Item", LocalDate.of(2023, 1, 1), new BigDecimal("50.00"));
        assertEquals(id, purchase.getId().getValue());
        assertEquals("Existing Item", purchase.getDescription().value());
    }

    @Test
    void newPurchase_shouldThrowExceptionForInvalidDescription() {
        assertThrows(InvalidPurchaseException.class, () -> Purchase.newPurchase(null, LocalDate.now(), new BigDecimal("10.00")));
        assertThrows(InvalidPurchaseException.class, () -> Purchase.newPurchase("a".repeat(51), LocalDate.now(), new BigDecimal("10.00")));
    }

    @Test
    void newPurchase_shouldThrowExceptionForInvalidTransactionDate() {
        assertThrows(InvalidPurchaseException.class, () -> Purchase.newPurchase("Desc", null, new BigDecimal("10.00")));
    }

    @Test
    void newPurchase_shouldThrowExceptionForInvalidPurchaseAmount() {
        assertThrows(InvalidPurchaseException.class, () -> Purchase.newPurchase("Desc", LocalDate.now(), BigDecimal.ZERO));
    }

    @Test
    void convertTo_shouldConvertAmountCorrectly() {
        Purchase purchase = Purchase.newPurchase("Test Item", LocalDate.now(), new BigDecimal("100.00"));
        BigDecimal exchangeRate = new BigDecimal("1.35"); // 1 USD = 1.35 CAD
        CurrencyAmount converted = purchase.convertTo(exchangeRate, Currency.getInstance("CAD"));

        assertEquals(new BigDecimal("135.00"), converted.value());
        assertEquals(Currency.getInstance("CAD"), converted.currency());
    }

    @Test
    void convertTo_shouldHandleRoundingCorrectly() {
        Purchase purchase = Purchase.newPurchase("Test Item", LocalDate.now(), new BigDecimal("100.12"));
        BigDecimal exchangeRate = new BigDecimal("1.333"); // 1 USD = 1.333 CAD
        CurrencyAmount converted = purchase.convertTo(exchangeRate, Currency.getInstance("CAD"));

        // 100.12 * 1.333 = 133.45996 -> rounds to 133.46
        assertEquals(new BigDecimal("133.46"), converted.value());
    }

    @Test
    void convertTo_shouldThrowExceptionForInvalidExchangeRate() {
        Purchase purchase = Purchase.newPurchase("Test Item", LocalDate.now(), new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> purchase.convertTo(BigDecimal.ZERO, Currency.getInstance("EUR")));
        assertThrows(IllegalArgumentException.class, () -> purchase.convertTo(new BigDecimal("-0.5"), Currency.getInstance("EUR")));
        assertThrows(IllegalArgumentException.class, () -> purchase.convertTo(null, Currency.getInstance("EUR")));
    }
}
