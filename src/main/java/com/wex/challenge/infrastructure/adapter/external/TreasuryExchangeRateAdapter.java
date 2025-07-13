package com.wex.challenge.infrastructure.adapter.external;

import com.wex.challenge.application.port.out.ExchangeRateProvider;
import com.wex.challenge.infrastructure.exception.NotSupportedCurrencyException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class TreasuryExchangeRateAdapter implements ExchangeRateProvider {
    private final TreasuryExchangeRateClient treasuryExchangeRateClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int PAGE_SIZE = 1;

/*   I saw that Fiscal Data API does not use ISO currency code for their data, so we need to pass the country name or they currency description in order to retrieve the exchange rate
     so that, I created this map here, but usually we have to have a complete map or ate least a most common currency supported by platform to convert, if all currency should be supported
     we should have a full map for it previously loaded. I could not find any api from Treasury service that can map those value back.

     Other option would be receiving the country name or Treasury currency code directly from the api
*/

    private static final Map<String, String> ISO_CODE_TO_TREASURY_COUNTRY_MAP = Map.of(
            "USD", "United States",
            "EUR", "Euro Zone",
            "BRL", "Brazil",
            "CAD", "Canada",
            "JPY", "Japan",
            "GBP", "United Kingdom",
            "AUD", "Australia",
            "MXN", "Mexico",
            "CHF", "Switzerland",
            "CNY", "China"
    );


    @Override
    public Optional<BigDecimal> getLatestExchangeRateInRange(String currencyCode, LocalDate startDate, LocalDate endDate) {
        if (currencyCode.equals("USD")) {
            return Optional.of(BigDecimal.ONE);
        }

        String countryName = getTreasuryCountryName(currencyCode);

        String filter = String.format(
                "country:eq:%s,record_date:gte:%s,record_date:lte:%s",
                countryName,
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER)
        );

        String sort = "-record_date";

        try {
            TreasuryExchangeRateResponse response = treasuryExchangeRateClient.getExchangeRates(filter, sort, PAGE_SIZE);

            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                BigDecimal rate = response.getData().get(0).getExchangeRate();
                log.info("Exchange rate for {} on {}: {}", currencyCode, endDate, rate);
                return Optional.of(rate);
            }
        } catch (Exception e) {
            log.error("Error on finding most recently exchange rate from Treasury api for currency {} between: {} and : {} msg: {}", currencyCode, startDate, endDate, e.getMessage());
        }
        return Optional.empty();
    }

    private String getTreasuryCountryName(String currencyCode) {
        String countryName = ISO_CODE_TO_TREASURY_COUNTRY_MAP.get(currencyCode.toUpperCase());
        if (countryName == null) {
            log.error("No Treasury country name mapping found for ISO code: {}. Please add it to ISO_CODE_TO_TREASURY_COUNTRY_MAP.", currencyCode);
            throw new NotSupportedCurrencyException("No Treasury country name mapping found for ISO code:" + currencyCode);
        }
        return countryName;
    }
}
