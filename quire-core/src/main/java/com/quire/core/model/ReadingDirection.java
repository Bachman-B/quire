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
 * The reading direction of the book being produced.
 *
 * <p>This is a first-class global parameter that propagates to imposition page order,
 * fold orientation, spine mark placement, folio position, and guide content.
 *
 * <p>Note: reading direction (the binding direction of the book) is independent of
 * UI language. A user may work in an English UI while producing an RTL-bound book.
 */
public enum ReadingDirection {

    /** Left-to-right — spine on the left, pages open to the right. */
    LTR,

    /** Right-to-left — spine on the right, pages open to the left. */
    RTL
}
