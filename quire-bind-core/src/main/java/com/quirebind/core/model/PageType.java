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
 * The role of a page within the full page sequence.
 */
public enum PageType {

    /** A page from the source PDF. */
    CONTENT,

    /** A user-added decorative or endpaper page at the front or rear. */
    AESTHETIC,

    /** An app-calculated blank page inserted to complete the final partial signature (Group C). */
    COMPLETION_BLANK,

    /** An app-calculated blank page inserted to bring the total to a multiple of 4 (Group B only). */
    FILLER_BLANK
}
