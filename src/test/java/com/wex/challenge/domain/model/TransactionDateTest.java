package com.wex.challenge.domain.model;

import com.wex.challenge.domain.exception.InvalidPurchaseException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionDateTest {

    @Test
    void transactionDate_shouldBeCreatedWithValidValue() {
        LocalDate date = LocalDate.of(2023, 1, 15);
        TransactionDate txDate = new TransactionDate(date);
        assertEquals(date, txDate.value());
    }

    @Test
    void transactionDate_shouldThrowExceptionForNullValue() {
        assertThrows(InvalidPurchaseException.class, () -> new TransactionDate(null));
    }

    @Test
    void transactionDate_shouldBeEqualForSameValue() {
        LocalDate date = LocalDate.of(2023, 1, 15);
        TransactionDate txDate1 = new TransactionDate(date);
        TransactionDate txDate2 = new TransactionDate(date);
        assertEquals(txDate1, txDate2);
        assertEquals(txDate1.hashCode(), txDate2.hashCode());
    }

    @Test
    void transactionDate_shouldThrowExceptionForFutureDate(){
        LocalDate date = LocalDate.of(2026, 4, 23);
        assertThrows(InvalidPurchaseException.class, ()-> new TransactionDate(date));

    }
}
