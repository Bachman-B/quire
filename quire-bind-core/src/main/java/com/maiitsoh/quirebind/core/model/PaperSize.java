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

/**
 * Standard paper sizes and a custom size option.
 *
 * <p>When {@link #CUSTOM} is selected, the project must supply explicit width and height
 * values in millimetres via {@link QuireProject#getCustomPageWidthMm()} and
 * {@link QuireProject#getCustomPageHeightMm()}.
 */
public enum PaperSize {

    /** ISO A4: 210 × 297 mm. */
    A4,

    /** ISO A5: 148 × 210 mm. */
    A5,

    /** ISO A3: 297 × 420 mm. */
    A3,

    /** US Letter: 215.9 × 279.4 mm. */
    LETTER,

    /** US Legal: 215.9 × 355.6 mm. */
    LEGAL,

    /** US Half-Letter: 139.7 × 215.9 mm. */
    HALF_LETTER,

    /**
     * Custom dimensions.
     * Requires {@link QuireProject#getCustomPageWidthMm()} and
     * {@link QuireProject#getCustomPageHeightMm()} to be present.
     */
    CUSTOM
}
