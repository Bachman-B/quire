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

/**
 * The horizontal position of the printed folio on the page.
 *
 * <p>Position is reading-direction aware: INNER_MARGIN is the spine side
 * and OUTER_MARGIN is the fore-edge side, regardless of LTR or RTL.
 */
public enum FolioPosition {

    /**
     * Inner margin — spine side of the page.
     * Left on recto pages for LTR; right on recto pages for RTL.
     */
    INNER_MARGIN,

    /**
     * Outer margin — fore-edge side of the page. The most common typographic convention.
     * Right on recto pages for LTR; left on recto pages for RTL.
     */
    OUTER_MARGIN
}
