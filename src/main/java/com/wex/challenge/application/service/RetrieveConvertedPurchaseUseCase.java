package com.wex.challenge.application.service;

import com.wex.challenge.application.dto.ConvertedPurchaseResponse;
import com.wex.challenge.application.port.in.RetrieveConvertedPurchaseQuery;
import com.wex.challenge.application.port.out.PurchaseRepository;
import com.wex.challenge.domain.exception.ExchangeRateNotFoundException;
import com.wex.challenge.domain.model.CurrencyAmount;
import com.wex.challenge.domain.model.Purchase;
import com.wex.challenge.domain.model.PurchaseId;
import com.wex.challenge.domain.service.CurrencyConversionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional(readOnly=true)
@AllArgsConstructor
public class RetrieveConvertedPurchaseUseCase {

    private final PurchaseRepository purchaseRepository;
    private final CurrencyConversionService currencyConversionService;

    public ConvertedPurchaseResponse execute(RetrieveConvertedPurchaseQuery query) {
        PurchaseId purchaseId = PurchaseId.from(query.getPurchaseId());
        Currency targetCurrency;
        try {
            targetCurrency = Currency.getInstance(query.getTargetCurrencyCode());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code: " + query.getTargetCurrencyCode());
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new NoSuchElementException("Purchase ID " + query.getPurchaseId() + " not found."));

        try {
            BigDecimal exchangeRate = currencyConversionService.getExchangeRate(targetCurrency, purchase.getTransactionDate().value());
            CurrencyAmount convertedAmount = purchase.convertTo(exchangeRate, targetCurrency);


            ConvertedPurchaseResponse response =  new ConvertedPurchaseResponse(
                    purchase.getId().getValue(),
                    purchase.getDescription().value(),
                    purchase.getTransactionDate().value(),
                    purchase.getPurchaseAmountUsd().value(),
                    exchangeRate,
                    convertedAmount.value(),
                    convertedAmount.currency().getCurrencyCode()
            );

            log.info("Purchase retrieve and converted with success, purchaseResponse={}", response);

            return response;
        } catch (ExchangeRateNotFoundException e) {
            log.error("Conversion errored, exchange rate not found, msg:{}", e.getMessage());
            throw e;
        }


    }
}
