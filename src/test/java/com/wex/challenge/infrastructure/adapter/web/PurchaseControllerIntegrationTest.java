package com.wex.challenge.infrastructure.adapter.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wex.challenge.application.port.in.CreatePurchaseCommand;
import com.wex.challenge.application.port.out.PurchaseRepository;
import com.wex.challenge.domain.exception.ExchangeRateNotFoundException;
import com.wex.challenge.domain.model.Purchase;
import com.wex.challenge.domain.model.PurchaseId;
import com.wex.challenge.domain.service.CurrencyConversionService;
import com.wex.challenge.infrastructure.exception.NotSupportedCurrencyException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PurchaseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PurchaseRepository purchaseRepository;

    @MockitoBean
    private CurrencyConversionService currencyConversionService;

    @Test
    void createPurchase_shouldReturn201Created() throws Exception {
        // Given
        CreatePurchaseCommand command = new CreatePurchaseCommand();
        command.setDescription("New Gadget");
        command.setTransactionDate(LocalDate.of(2023, 7, 1));
        command.setPurchaseAmountUsd(new BigDecimal("99.99"));

        Purchase savedPurchase = Purchase.from(
                PurchaseId.generate().getValue(),
                command.getDescription(),
                command.getTransactionDate(),
                command.getPurchaseAmountUsd()
        );
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        // When & Then
        mockMvc.perform(post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("New Gadget"))
                .andExpect(jsonPath("$.purchaseAmountUsd").value(99.99));
    }

    @Test
    void createPurchase_shouldReturn400BadRequestForInvalidDescription() throws Exception {
        // Given
        CreatePurchaseCommand command = new CreatePurchaseCommand();
        command.setDescription("a".repeat(51)); // Too long
        command.setTransactionDate(LocalDate.of(2023, 7, 1));
        command.setPurchaseAmountUsd(new BigDecimal("10.00"));

        // When & Then
        mockMvc.perform(post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getConvertedPurchase_shouldReturn200Ok() throws Exception {
        // Given
        String purchaseId = "test-purchase-id";
        LocalDate transactionDate = LocalDate.of(2023, 7, 10);
        BigDecimal originalAmount = new BigDecimal("100.00");
        BigDecimal exchangeRate = new BigDecimal("1.25");
        String targetCurrencyCode = "CAD";

        Purchase existingPurchase = Purchase.from(purchaseId, "Test Item", transactionDate, originalAmount);

        when(purchaseRepository.findById(PurchaseId.from(purchaseId))).thenReturn(Optional.of(existingPurchase));
        when(currencyConversionService.getExchangeRate(Currency.getInstance(targetCurrencyCode), transactionDate))
                .thenReturn(exchangeRate);

        // When & Then
        mockMvc.perform(get("/purchases/{purchaseId}/converted/{targetCurrencyCode}", purchaseId, targetCurrencyCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(purchaseId))
                .andExpect(jsonPath("$.description").value("Test Item"))
                .andExpect(jsonPath("$.originalPurchaseAmountUsd").value(100.00))
                .andExpect(jsonPath("$.exchangeRateUsed").value(1.25))
                .andExpect(jsonPath("$.convertedAmount").value(125.00))
                .andExpect(jsonPath("$.targetCurrencyCode").value(targetCurrencyCode));
    }

    @Test
    void getConvertedPurchase_shouldReturn404NotFoundWhenPurchaseDoesNotExist() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        String targetCurrencyCode = "EUR";

        when(purchaseRepository.findById(PurchaseId.from(nonExistentId))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/purchases/{purchaseId}/converted/{targetCurrencyCode}", nonExistentId, targetCurrencyCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getConvertedPurchase_shouldReturn404NotFoundWhenExchangeRateNotAvailable() throws Exception {
        // Given
        String purchaseId = "test-purchase-id";
        LocalDate transactionDate = LocalDate.of(2023, 7, 10);
        BigDecimal originalAmount = new BigDecimal("100.00");
        String targetCurrencyCode = "AUD";

        Purchase existingPurchase = Purchase.from(purchaseId, "Test Item", transactionDate, originalAmount);

        when(purchaseRepository.findById(PurchaseId.from(purchaseId))).thenReturn(Optional.of(existingPurchase));
        when(currencyConversionService.getExchangeRate(Currency.getInstance(targetCurrencyCode), transactionDate))
                .thenThrow(new ExchangeRateNotFoundException("Rate not found"));

        // When & Then
        mockMvc.perform(get("/purchases/{purchaseId}/converted/{targetCurrencyCode}", purchaseId, targetCurrencyCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getConvertedPurchase_shouldReturn400BadRequestForInvalidCurrencyCode() throws Exception {
        // Given
        String purchaseId = "test-purchase-id";
        String invalidCurrencyCode = "INVALID";

        // When & Then
        mockMvc.perform(get("/purchases/{purchaseId}/converted/{targetCurrencyCode}", purchaseId, invalidCurrencyCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getConvertedPurchase_shouldReturn400BadRequestWhenCurrencyNotSupported() throws Exception {
        String purchaseId = "test-purchase-id-unsupported";
        LocalDate transactionDate = LocalDate.of(2024, 6, 28);
        BigDecimal originalAmount = new BigDecimal("75.00");
        String unsupportedCurrencyCode = "SEK";

        Purchase existingPurchase = Purchase.from(purchaseId, "Unsupported Currency Item", transactionDate, originalAmount);

        when(purchaseRepository.findById(PurchaseId.from(purchaseId))).thenReturn(Optional.of(existingPurchase));
        when(currencyConversionService.getExchangeRate(Currency.getInstance(unsupportedCurrencyCode), transactionDate))
                .thenThrow(new NotSupportedCurrencyException("No Treasury country name mapping found for ISO code: " + unsupportedCurrencyCode));

        mockMvc.perform(get("/purchases/{purchaseId}/converted/{targetCurrencyCode}", purchaseId, unsupportedCurrencyCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No Treasury country name mapping found for ISO code: SEK"));
    }
}
