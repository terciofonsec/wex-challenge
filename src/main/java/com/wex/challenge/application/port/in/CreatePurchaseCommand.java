package com.wex.challenge.application.port.in;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePurchaseCommand {
    @NotBlank
    private String description;
    @NotNull
    private LocalDate transactionDate;
    @Positive
    private BigDecimal purchaseAmountUsd;
}
