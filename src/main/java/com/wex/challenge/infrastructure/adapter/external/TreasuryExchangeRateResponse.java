package com.wex.challenge.infrastructure.adapter.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryExchangeRateResponse {
    private List<ExchangeRateData> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExchangeRateData {
        @JsonProperty("country_currency_desc")
        private String countryCurrencyDesc;

        @JsonProperty("exchange_rate")
        private BigDecimal exchangeRate;

        @JsonProperty("record_date")
        private LocalDate recordDate;

        @JsonProperty("currency_type")
        private String currencyType;

        @JsonProperty("currency_description")
        private String currencyDescription;

        @JsonProperty("country")
        private String country;
    }
}
