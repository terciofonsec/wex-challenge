package com.wex.challenge.domain.service;

import com.wex.challenge.application.port.out.ExchangeRateProvider;
import com.wex.challenge.domain.exception.ExchangeRateNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CurrencyConversionServiceTest {

    private static final long MAX_DAYS_FOR_EXCHANGE_RATE = 180;
    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    private CurrencyConversionService currencyConversionService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyConversionService = new CurrencyConversionService(exchangeRateProvider);
    }

    @Test
    void getExchangeRate_shouldReturnRateIfFoundOnPurchaseDate() {
        LocalDate purchaseDate = LocalDate.of(2023, 7, 10);
        Currency targetCurrency = Currency.getInstance("EUR");
        BigDecimal expectedRate = new BigDecimal("0.92");
        LocalDate sixMonthsAgo = purchaseDate.minusDays(MAX_DAYS_FOR_EXCHANGE_RATE);

        when(exchangeRateProvider.getLatestExchangeRateInRange(targetCurrency.getCurrencyCode(), sixMonthsAgo, purchaseDate))
                .thenReturn(Optional.of(expectedRate));

        BigDecimal actualRate = currencyConversionService.getExchangeRate(targetCurrency, purchaseDate);
        assertEquals(expectedRate, actualRate);
        verify(exchangeRateProvider, times(1)).getLatestExchangeRateInRange(targetCurrency.getCurrencyCode(), sixMonthsAgo, purchaseDate);
    }

    @Test
    void getExchangeRate_shouldReturnRateIfFoundWithin6MonthsBeforePurchaseDate() {
        LocalDate purchaseDate = LocalDate.of(2023, 7, 10);
        Currency targetCurrency = Currency.getInstance("CAD");
        BigDecimal expectedRate = new BigDecimal("1.35");
        LocalDate sixMonthsAgo = purchaseDate.minusDays(MAX_DAYS_FOR_EXCHANGE_RATE);

        when(exchangeRateProvider.getLatestExchangeRateInRange(targetCurrency.getCurrencyCode(), sixMonthsAgo, purchaseDate))
                .thenReturn(Optional.of(expectedRate));

        BigDecimal actualRate = currencyConversionService.getExchangeRate(targetCurrency, purchaseDate);
        assertEquals(expectedRate, actualRate);
        verify(exchangeRateProvider, times(1)).getLatestExchangeRateInRange(targetCurrency.getCurrencyCode(), sixMonthsAgo, purchaseDate);

    }

    @Test
    void getExchangeRate_shouldThrowExceptionIfNoRateFoundWithin6Months() {
        LocalDate purchaseDate = LocalDate.of(2023, 1, 10); // Start date
        Currency targetCurrency = Currency.getInstance("JPY");
        LocalDate sixMonthsAgo = purchaseDate.minusDays(MAX_DAYS_FOR_EXCHANGE_RATE);


        when(exchangeRateProvider.getLatestExchangeRateInRange(targetCurrency.getCurrencyCode(), sixMonthsAgo,  purchaseDate))
                    .thenReturn(Optional.empty());


        ExchangeRateNotFoundException thrown = assertThrows(ExchangeRateNotFoundException.class, () ->
                currencyConversionService.getExchangeRate(targetCurrency, purchaseDate)
        );

        assertTrue(thrown.getMessage().contains("No currency conversion rate is available"));
        verify(exchangeRateProvider, times(1)).getLatestExchangeRateInRange(anyString(),any(LocalDate.class), any(LocalDate.class));
    }
}
