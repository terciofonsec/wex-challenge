package com.wex.challenge.domain.service;

import com.wex.challenge.application.port.out.ExchangeRateProvider;
import com.wex.challenge.domain.exception.ExchangeRateNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;

public class CurrencyConversionService {

    private final ExchangeRateProvider exchangeRateProvider;
    private static final long MAX_DAYS_FOR_EXCHANGE_RATE = 180;

    public CurrencyConversionService(ExchangeRateProvider exchangeRateProvider) {
        this.exchangeRateProvider = exchangeRateProvider;
    }

    public BigDecimal getExchangeRate(Currency targetCurrency, LocalDate purchaseDate) {
        LocalDate sixMonthsAgo = purchaseDate.minusDays(MAX_DAYS_FOR_EXCHANGE_RATE);

        Optional<BigDecimal> latestRateInRange = exchangeRateProvider.getLatestExchangeRateInRange(
                targetCurrency.getCurrencyCode(),
                sixMonthsAgo,
                purchaseDate
        );

        if (latestRateInRange.isPresent()) {
            return latestRateInRange.get();
        }

        throw new ExchangeRateNotFoundException(
                "No currency conversion rate is available for " + targetCurrency.getCurrencyCode() +
                        " in " + MAX_DAYS_FOR_EXCHANGE_RATE + " days equals or before to " + purchaseDate
        );
    }
}
