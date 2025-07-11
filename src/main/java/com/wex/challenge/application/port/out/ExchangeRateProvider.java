package com.wex.challenge.application.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface ExchangeRateProvider {
    Optional<BigDecimal> getLatestExchangeRateInRange(String currencyCode, LocalDate startDate, LocalDate endDate);
}
