package com.wex.challenge.domain.model;

import com.wex.challenge.domain.exception.InvalidPurchaseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PurchaseAmountTest {
    @Test
    void purchaseAmount_shouldBeCreatedWithValidValueAndRounded() {
        PurchaseAmount amount = new PurchaseAmount(new BigDecimal("100.123"));
        assertEquals(new BigDecimal("100.12"), amount.value());

        PurchaseAmount amount2 = new PurchaseAmount(new BigDecimal("100.125"));
        assertEquals(new BigDecimal("100.13"), amount2.value()); // HALF_UP rounding

        PurchaseAmount amount3 = new PurchaseAmount(new BigDecimal("100.126"));
        assertEquals(new BigDecimal("100.13"), amount3.value());
    }

    @Test
    void purchaseAmount_shouldThrowExceptionForNullValue() {
        assertThrows(InvalidPurchaseException.class, () -> new PurchaseAmount(null));
    }

    @Test
    void purchaseAmount_shouldThrowExceptionForZeroOrNegativeValue() {
        assertThrows(InvalidPurchaseException.class, () -> new PurchaseAmount(BigDecimal.ZERO));
        assertThrows(InvalidPurchaseException.class, () -> new PurchaseAmount(new BigDecimal("-10.00")));
    }

    @Test
    void purchaseAmount_shouldBeEqualForSameValue() {
        PurchaseAmount amount1 = new PurchaseAmount(new BigDecimal("50.50"));
        PurchaseAmount amount2 = new PurchaseAmount(new BigDecimal("50.500")); // Same value, different scale
        assertEquals(amount1, amount2);
        assertEquals(amount1.hashCode(), amount2.hashCode());
    }
}
