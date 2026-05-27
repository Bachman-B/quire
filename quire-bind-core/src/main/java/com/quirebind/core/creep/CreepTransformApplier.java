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
package com.quirebind.core.creep;

import com.quirebind.core.model.CreepConfig;
import com.quirebind.core.model.QuireProject;

/**
 * Applies an affine PDF transform to shift page content and compensate for creep.
 *
 * <p><strong>Phase 2 stub.</strong> In Phase 1, this class exists only to keep the
 * package structure stable. All methods throw {@link UnsupportedOperationException}.
 */
public final class CreepTransformApplier {

    private CreepTransformApplier() {
    }

    /**
     * Applies the creep compensation transform to the output PDF.
     *
     * @param project the project whose creep data drives the transform
     * @return an updated {@link CreepConfig} with {@code transformApplied} set to true
     * @throws UnsupportedOperationException always; available in Phase 2 only
     */
    public static CreepConfig applyTransform(QuireProject project) {
        throw new UnsupportedOperationException(
                "CreepTransformApplier is a Phase 2 feature and is not available in Phase 1");
    }
}
