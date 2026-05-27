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
package com.quirebind.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BindingTechniqueTest {

    @Test
    void valuesExist() {
        assertEquals(9, BindingTechnique.values().length);
    }

    @Test
    void perfectBindingIsGroupA() {
        assertEquals(ImpositionGroup.A, BindingTechnique.PERFECT_BINDING.group());
    }

    @Test
    void spiralIsGroupA() {
        assertEquals(ImpositionGroup.A, BindingTechnique.SPIRAL.group());
    }

    @Test
    void japaneseStabIsGroupA() {
        assertEquals(ImpositionGroup.A, BindingTechnique.JAPANESE_STAB.group());
    }

    @Test
    void saddleStitchIsGroupB() {
        assertEquals(ImpositionGroup.B, BindingTechnique.SADDLE_STITCH.group());
    }

    @Test
    void pamphletIsGroupB() {
        assertEquals(ImpositionGroup.B, BindingTechnique.PAMPHLET.group());
    }

    @Test
    void bookletIsGroupB() {
        assertEquals(ImpositionGroup.B, BindingTechnique.BOOKLET.group());
    }

    @Test
    void sewnSignaturesIsGroupC() {
        assertEquals(ImpositionGroup.C, BindingTechnique.SEWN_SIGNATURES.group());
    }

    @Test
    void hardcoverIsGroupC() {
        assertEquals(ImpositionGroup.C, BindingTechnique.HARDCOVER.group());
    }

    @Test
    void copticIsGroupC() {
        assertEquals(ImpositionGroup.C, BindingTechnique.COPTIC.group());
    }

    @Test
    void valueOfRoundtrips() {
        for (BindingTechnique bt : BindingTechnique.values()) {
            assertEquals(bt, BindingTechnique.valueOf(bt.name()));
            assertNotNull(bt.group());
        }
    }
}
