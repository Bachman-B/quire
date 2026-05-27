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

/**
 * The imposition layout — how many pages are placed on each side of a printed sheet.
 *
 * <p>Phase 1 implements {@link #FOLIO} only. {@link #QUARTO} and {@link #OCTAVO} are
 * present in this enum from day one so the model is stable, but the imposition engine
 * throws {@link UnsupportedOperationException} if either is requested in Phase 1.
 */
public enum ImpositionLayout {

    /** Folio: 2 pages per side, 1 fold. 4 pages per sheet. Implemented in Phase 1. */
    FOLIO,

    /**
     * Quarto: 4 pages per side, 2 folds. 8 pages per sheet.
     *
     * <p><strong>Phase 2.</strong> Requesting this in Phase 1 throws
     * {@link UnsupportedOperationException}.
     */
    QUARTO,

    /**
     * Octavo: 8 pages per side, 3 folds. 16 pages per sheet.
     *
     * <p><strong>Phase 2.</strong> Requesting this in Phase 1 throws
     * {@link UnsupportedOperationException}.
     */
    OCTAVO
}
