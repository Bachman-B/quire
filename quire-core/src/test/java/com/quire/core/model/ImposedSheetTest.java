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

class ImposedSheetTest {

    private QuirePage contentPage(int pos) {
        return QuirePage.builder().physicalPosition(pos).pageType(PageType.CONTENT).build();
    }

    @Test
    void buildWithDefaults() {
        ImposedSheet sheet = ImposedSheet.builder().build();
        assertEquals(0, sheet.getSheetIndex());
        assertEquals(0, sheet.getSignatureIndex());
        assertTrue(sheet.getFrontPages().isEmpty());
        assertTrue(sheet.getBackPages().isEmpty());
        assertTrue(sheet.getCreepResult().isEmpty());
    }

    @Test
    void builderSetsAllFields() {
        CreepSheetResult creep = CreepSheetResult.builder().sheetIndex(0).build();
        ImposedSheet sheet = ImposedSheet.builder()
                .sheetIndex(1)
                .signatureIndex(2)
                .frontPages(List.of(contentPage(0), contentPage(3)))
                .backPages(List.of(contentPage(1), contentPage(2)))
                .creepResult(creep)
                .build();
        assertEquals(1, sheet.getSheetIndex());
        assertEquals(2, sheet.getSignatureIndex());
        assertEquals(2, sheet.getFrontPages().size());
        assertEquals(2, sheet.getBackPages().size());
        assertTrue(sheet.getCreepResult().isPresent());
    }

    @Test
    void creepResultNullClearsOptional() {
        ImposedSheet sheet = ImposedSheet.builder().creepResult(null).build();
        assertTrue(sheet.getCreepResult().isEmpty());
    }

    @Test
    void negativeSheetIndexThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ImposedSheet.builder().sheetIndex(-1));
    }

    @Test
    void negativeSignatureIndexThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ImposedSheet.builder().signatureIndex(-1));
    }

    @Test
    void nullFrontPagesThrows() {
        assertThrows(NullPointerException.class,
                () -> ImposedSheet.builder().frontPages(null));
    }

    @Test
    void nullBackPagesThrows() {
        assertThrows(NullPointerException.class,
                () -> ImposedSheet.builder().backPages(null));
    }

    @Test
    void equalsSameValues() {
        ImposedSheet a = ImposedSheet.builder().sheetIndex(0).signatureIndex(0).build();
        ImposedSheet b = ImposedSheet.builder().sheetIndex(0).signatureIndex(0).build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentSheetIndex() {
        ImposedSheet a = ImposedSheet.builder().sheetIndex(0).build();
        ImposedSheet b = ImposedSheet.builder().sheetIndex(1).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsSelf() {
        ImposedSheet a = ImposedSheet.builder().build();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        assertNotEquals(ImposedSheet.builder().build(), null);
    }

    @Test
    void equalsWrongType() {
        assertNotEquals(ImposedSheet.builder().build(), "other");
    }

    @Test
    void equalsDifferentSignatureIndex() {
        ImposedSheet a = ImposedSheet.builder().sheetIndex(0).signatureIndex(0).build();
        ImposedSheet b = ImposedSheet.builder().sheetIndex(0).signatureIndex(1).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentFrontPages() {
        ImposedSheet a = ImposedSheet.builder().sheetIndex(0).signatureIndex(0).build();
        ImposedSheet b = ImposedSheet.builder().sheetIndex(0).signatureIndex(0)
                .frontPages(List.of(contentPage(0))).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentBackPages() {
        ImposedSheet a = ImposedSheet.builder().sheetIndex(0).signatureIndex(0).build();
        ImposedSheet b = ImposedSheet.builder().sheetIndex(0).signatureIndex(0)
                .backPages(List.of(contentPage(0))).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentCreepResult() {
        CreepSheetResult creep = CreepSheetResult.builder().sheetIndex(0).build();
        ImposedSheet a = ImposedSheet.builder().sheetIndex(0).signatureIndex(0).build();
        ImposedSheet b = ImposedSheet.builder().sheetIndex(0).signatureIndex(0)
                .creepResult(creep).build();
        assertNotEquals(a, b);
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(ImposedSheet.builder().build().toString());
    }
}
