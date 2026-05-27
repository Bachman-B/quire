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

class CreepWarningTest {

    private CreepWarning.Builder validBuilder() {
        return CreepWarning.builder()
                .severity(WarningSeverity.WARNING)
                .sheetIndex(1)
                .affectedPages(List.of(3, 4))
                .creepMm(2.0)
                .availableMarginMm(1.0)
                .deficitMm(1.0);
    }

    @Test
    void builderSetsAllFields() {
        CreepWarning w = validBuilder().build();
        assertEquals(WarningSeverity.WARNING, w.getSeverity());
        assertEquals(1, w.getSheetIndex());
        assertEquals(List.of(3, 4), w.getAffectedPages());
        assertEquals(2.0, w.getCreepMm());
        assertEquals(1.0, w.getAvailableMarginMm());
        assertEquals(1.0, w.getDeficitMm());
    }

    @Test
    void missingSeverityInBuildThrows() {
        assertThrows(NullPointerException.class,
                () -> CreepWarning.builder()
                        .sheetIndex(0).creepMm(1.0).availableMarginMm(0.0).deficitMm(1.0)
                        .build());
    }

    @Test
    void negativeSheetIndexThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepWarning.builder().sheetIndex(-1));
    }

    @Test
    void creepMmZeroThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepWarning.builder().creepMm(0.0));
    }

    @Test
    void creepMmNegativeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepWarning.builder().creepMm(-1.0));
    }

    @Test
    void negativeAvailableMarginThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepWarning.builder().availableMarginMm(-0.1));
    }

    @Test
    void deficitMmZeroThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepWarning.builder().deficitMm(0.0));
    }

    @Test
    void deficitMmNegativeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepWarning.builder().deficitMm(-1.0));
    }

    @Test
    void nullAffectedPagesThrows() {
        assertThrows(NullPointerException.class,
                () -> CreepWarning.builder().affectedPages(null));
    }

    @Test
    void equalsSameValues() {
        CreepWarning a = validBuilder().build();
        CreepWarning b = validBuilder().build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentSheetIndex() {
        CreepWarning a = validBuilder().build();
        CreepWarning b = validBuilder().sheetIndex(2).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsSelf() {
        CreepWarning a = validBuilder().build();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        assertNotEquals(validBuilder().build(), null);
    }

    @Test
    void equalsWrongType() {
        assertNotEquals(validBuilder().build(), "other");
    }

    @Test
    void equalsDifferentCreepMm() {
        CreepWarning a = validBuilder().build();
        CreepWarning b = validBuilder().creepMm(3.0).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentAvailableMarginMm() {
        CreepWarning a = validBuilder().build();
        CreepWarning b = validBuilder().availableMarginMm(2.0).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentDeficitMm() {
        CreepWarning a = validBuilder().build();
        CreepWarning b = validBuilder().deficitMm(2.0).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentSeverity() {
        CreepWarning a = validBuilder().build();
        CreepWarning b = validBuilder().severity(WarningSeverity.ERROR).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentAffectedPages() {
        CreepWarning a = validBuilder().build();
        CreepWarning b = validBuilder().affectedPages(List.of(5, 6)).build();
        assertNotEquals(a, b);
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(validBuilder().build().toString());
    }
}
