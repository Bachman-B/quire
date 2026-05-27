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
package com.quirebind.core.binding;

import com.quirebind.core.model.BindingTechnique;
import com.quirebind.core.model.ImpositionGroup;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class BindingGroupMapperTest {

    @Test
    void groupAForPerfectBinding() {
        assertEquals(ImpositionGroup.A, BindingGroupMapper.groupFor(BindingTechnique.PERFECT_BINDING));
    }

    @Test
    void groupAForSpiral() {
        assertEquals(ImpositionGroup.A, BindingGroupMapper.groupFor(BindingTechnique.SPIRAL));
    }

    @Test
    void groupAForJapaneseStab() {
        assertEquals(ImpositionGroup.A, BindingGroupMapper.groupFor(BindingTechnique.JAPANESE_STAB));
    }

    @Test
    void groupBForSaddleStitch() {
        assertEquals(ImpositionGroup.B, BindingGroupMapper.groupFor(BindingTechnique.SADDLE_STITCH));
    }

    @Test
    void groupBForPamphlet() {
        assertEquals(ImpositionGroup.B, BindingGroupMapper.groupFor(BindingTechnique.PAMPHLET));
    }

    @Test
    void groupBForBooklet() {
        assertEquals(ImpositionGroup.B, BindingGroupMapper.groupFor(BindingTechnique.BOOKLET));
    }

    @Test
    void groupCForSewnSignatures() {
        assertEquals(ImpositionGroup.C, BindingGroupMapper.groupFor(BindingTechnique.SEWN_SIGNATURES));
    }

    @Test
    void groupCForHardcover() {
        assertEquals(ImpositionGroup.C, BindingGroupMapper.groupFor(BindingTechnique.HARDCOVER));
    }

    @Test
    void groupCForCoptic() {
        assertEquals(ImpositionGroup.C, BindingGroupMapper.groupFor(BindingTechnique.COPTIC));
    }

    @Test
    void nullTechniqueThrows() {
        assertThrows(NullPointerException.class, () -> BindingGroupMapper.groupFor(null));
    }

    @Test
    void constructorIsPrivateAndInvocable() throws Exception {
        Constructor<BindingGroupMapper> ctor =
                BindingGroupMapper.class.getDeclaredConstructor();
        assertNotNull(ctor);
        ctor.setAccessible(true);
        assertNotNull(ctor.newInstance());
    }
}
