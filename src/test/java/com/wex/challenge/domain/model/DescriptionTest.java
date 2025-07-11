package com.wex.challenge.domain.model;

import com.wex.challenge.domain.exception.InvalidPurchaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DescriptionTest {

    @Test
    void description_shouldBeCreatedWithValidValue() {
        Description desc = new Description("Test Description");
        assertEquals("Test Description", desc.value());
    }

    @Test
    void description_shouldThrowExceptionForNullValue() {
        assertThrows(InvalidPurchaseException.class, () -> new Description(null));
    }

    @Test
    void description_shouldThrowExceptionForEmptyValue() {
        assertThrows(InvalidPurchaseException.class, () -> new Description(""));
        assertThrows(InvalidPurchaseException.class, () -> new Description("   "));
    }

    @Test
    void description_shouldThrowExceptionForTooLongValue() {
        String longDesc = "a".repeat(51);
        assertThrows(InvalidPurchaseException.class, () -> new Description(longDesc));
    }

    @Test
    void description_shouldBeEqualForSameValue() {
        Description desc1 = new Description("Item A");
        Description desc2 = new Description("Item A");
        assertEquals(desc1, desc2);
        assertEquals(desc1.hashCode(), desc2.hashCode());
    }
}
