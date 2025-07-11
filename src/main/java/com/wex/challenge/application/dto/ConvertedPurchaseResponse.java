package com.wex.challenge.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvertedPurchaseResponse {
    private String id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal originalPurchaseAmountUsd;
    private BigDecimal exchangeRateUsed;
    private BigDecimal convertedAmount;
    private String targetCurrencyCode;
}
