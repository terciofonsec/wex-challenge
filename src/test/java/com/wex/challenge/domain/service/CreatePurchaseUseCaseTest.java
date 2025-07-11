package com.wex.challenge.domain.service;

import com.wex.challenge.application.dto.PurchaseResponse;
import com.wex.challenge.application.port.in.CreatePurchaseCommand;
import com.wex.challenge.application.port.out.PurchaseRepository;
import com.wex.challenge.application.service.CreatePurchaseUseCase;
import com.wex.challenge.domain.exception.InvalidPurchaseException;
import com.wex.challenge.domain.model.Purchase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreatePurchaseUseCaseTest {

    @Mock
    private PurchaseRepository purchaseRepository;


    private CreatePurchaseUseCase createPurchaseUseCase;

    @BeforeEach
    void setUp() {
        createPurchaseUseCase = new CreatePurchaseUseCase(purchaseRepository);
    }

    @Test
    void execute_shouldCreateAndSavePurchaseSuccessfully() {
        // Given
        CreatePurchaseCommand command = new CreatePurchaseCommand();
        command.setDescription("Laptop");
        command.setTransactionDate(LocalDate.of(2023, 1, 1));
        command.setPurchaseAmountUsd(new BigDecimal("1200.50"));

        // Simulate saving
        Purchase savedPurchase = Purchase.from(
                "test-id-123",
                command.getDescription(),
                command.getTransactionDate(),
                command.getPurchaseAmountUsd()
        );
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        // When
        PurchaseResponse response = createPurchaseUseCase.execute(command);

        // Then
        assertNotNull(response);
        assertEquals("test-id-123", response.getId());
        assertEquals("Laptop", response.getDescription());
        assertEquals(LocalDate.of(2023, 1, 1), response.getTransactionDate());
        assertEquals(new BigDecimal("1200.50"), response.getPurchaseAmountUsd());

        // Verify that save was called with a Purchase object
        ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
        verify(purchaseRepository, times(1)).save(purchaseCaptor.capture());
        Purchase capturedPurchase = purchaseCaptor.getValue();
        assertNotNull(capturedPurchase.getId()); // ID should be generated
        assertEquals("Laptop", capturedPurchase.getDescription().value());
    }

    @Test
    void execute_shouldHandleInvalidCommandDataGracefully() {
        // Given an invalid command (e.g., null description)
        CreatePurchaseCommand command = new CreatePurchaseCommand();
        command.setDescription(null); // Invalid
        command.setTransactionDate(LocalDate.now());
        command.setPurchaseAmountUsd(new BigDecimal("10.00"));

        // When / Then
        assertThrows(InvalidPurchaseException.class, () ->
                createPurchaseUseCase.execute(command)
        );
        verify(purchaseRepository, never()).save(any(Purchase.class)); // Should not attempt to save
    }
}
