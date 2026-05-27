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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignatureTest {

    @Test
    void buildWithDefaults() {
        Signature sig = Signature.builder().build();
        assertEquals(0, sig.getSignatureIndex());
        assertTrue(sig.getSheets().isEmpty());
        assertTrue(sig.getLogicalPageNumbers().isEmpty());
    }

    @Test
    void builderSetsAllFields() {
        ImposedSheet sheet = ImposedSheet.builder().sheetIndex(0).signatureIndex(0).build();
        Signature sig = Signature.builder()
                .signatureIndex(2)
                .sheets(List.of(sheet))
                .logicalPageNumbers(List.of(1, 2, 3, 4))
                .build();
        assertEquals(2, sig.getSignatureIndex());
        assertEquals(1, sig.getSheets().size());
        assertEquals(List.of(1, 2, 3, 4), sig.getLogicalPageNumbers());
    }

    @Test
    void negativeSignatureIndexThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> Signature.builder().signatureIndex(-1));
    }

    @Test
    void nullSheetsThrows() {
        assertThrows(NullPointerException.class,
                () -> Signature.builder().sheets(null));
    }

    @Test
    void nullLogicalPageNumbersThrows() {
        assertThrows(NullPointerException.class,
                () -> Signature.builder().logicalPageNumbers(null));
    }

    @Test
    void equalsSameValues() {
        Signature a = Signature.builder().signatureIndex(0).build();
        Signature b = Signature.builder().signatureIndex(0).build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentIndex() {
        Signature a = Signature.builder().signatureIndex(0).build();
        Signature b = Signature.builder().signatureIndex(1).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsSelf() {
        Signature a = Signature.builder().build();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        assertNotEquals(Signature.builder().build(), null);
    }

    @Test
    void equalsWrongType() {
        assertNotEquals(Signature.builder().build(), "other");
    }

    @Test
    void equalsDifferentSheets() {
        ImposedSheet sheet = ImposedSheet.builder().sheetIndex(0).signatureIndex(0).build();
        Signature a = Signature.builder().signatureIndex(0).build();
        Signature b = Signature.builder().signatureIndex(0).sheets(List.of(sheet)).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentLogicalPageNumbers() {
        Signature a = Signature.builder().signatureIndex(0).build();
        Signature b = Signature.builder().signatureIndex(0).logicalPageNumbers(List.of(1, 2)).build();
        assertNotEquals(a, b);
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(Signature.builder().signatureIndex(0).sheets(List.of()).build().toString());
    }
}
