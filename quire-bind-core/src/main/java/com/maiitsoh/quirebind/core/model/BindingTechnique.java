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
 * The nine hand-binding techniques supported by Quire.
 *
 * <p>Each technique maps to an {@link ImpositionGroup} which governs page count
 * constraints, padding behaviour, and available imposition layouts.
 *
 * @see BindingTechnique#group()
 */
public enum BindingTechnique {

    /** Perfect binding — flat spine, pages glued. Group A. */
    PERFECT_BINDING,

    /** Spiral or coil binding — pages drilled and threaded with a coil. Group A. */
    SPIRAL,

    /** Japanese stab binding — pages stacked flat and sewn through the spine edge. Group A. */
    JAPANESE_STAB,

    /** Saddle stitch — all sheets nested and stapled or sewn through the fold. Group B. */
    SADDLE_STITCH,

    /** Pamphlet — single folded sheet sewn through the fold. Group B. */
    PAMPHLET,

    /** Booklet — nested sheets folded together. Group B. */
    BOOKLET,

    /** Sewn signatures — multiple independently sewn signatures assembled into a book block. Group C. */
    SEWN_SIGNATURES,

    /** Hardcover / case binding — sewn signatures mounted in a hard case. Group C. */
    HARDCOVER,

    /** Coptic binding — exposed-spine binding sewn through the signatures. Group C. */
    COPTIC;

    /**
     * Returns the {@link ImpositionGroup} that governs imposition for this technique.
     *
     * @return the imposition group, never null
     */
    public ImpositionGroup group() {
        return switch (this) {
            case PERFECT_BINDING, SPIRAL, JAPANESE_STAB -> ImpositionGroup.A;
            case SADDLE_STITCH, PAMPHLET, BOOKLET -> ImpositionGroup.B;
            case SEWN_SIGNATURES, HARDCOVER, COPTIC -> ImpositionGroup.C;
        };
    }
}
