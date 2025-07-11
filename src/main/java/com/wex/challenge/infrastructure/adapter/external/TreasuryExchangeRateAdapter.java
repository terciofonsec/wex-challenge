package com.wex.challenge.infrastructure.adapter.external;

import com.wex.challenge.application.port.out.ExchangeRateProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class TreasuryExchangeRateAdapter implements ExchangeRateProvider {
    private final TreasuryExchangeRateClient treasuryExchangeRateClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int PAGE_SIZE = 1;


    @Override
    public Optional<BigDecimal> getLatestExchangeRateInRange(String currencyCode, LocalDate startDate, LocalDate endDate) {
        String filter = String.format(
                "currency:eq:%s,record_date:gte:%s,record_date:lte:%s",
                currencyCode,
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER)
        );

        String sort = "-record_date";

        try {
            TreasuryExchangeRateResponse response = treasuryExchangeRateClient.getExchangeRates(filter, sort, PAGE_SIZE);

            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                return Optional.ofNullable(response.getData().get(0).getExchangeRate());
            }
        } catch (Exception e) {
           log.error("Error on finding most recently exchange rate from Treasury api for currency {} between: {} and : {} msg: {}", currencyCode, startDate, endDate, e.getMessage());
        }
        return Optional.empty();
    }
}
