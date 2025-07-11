package com.wex.challenge.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponse {
    private String id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal purchaseAmountUsd;
}
