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

class CreepConfigTest {

    @Test
    void defaultsAreEmpty() {
        CreepConfig cfg = CreepConfig.builder().build();
        assertTrue(cfg.getPaperWeightGsm().isEmpty());
        assertTrue(cfg.getPaperThicknessMm().isEmpty());
        assertTrue(cfg.getCalculatedCreepMm().isEmpty());
        assertTrue(cfg.getSheetResults().isEmpty());
        assertFalse(cfg.isTransformApplied());
        assertFalse(cfg.isApplyToOutput());
    }

    @Test
    void builderSetsAllFields() {
        CreepSheetResult sheet = CreepSheetResult.builder().sheetIndex(0).build();
        CreepConfig cfg = CreepConfig.builder()
                .paperWeightGsm(80.0)
                .paperThicknessMm(0.1)
                .calculatedCreepMm(0.5)
                .sheetResults(List.of(sheet))
                .transformApplied(true)
                .applyToOutput(true)
                .build();
        assertEquals(80.0, cfg.getPaperWeightGsm().orElseThrow());
        assertEquals(0.1, cfg.getPaperThicknessMm().orElseThrow());
        assertEquals(0.5, cfg.getCalculatedCreepMm().orElseThrow());
        assertEquals(1, cfg.getSheetResults().size());
        assertTrue(cfg.isTransformApplied());
        assertTrue(cfg.isApplyToOutput());
    }

    @Test
    void paperWeightGsmNullClearsOptional() {
        CreepConfig cfg = CreepConfig.builder().paperWeightGsm(null).build();
        assertTrue(cfg.getPaperWeightGsm().isEmpty());
    }

    @Test
    void paperWeightGsmZeroThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepConfig.builder().paperWeightGsm(0.0));
    }

    @Test
    void paperWeightGsmNegativeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepConfig.builder().paperWeightGsm(-1.0));
    }

    @Test
    void paperThicknessMmNullClearsOptional() {
        CreepConfig cfg = CreepConfig.builder().paperThicknessMm(null).build();
        assertTrue(cfg.getPaperThicknessMm().isEmpty());
    }

    @Test
    void paperThicknessMmZeroThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepConfig.builder().paperThicknessMm(0.0));
    }

    @Test
    void paperThicknessMmNegativeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepConfig.builder().paperThicknessMm(-0.1));
    }

    @Test
    void calculatedCreepMmNullClearsOptional() {
        CreepConfig cfg = CreepConfig.builder().calculatedCreepMm(null).build();
        assertTrue(cfg.getCalculatedCreepMm().isEmpty());
    }

    @Test
    void calculatedCreepMmNegativeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CreepConfig.builder().calculatedCreepMm(-0.1));
    }

    @Test
    void calculatedCreepMmZeroIsAllowed() {
        CreepConfig cfg = CreepConfig.builder().calculatedCreepMm(0.0).build();
        assertEquals(0.0, cfg.getCalculatedCreepMm().orElseThrow());
    }

    @Test
    void nullSheetResultsThrows() {
        assertThrows(NullPointerException.class,
                () -> CreepConfig.builder().sheetResults(null));
    }

    @Test
    void toBuilderRoundtrip() {
        CreepConfig original = CreepConfig.builder()
                .paperWeightGsm(90.0)
                .calculatedCreepMm(0.3)
                .build();
        assertEquals(original, original.toBuilder().build());
    }

    @Test
    void equalsSameValues() {
        CreepConfig a = CreepConfig.builder().paperWeightGsm(80.0).build();
        CreepConfig b = CreepConfig.builder().paperWeightGsm(80.0).build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentValues() {
        CreepConfig a = CreepConfig.builder().paperWeightGsm(80.0).build();
        CreepConfig b = CreepConfig.builder().paperWeightGsm(90.0).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsSelf() {
        CreepConfig a = CreepConfig.builder().build();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        assertNotEquals(CreepConfig.builder().build(), null);
    }

    @Test
    void equalsWrongType() {
        assertNotEquals(CreepConfig.builder().build(), "other");
    }

    @Test
    void equalsDifferentTransformApplied() {
        CreepConfig a = CreepConfig.builder().build();
        CreepConfig b = CreepConfig.builder().transformApplied(true).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentApplyToOutput() {
        CreepConfig a = CreepConfig.builder().build();
        CreepConfig b = CreepConfig.builder().applyToOutput(true).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentPaperThicknessMm() {
        CreepConfig a = CreepConfig.builder().paperWeightGsm(80.0).paperThicknessMm(0.1).build();
        CreepConfig b = CreepConfig.builder().paperWeightGsm(80.0).paperThicknessMm(0.2).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentCalculatedCreepMm() {
        CreepConfig a = CreepConfig.builder().paperWeightGsm(80.0).paperThicknessMm(0.1)
                .calculatedCreepMm(0.5).build();
        CreepConfig b = CreepConfig.builder().paperWeightGsm(80.0).paperThicknessMm(0.1)
                .calculatedCreepMm(1.0).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentSheetResults() {
        CreepSheetResult sheet = CreepSheetResult.builder().sheetIndex(0).build();
        CreepConfig a = CreepConfig.builder().paperWeightGsm(80.0).paperThicknessMm(0.1)
                .calculatedCreepMm(0.5).build();
        CreepConfig b = CreepConfig.builder().paperWeightGsm(80.0).paperThicknessMm(0.1)
                .calculatedCreepMm(0.5).sheetResults(List.of(sheet)).build();
        assertNotEquals(a, b);
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(CreepConfig.builder().build().toString());
    }
}
