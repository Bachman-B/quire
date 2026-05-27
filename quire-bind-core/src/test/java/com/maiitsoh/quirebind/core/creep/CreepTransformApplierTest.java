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
package com.maiitsoh.quirebind.core.creep;

import com.maiitsoh.quirebind.core.model.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class CreepTransformApplierTest {

    private QuireProject minimalProject() {
        return QuireProject.builder()
                .name("Test")
                .bindingTechnique(BindingTechnique.SADDLE_STITCH)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(new PageSequence())
                .paddingConfig(PaddingConfig.builder().build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().build())
                .build();
    }

    @Test
    void applyTransformThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class,
                () -> CreepTransformApplier.applyTransform(minimalProject()));
    }

    @Test
    void constructorIsPrivateAndInvocable() throws Exception {
        Constructor<CreepTransformApplier> ctor =
                CreepTransformApplier.class.getDeclaredConstructor();
        assertNotNull(ctor);
        ctor.setAccessible(true);
        assertNotNull(ctor.newInstance());
    }
}
