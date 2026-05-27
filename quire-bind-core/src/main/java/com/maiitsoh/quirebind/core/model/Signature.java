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

import java.util.List;
import java.util.Objects;

/**
 * An output concept representing one folded signature ready for binding.
 *
 * <p>Computed by the imposition engine. A signature contains one or more
 * {@link ImposedSheet} instances stacked and folded together.
 */
public final class Signature {

    private final int signatureIndex;
    private final List<ImposedSheet> sheets;
    private final List<Integer> logicalPageNumbers;

    private Signature(Builder builder) {
        this.signatureIndex = builder.signatureIndex;
        this.sheets = List.copyOf(builder.sheets);
        this.logicalPageNumbers = List.copyOf(builder.logicalPageNumbers);
    }

    /** Returns the 0-based index of this signature in the complete book. */
    public int getSignatureIndex() {
        return signatureIndex;
    }

    /** Returns the sheets that make up this signature (outermost first). */
    public List<ImposedSheet> getSheets() {
        return sheets;
    }

    /** Returns the logical page numbers assigned to this signature's pages. */
    public List<Integer> getLogicalPageNumbers() {
        return logicalPageNumbers;
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link Signature}. */
    public static final class Builder {

        private int signatureIndex;
        private List<ImposedSheet> sheets = List.of();
        private List<Integer> logicalPageNumbers = List.of();

        private Builder() {
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
         * Sets the imposed sheets (outermost first).
         *
         * @param sheets must not be null
         * @return this builder
         */
        public Builder sheets(List<ImposedSheet> sheets) {
            this.sheets = Objects.requireNonNull(sheets, "sheets");
            return this;
        }

        /**
         * Sets the logical page numbers for this signature.
         *
         * @param logicalPageNumbers must not be null
         * @return this builder
         */
        public Builder logicalPageNumbers(List<Integer> logicalPageNumbers) {
            this.logicalPageNumbers = Objects.requireNonNull(logicalPageNumbers, "logicalPageNumbers");
            return this;
        }

        /** Builds the {@link Signature}. */
        public Signature build() {
            return new Signature(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Signature other)) {
            return false;
        }
        return signatureIndex == other.signatureIndex
                && sheets.equals(other.sheets)
                && logicalPageNumbers.equals(other.logicalPageNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signatureIndex, sheets, logicalPageNumbers);
    }

    @Override
    public String toString() {
        return "Signature{"
                + "signatureIndex=" + signatureIndex
                + ", sheetCount=" + sheets.size()
                + '}';
    }
}
