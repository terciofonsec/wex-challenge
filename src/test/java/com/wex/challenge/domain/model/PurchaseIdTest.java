package com.wex.challenge.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PurchaseIdTest {

    @Test
    void purchaseId_generate_shouldCreateUniqueIds() {
        PurchaseId id1 = PurchaseId.generate();
        PurchaseId id2 = PurchaseId.generate();
        assertNotNull(id1.getValue());
        assertNotNull(id2.getValue());
        assertNotEquals(id1, id2); // Ensure they are different
    }

    @Test
    void purchaseId_from_shouldCreateIdWithGivenValue() {
        String uuid = "a1b2c3d4-e5f6-7890-1234-567890abcdef";
        PurchaseId id = PurchaseId.from(uuid);
        assertEquals(uuid, id.getValue());
    }

    @Test
    void purchaseId_shouldBeEqualForSameValue() {
        String uuid = "a1b2c3d4-e5f6-7890-1234-567890abcdef";
        PurchaseId id1 = PurchaseId.from(uuid);
        PurchaseId id2 = PurchaseId.from(uuid);
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void purchaseId_shouldThrowExceptionForNullValue() {
        assertThrows(IllegalArgumentException.class, () -> PurchaseId.from(null));
    }

    @Test
    void purchaseId_shouldThrowExceptionForEmptyValue() {
        assertThrows(IllegalArgumentException.class, () -> PurchaseId.from(""));
        assertThrows(IllegalArgumentException.class, () -> PurchaseId.from("   "));
    }
}
