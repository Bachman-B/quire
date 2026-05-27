/*
 * Copyright 2025 QuireBind Contributors
 *
 * This file is part of QuireBind.
 *
 * QuireBind is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QuireBind is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with QuireBind.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.maiitsoh.quirebind.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreepSheetResultTest {

    @Test
    void builderSetsAllFields() {
        CreepSheetResult r = CreepSheetResult.builder()
                .sheetIndex(2)
                .creepMm(1.5)
                .pageNumbers(List.of(5, 6, 7, 8))
                .availableSpineMarginMm(3.0)
                .marginWarning(true)
                .marginDeficitMm(0.5)
                .build();
        assertEquals(2, r.getSheetIndex());
        assertEquals(1.5, r.getCreepMm());
        assertEquals(List.of(5, 6, 7, 8), r.getPageNumbers());
        assertEquals(3.0, r.getAvailableSpineMarginMm().orElseThrow());
        assertTrue(r.isMarginWarning());
        assertEquals(0.5, r.getMarginDeficitMm());
    }

    @Test
    void availableSpineMarginAbsentByDefault() {
        CreepSheetResult r = CreepSheetResult.builder().build();
        assertTrue(r.getAvailableSpineMarginMm().isEmpty());
    }

    @Test
    void availableSpineMarginNullClearsOptional() {
        CreepSheetResult r = CreepSheetResult.builder().availableSpineMarginMm(null).build();
        assertTrue(r.getAvailableSpineMarginMm().isEmpty());
    }

    @Test
    void negativeSheetIndexThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepSheetResult.builder().sheetIndex(-1));
    }

    @Test
    void negativeCreepMmThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepSheetResult.builder().creepMm(-0.1));
    }

    @Test
    void negativeMarginDeficitMmThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepSheetResult.builder().marginDeficitMm(-0.1));
    }

    @Test
    void nullPageNumbersThrows() {
        assertThrows(NullPointerException.class,
                () -> CreepSheetResult.builder().pageNumbers(null));
    }

    @Test
    void equalsSameValues() {
        CreepSheetResult a = CreepSheetResult.builder()
                .sheetIndex(0).creepMm(1.0).marginDeficitMm(0.0).build();
        CreepSheetResult b = CreepSheetResult.builder()
                .sheetIndex(0).creepMm(1.0).marginDeficitMm(0.0).build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentValues() {
        CreepSheetResult a = CreepSheetResult.builder().sheetIndex(0).creepMm(1.0).build();
        CreepSheetResult b = CreepSheetResult.builder().sheetIndex(1).creepMm(1.0).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsSelf() {
        CreepSheetResult a = CreepSheetResult.builder().build();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        assertNotEquals(CreepSheetResult.builder().build(), null);
    }

    @Test
    void equalsWrongType() {
        assertNotEquals(CreepSheetResult.builder().build(), "other");
    }

    @Test
    void equalsDifferentCreepMm() {
        CreepSheetResult a = CreepSheetResult.builder().sheetIndex(0).creepMm(1.0).build();
        CreepSheetResult b = CreepSheetResult.builder().sheetIndex(0).creepMm(2.0).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentMarginWarning() {
        CreepSheetResult a = CreepSheetResult.builder().sheetIndex(0).creepMm(1.0).build();
        CreepSheetResult b = CreepSheetResult.builder().sheetIndex(0).creepMm(1.0)
                .marginWarning(true).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentMarginDeficitMm() {
        CreepSheetResult a = CreepSheetResult.builder().sheetIndex(0).creepMm(1.0)
                .marginDeficitMm(0.0).build();
        CreepSheetResult b = CreepSheetResult.builder().sheetIndex(0).creepMm(1.0)
                .marginDeficitMm(0.5).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentPageNumbers() {
        CreepSheetResult a = CreepSheetResult.builder().sheetIndex(0).creepMm(1.0)
                .marginDeficitMm(0.0).pageNumbers(List.of(1, 2)).build();
        CreepSheetResult b = CreepSheetResult.builder().sheetIndex(0).creepMm(1.0)
                .marginDeficitMm(0.0).pageNumbers(List.of(3, 4)).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentAvailableSpineMarginMm() {
        CreepSheetResult a = CreepSheetResult.builder().sheetIndex(0).creepMm(1.0)
                .marginDeficitMm(0.0).pageNumbers(List.of(1)).availableSpineMarginMm(3.0).build();
        CreepSheetResult b = CreepSheetResult.builder().sheetIndex(0).creepMm(1.0)
                .marginDeficitMm(0.0).pageNumbers(List.of(1)).availableSpineMarginMm(4.0).build();
        assertNotEquals(a, b);
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(CreepSheetResult.builder().build().toString());
    }
}
