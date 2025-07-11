package com.wex.challenge.domain.service;

import com.wex.challenge.application.dto.ConvertedPurchaseResponse;
import com.wex.challenge.application.port.in.RetrieveConvertedPurchaseQuery;
import com.wex.challenge.application.port.out.PurchaseRepository;
import com.wex.challenge.application.service.RetrieveConvertedPurchaseUseCase;
import com.wex.challenge.domain.exception.ExchangeRateNotFoundException;
import com.wex.challenge.domain.model.Purchase;
import com.wex.challenge.domain.model.PurchaseId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RetrieveConvertedPurchaseUseCaseTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private CurrencyConversionService currencyConversionService;

    private RetrieveConvertedPurchaseUseCase retrieveConvertedPurchaseUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        retrieveConvertedPurchaseUseCase = new RetrieveConvertedPurchaseUseCase(purchaseRepository, currencyConversionService);
    }

    @Test
    void execute_shouldRetrieveAndConvertPurchaseSuccessfully() {
        // Given
        String purchaseIdValue = "purchase-id-123";
        LocalDate purchaseDate = LocalDate.of(2023, 7, 10);
        BigDecimal usdAmount = new BigDecimal("100.00");
        Currency targetCurrency = Currency.getInstance("CAD");
        BigDecimal exchangeRate = new BigDecimal("1.35");

        Purchase existingPurchase = Purchase.from(purchaseIdValue, "Test Item", purchaseDate, usdAmount);

        RetrieveConvertedPurchaseQuery query = new RetrieveConvertedPurchaseQuery();
        query.setPurchaseId(purchaseIdValue);
        query.setTargetCurrencyCode(targetCurrency.getCurrencyCode());

        when(purchaseRepository.findById(PurchaseId.from(purchaseIdValue))).thenReturn(Optional.of(existingPurchase));
        when(currencyConversionService.getExchangeRate(targetCurrency, purchaseDate)).thenReturn(exchangeRate);

        // When
        ConvertedPurchaseResponse response = retrieveConvertedPurchaseUseCase.execute(query);

        // Then
        assertNotNull(response);
        assertEquals(purchaseIdValue, response.getId());
        assertEquals("Test Item", response.getDescription());
        assertEquals(purchaseDate, response.getTransactionDate());
        assertEquals(usdAmount, response.getOriginalPurchaseAmountUsd());
        assertEquals(exchangeRate, response.getExchangeRateUsed());
        assertEquals(new BigDecimal("135.00"), response.getConvertedAmount());
        assertEquals(targetCurrency.getCurrencyCode(), response.getTargetCurrencyCode());

        verify(purchaseRepository, times(1)).findById(PurchaseId.from(purchaseIdValue));
        verify(currencyConversionService, times(1)).getExchangeRate(targetCurrency, purchaseDate);
    }

    @Test
    void execute_shouldThrowExceptionWhenPurchaseNotFound() {
        // Given
        String nonExistentId = "non-existent-id";
        RetrieveConvertedPurchaseQuery query = new RetrieveConvertedPurchaseQuery();
        query.setPurchaseId(nonExistentId);
        query.setTargetCurrencyCode("EUR");

        when(purchaseRepository.findById(PurchaseId.from(nonExistentId))).thenReturn(Optional.empty());

        // When / Then
        assertThrows(NoSuchElementException.class, () -> retrieveConvertedPurchaseUseCase.execute(query));
        verify(purchaseRepository, times(1)).findById(PurchaseId.from(nonExistentId));
        verify(currencyConversionService, never()).getExchangeRate(any(Currency.class), any(LocalDate.class)); // Should not call conversion service
    }

    @Test
    void execute_shouldThrowExceptionWhenExchangeRateNotFound() {
        // Given
        String purchaseIdValue = "purchase-id-123";
        LocalDate purchaseDate = LocalDate.of(2023, 7, 10);
        BigDecimal usdAmount = new BigDecimal("100.00");
        Currency targetCurrency = Currency.getInstance("AUD");

        Purchase existingPurchase = Purchase.from(purchaseIdValue, "Test Item", purchaseDate, usdAmount);

        RetrieveConvertedPurchaseQuery query = new RetrieveConvertedPurchaseQuery();
        query.setPurchaseId(purchaseIdValue);
        query.setTargetCurrencyCode(targetCurrency.getCurrencyCode());

        when(purchaseRepository.findById(PurchaseId.from(purchaseIdValue))).thenReturn(Optional.of(existingPurchase));
        when(currencyConversionService.getExchangeRate(targetCurrency, purchaseDate))
                .thenThrow(new ExchangeRateNotFoundException("Rate not found for AUD"));

        // When / Then
        ExchangeRateNotFoundException thrown = assertThrows(ExchangeRateNotFoundException.class, () ->
                retrieveConvertedPurchaseUseCase.execute(query)
        );
        assertTrue(thrown.getMessage().contains("Rate not found for AUD"));
        verify(purchaseRepository, times(1)).findById(PurchaseId.from(purchaseIdValue));
        verify(currencyConversionService, times(1)).getExchangeRate(targetCurrency, purchaseDate);
    }

    @Test
    void execute_shouldThrowExceptionForInvalidCurrencyCode() {
        // Given
        RetrieveConvertedPurchaseQuery query = new RetrieveConvertedPurchaseQuery();
        query.setPurchaseId("any-id");
        query.setTargetCurrencyCode("XYZ"); // Invalid currency code

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> retrieveConvertedPurchaseUseCase.execute(query));
        verify(purchaseRepository, never()).findById(any(PurchaseId.class)); // Should not proceed
    }
}
