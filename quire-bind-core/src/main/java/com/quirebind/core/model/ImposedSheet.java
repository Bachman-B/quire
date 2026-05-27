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
 * An output concept representing one physical sheet of paper in an imposed signature.
 *
 * <p>Each sheet has pages on its front and back sides. The sheet's position within
 * its signature determines its creep shift.
 */
public final class ImposedSheet {

    private final int sheetIndex;
    private final int signatureIndex;
    private final List<QuirePage> frontPages;
    private final List<QuirePage> backPages;
    private final Optional<CreepSheetResult> creepResult;

    private ImposedSheet(Builder builder) {
        this.sheetIndex = builder.sheetIndex;
        this.signatureIndex = builder.signatureIndex;
        this.frontPages = List.copyOf(builder.frontPages);
        this.backPages = List.copyOf(builder.backPages);
        this.creepResult = builder.creepResult;
    }

    /** Returns the 0-based sheet index within its signature (0 = outermost). */
    public int getSheetIndex() {
        return sheetIndex;
    }

    /** Returns the 0-based index of the signature this sheet belongs to. */
    public int getSignatureIndex() {
        return signatureIndex;
    }

    /** Returns the pages printed on the front side of this sheet. */
    public List<QuirePage> getFrontPages() {
        return frontPages;
    }

    /** Returns the pages printed on the back side of this sheet. */
    public List<QuirePage> getBackPages() {
        return backPages;
    }

    /**
     * Returns the creep calculation result for this sheet position,
     * or empty if creep has not been calculated.
     */
    public Optional<CreepSheetResult> getCreepResult() {
        return creepResult;
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link ImposedSheet}. */
    public static final class Builder {

        private int sheetIndex;
        private int signatureIndex;
        private List<QuirePage> frontPages = List.of();
        private List<QuirePage> backPages = List.of();
        private Optional<CreepSheetResult> creepResult = Optional.empty();

        private Builder() {
        }

        /**
         * Sets the 0-based sheet index within the signature.
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
         * Sets the 0-based signature index.
         *
         * @param signatureIndex must be zero or positive
         * @return this builder
         */
        public Builder signatureIndex(int signatureIndex) {
            if (signatureIndex < 0) {
                throw new IllegalArgumentException("signatureIndex must be >= 0");
            }
            this.signatureIndex = signatureIndex;
            return this;
        }

        /**
         * Sets the front-side pages.
         *
         * @param frontPages must not be null
         * @return this builder
         */
        public Builder frontPages(List<QuirePage> frontPages) {
            this.frontPages = Objects.requireNonNull(frontPages, "frontPages");
            return this;
        }

        /**
         * Sets the back-side pages.
         *
         * @param backPages must not be null
         * @return this builder
         */
        public Builder backPages(List<QuirePage> backPages) {
            this.backPages = Objects.requireNonNull(backPages, "backPages");
            return this;
        }

        /**
         * Sets the creep result for this sheet, or null if not yet calculated.
         *
         * @param creepResult the result, or null
         * @return this builder
         */
        public Builder creepResult(CreepSheetResult creepResult) {
            this.creepResult = Optional.ofNullable(creepResult);
            return this;
        }

        /** Builds the {@link ImposedSheet}. */
        public ImposedSheet build() {
            return new ImposedSheet(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ImposedSheet other)) {
            return false;
        }
        return sheetIndex == other.sheetIndex
                && signatureIndex == other.signatureIndex
                && frontPages.equals(other.frontPages)
                && backPages.equals(other.backPages)
                && creepResult.equals(other.creepResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetIndex, signatureIndex, frontPages, backPages, creepResult);
    }

    @Override
    public String toString() {
        return "ImposedSheet{"
                + "sheetIndex=" + sheetIndex
                + ", signatureIndex=" + signatureIndex
                + '}';
    }
}
