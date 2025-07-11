package com.wex.challenge.infrastructure.adapter.persistence;

import com.wex.challenge.domain.model.Purchase;
import com.wex.challenge.domain.model.PurchaseId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PurchaseRepositoryAdapter.class)
public class PurchaseRepositoryAdapterIntegrationTest {

    @Autowired
    private PurchaseRepositoryAdapter purchaseRepositoryAdapter;

    @Autowired
    private PurchaseJpaRepository purchaseJpaRepository;

    @Test
    void save_shouldPersistPurchaseSuccessfully() {
        // Given
        Purchase purchase = Purchase.newPurchase("Test Book", LocalDate.of(2023, 1, 20), new BigDecimal("25.99"));

        // When
        Purchase savedPurchase = purchaseRepositoryAdapter.save(purchase);

        // Then
        assertNotNull(savedPurchase);
        assertNotNull(savedPurchase.getId().getValue());
        assertEquals(purchase.getDescription(), savedPurchase.getDescription());
        assertEquals(purchase.getTransactionDate(), savedPurchase.getTransactionDate());
        assertEquals(purchase.getPurchaseAmountUsd(), savedPurchase.getPurchaseAmountUsd());

        // Verify it's actually in the database
        Optional<PurchaseJpaEntity> foundEntity = purchaseJpaRepository.findById(savedPurchase.getId().getValue());
        assertTrue(foundEntity.isPresent());
        assertEquals(savedPurchase.getId().getValue(), foundEntity.get().getId());
    }

    @Test
    void findById_shouldRetrieveExistingPurchase() {
        // Given
        String id = "existing-purchase-id";
        PurchaseJpaEntity existingEntity = new PurchaseJpaEntity(id, "Existing Item", LocalDate.of(2022, 12, 1), new BigDecimal("75.00"));
        purchaseJpaRepository.save(existingEntity);

        // When
        Optional<Purchase> foundPurchase = purchaseRepositoryAdapter.findById(PurchaseId.from(id));

        // Then
        assertTrue(foundPurchase.isPresent());
        assertEquals(id, foundPurchase.get().getId().getValue());
        assertEquals("Existing Item", foundPurchase.get().getDescription().value());
    }

    @Test
    void findById_shouldReturnEmptyOptionalForNonExistingPurchase() {
        // Given
        String nonExistingId = "non-existing-id";

        // When
        Optional<Purchase> foundPurchase = purchaseRepositoryAdapter.findById(PurchaseId.from(nonExistingId));

        // Then
        assertFalse(foundPurchase.isPresent());
    }
}
