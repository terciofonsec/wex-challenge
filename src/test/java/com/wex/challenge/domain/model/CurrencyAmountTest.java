package com.wex.challenge.domain.model;

import com.wex.challenge.domain.exception.InvalidPurchaseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyAmountTest {
    @Test
    void currencyAmount_shouldBeCreatedWithValidValueAndRounded() {
        CurrencyAmount amount = new CurrencyAmount(new BigDecimal("200.456"), Currency.getInstance("CAD"));
        assertEquals(new BigDecimal("200.46"), amount.value());
        assertEquals(Currency.getInstance("CAD"), amount.currency());
    }

    @Test
    void currencyAmount_shouldThrowExceptionForNullValue() {
        assertThrows(InvalidPurchaseException.class, () -> new CurrencyAmount(null, Currency.getInstance("EUR")));
    }

    @Test
    void currencyAmount_shouldThrowExceptionForNullCurrency() {
        assertThrows(InvalidPurchaseException.class, () -> new CurrencyAmount(new BigDecimal("10.00"), null));
    }

    @Test
    void currencyAmount_shouldBeEqualForSameValueAndCurrency() {
        CurrencyAmount amount1 = new CurrencyAmount(new BigDecimal("75.25"), Currency.getInstance("GBP"));
        CurrencyAmount amount2 = new CurrencyAmount(new BigDecimal("75.250"), Currency.getInstance("GBP"));
        assertEquals(amount1, amount2);
        assertEquals(amount1.hashCode(), amount2.hashCode());
    }
}
