/*
 * Copyright 2025 Quire Contributors
 *
 * This file is part of Quire.
 *
 * Quire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Quire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Quire.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.quire.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaddingConfigTest {

    @Test
    void defaultsAreAllZero() {
        PaddingConfig cfg = PaddingConfig.builder().build();
        assertEquals(0, cfg.getAestheticFront());
        assertEquals(0, cfg.getAestheticRear());
        assertEquals(0, cfg.getCompletionFront());
        assertEquals(0, cfg.getCompletionRear());
        assertEquals(0, cfg.getSignatureSize());
    }

    @Test
    void builderSetsAllFields() {
        PaddingConfig cfg = PaddingConfig.builder()
                .aestheticFront(2)
                .aestheticRear(2)
                .completionFront(1)
                .completionRear(3)
                .signatureSize(4)
                .build();
        assertEquals(2, cfg.getAestheticFront());
        assertEquals(2, cfg.getAestheticRear());
        assertEquals(1, cfg.getCompletionFront());
        assertEquals(3, cfg.getCompletionRear());
        assertEquals(4, cfg.getSignatureSize());
    }

    @Test
    void negativeAestheticFrontThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> PaddingConfig.builder().aestheticFront(-1));
    }

    @Test
    void negativeAestheticRearThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> PaddingConfig.builder().aestheticRear(-1));
    }

    @Test
    void negativeCompletionFrontThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> PaddingConfig.builder().completionFront(-1));
    }

    @Test
    void negativeCompletionRearThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> PaddingConfig.builder().completionRear(-1));
    }

    @Test
    void negativeSignatureSizeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> PaddingConfig.builder().signatureSize(-1));
    }

    @Test
    void toBuilderRoundtrip() {
        PaddingConfig original = PaddingConfig.builder()
                .aestheticFront(1)
                .aestheticRear(2)
                .completionFront(3)
                .completionRear(4)
                .signatureSize(5)
                .build();
        PaddingConfig copy = original.toBuilder().build();
        assertEquals(original, copy);
    }

    @Test
    void equalsSameValues() {
        PaddingConfig a = PaddingConfig.builder().aestheticFront(1).build();
        PaddingConfig b = PaddingConfig.builder().aestheticFront(1).build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentValues() {
        PaddingConfig a = PaddingConfig.builder().aestheticFront(1).build();
        PaddingConfig b = PaddingConfig.builder().aestheticFront(2).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsSelf() {
        PaddingConfig a = PaddingConfig.builder().build();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        assertNotEquals(PaddingConfig.builder().build(), null);
    }

    @Test
    void equalsWrongType() {
        assertNotEquals(PaddingConfig.builder().build(), "not a config");
    }

    @Test
    void equalsDifferentAestheticRear() {
        PaddingConfig a = PaddingConfig.builder().build();
        PaddingConfig b = PaddingConfig.builder().aestheticRear(1).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentCompletionFront() {
        PaddingConfig a = PaddingConfig.builder().build();
        PaddingConfig b = PaddingConfig.builder().completionFront(1).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentCompletionRear() {
        PaddingConfig a = PaddingConfig.builder().build();
        PaddingConfig b = PaddingConfig.builder().completionRear(1).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentSignatureSize() {
        PaddingConfig a = PaddingConfig.builder().build();
        PaddingConfig b = PaddingConfig.builder().signatureSize(4).build();
        assertNotEquals(a, b);
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(PaddingConfig.builder().build().toString());
    }
}
