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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Creep calculation result for one sheet position within a signature.
 *
 * <p>Computed by {@code CreepCalculator}. One instance per sheet position, where
 * sheet index 0 is the outermost sheet (zero creep) and the highest index is the
 * innermost sheet (maximum creep).
 */
public final class CreepSheetResult {

    private final int sheetIndex;
    private final double creepMm;
    private final List<Integer> pageNumbers;
    private final Optional<Double> availableSpineMarginMm;
    private final boolean marginWarning;
    private final double marginDeficitMm;

    private CreepSheetResult(Builder builder) {
        this.sheetIndex = builder.sheetIndex;
        this.creepMm = builder.creepMm;
        this.pageNumbers = List.copyOf(builder.pageNumbers);
        this.availableSpineMarginMm = builder.availableSpineMarginMm;
        this.marginWarning = builder.marginWarning;
        this.marginDeficitMm = builder.marginDeficitMm;
    }

    /** Returns the 0-based sheet index within the signature (0 = outermost). */
    public int getSheetIndex() {
        return sheetIndex;
    }

    /** Returns the outward creep shift for this sheet position in millimetres. */
    public double getCreepMm() {
        return creepMm;
    }

    /** Returns the logical page numbers printed on both sides of this sheet. */
    public List<Integer> getPageNumbers() {
        return pageNumbers;
    }

    /**
     * Returns the available spine-side whitespace detected from the PDF content bounds,
     * or empty if not yet analysed.
     */
    public Optional<Double> getAvailableSpineMarginMm() {
        return availableSpineMarginMm;
    }

    /** Returns true if {@link #getCreepMm()} exceeds {@link #getAvailableSpineMarginMm()}. */
    public boolean isMarginWarning() {
        return marginWarning;
    }

    /**
     * Returns the margin deficit in millimetres: {@code creepMm - availableSpineMarginMm}.
     * Returns 0 if there is no warning.
     */
    public double getMarginDeficitMm() {
        return marginDeficitMm;
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link CreepSheetResult}. */
    public static final class Builder {

        private int sheetIndex;
        private double creepMm;
        private List<Integer> pageNumbers = List.of();
        private Optional<Double> availableSpineMarginMm = Optional.empty();
        private boolean marginWarning;
        private double marginDeficitMm;

        private Builder() {
        }

        /**
         * Sets the 0-based sheet index.
         *
         * @param sheetIndex must be zero or positive
         * @return this builder
         */
        public Builder sheetIndex(int sheetIndex) {
            if (sheetIndex < 0) {
                throw new IllegalArgumentException("sheetIndex must be >= 0");
            }
            this.sheetIndex = sheetIndex;
            return this;
        }

        /**
         * Sets the creep shift in millimetres for this sheet.
         *
         * @param creepMm must be zero or positive
         * @return this builder
         */
        public Builder creepMm(double creepMm) {
            if (creepMm < 0) {
                throw new IllegalArgumentException("creepMm must be >= 0");
            }
            this.creepMm = creepMm;
            return this;
        }

        /**
         * Sets the logical page numbers on this sheet.
         *
         * @param pageNumbers must not be null
         * @return this builder
         */
        public Builder pageNumbers(List<Integer> pageNumbers) {
            this.pageNumbers = Objects.requireNonNull(pageNumbers, "pageNumbers");
            return this;
        }

        /**
         * Sets the available spine margin detected from the PDF, or null for not analysed.
         *
         * @param availableSpineMarginMm in millimetres, or null if not yet measured
         * @return this builder
         */
        public Builder availableSpineMarginMm(Double availableSpineMarginMm) {
            this.availableSpineMarginMm = Optional.ofNullable(availableSpineMarginMm);
            return this;
        }

        /**
         * Sets whether a spine margin warning applies to this sheet.
         *
         * @param marginWarning true if creep exceeds available margin
         * @return this builder
         */
        public Builder marginWarning(boolean marginWarning) {
            this.marginWarning = marginWarning;
            return this;
        }

        /**
         * Sets the margin deficit in millimetres (0 if no warning).
         *
         * @param marginDeficitMm must be zero or positive
         * @return this builder
         */
        public Builder marginDeficitMm(double marginDeficitMm) {
            if (marginDeficitMm < 0) {
                throw new IllegalArgumentException("marginDeficitMm must be >= 0");
            }
            this.marginDeficitMm = marginDeficitMm;
            return this;
        }

        /** Builds the {@link CreepSheetResult}. */
        public CreepSheetResult build() {
            return new CreepSheetResult(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CreepSheetResult other)) {
            return false;
        }
        return sheetIndex == other.sheetIndex
                && Double.compare(creepMm, other.creepMm) == 0
                && marginWarning == other.marginWarning
                && Double.compare(marginDeficitMm, other.marginDeficitMm) == 0
                && pageNumbers.equals(other.pageNumbers)
                && availableSpineMarginMm.equals(other.availableSpineMarginMm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetIndex, creepMm, pageNumbers,
                availableSpineMarginMm, marginWarning, marginDeficitMm);
    }

    @Override
    public String toString() {
        return "CreepSheetResult{"
                + "sheetIndex=" + sheetIndex
                + ", creepMm=" + creepMm
                + ", marginWarning=" + marginWarning
                + ", marginDeficitMm=" + marginDeficitMm
                + '}';
    }
}
