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
 * The three structural groups that govern imposition, padding, and mark behaviour.
 *
 * <ul>
 *   <li>{@link #A} — single sheet, flat imposition: no folding, no signature structure</li>
 *   <li>{@link #B} — folded sheet, single signature: all sheets fold into one signature</li>
 *   <li>{@link #C} — folded sheet, multiple signatures: pages divided into fixed-size signatures</li>
 * </ul>
 */
public enum ImpositionGroup {

    /** Single sheet, flat imposition. No folding, no signature structure. */
    A,

    /** Folded sheet, single signature. All sheets fold together into one signature. */
    B,

    /** Folded sheet, multiple signatures. Pages divided into fixed-size independent signatures. */
    C
}
