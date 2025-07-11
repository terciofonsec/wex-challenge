package com.wex.challenge.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveConvertedPurchaseQuery {
    private String purchaseId;
    private String targetCurrencyCode;
}
