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

import java.util.Objects;

/**
 * Controls the aesthetic and completion pages added around the document content.
 *
 * <p>Invariant: {@code completionFront + completionRear} must equal the value returned by
 * {@link PageSequence#completionPagesRequired(int)} for the configured signature size.
 * This invariant is enforced at the pipeline level, not in this class.
 */
public final class PaddingConfig {

    private final int aestheticFront;
    private final int aestheticRear;
    private final int completionFront;
    private final int completionRear;
    private final int signatureSize;

    private PaddingConfig(Builder builder) {
        this.aestheticFront = builder.aestheticFront;
        this.aestheticRear = builder.aestheticRear;
        this.completionFront = builder.completionFront;
        this.completionRear = builder.completionRear;
        this.signatureSize = builder.signatureSize;
    }

    /** Returns the number of aesthetic pages at the front of the document. */
    public int getAestheticFront() {
        return aestheticFront;
    }

    /** Returns the number of aesthetic pages at the rear of the document. */
    public int getAestheticRear() {
        return aestheticRear;
    }

    /** Returns the number of completion blank pages placed at the front (Group C only). */
    public int getCompletionFront() {
        return completionFront;
    }

    /** Returns the number of completion blank pages placed at the rear (Group C only). */
    public int getCompletionRear() {
        return completionRear;
    }

    /**
     * Returns the number of pages per signature (Groups B and C only).
     * Returns 0 for Group A where no signature structure exists.
     */
    public int getSignatureSize() {
        return signatureSize;
    }

    /**
     * Returns a new builder pre-populated with the values of this instance.
     *
     * @return a builder copying all fields from this instance
     */
    public Builder toBuilder() {
        return new Builder()
                .aestheticFront(this.aestheticFront)
                .aestheticRear(this.aestheticRear)
                .completionFront(this.completionFront)
                .completionRear(this.completionRear)
                .signatureSize(this.signatureSize);
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link PaddingConfig}. */
    public static final class Builder {

        private int aestheticFront = 0;
        private int aestheticRear = 0;
        private int completionFront = 0;
        private int completionRear = 0;
        private int signatureSize = 0;

        private Builder() {
        }

        /**
         * Sets the aesthetic page count at the front.
         *
         * @param aestheticFront must be zero or positive
         * @return this builder
         * @throws IllegalArgumentException if negative
         */
        public Builder aestheticFront(int aestheticFront) {
            if (aestheticFront < 0) {
                throw new IllegalArgumentException("aestheticFront must be >= 0");
            }
            this.aestheticFront = aestheticFront;
            return this;
        }

        /**
         * Sets the aesthetic page count at the rear.
         *
         * @param aestheticRear must be zero or positive
         * @return this builder
         * @throws IllegalArgumentException if negative
         */
        public Builder aestheticRear(int aestheticRear) {
            if (aestheticRear < 0) {
                throw new IllegalArgumentException("aestheticRear must be >= 0");
            }
            this.aestheticRear = aestheticRear;
            return this;
        }

        /**
         * Sets the completion page count at the front (Group C only).
         *
         * @param completionFront must be zero or positive
         * @return this builder
         * @throws IllegalArgumentException if negative
         */
        public Builder completionFront(int completionFront) {
            if (completionFront < 0) {
                throw new IllegalArgumentException("completionFront must be >= 0");
            }
            this.completionFront = completionFront;
            return this;
        }

        /**
         * Sets the completion page count at the rear (Group C only).
         *
         * @param completionRear must be zero or positive
         * @return this builder
         * @throws IllegalArgumentException if negative
         */
        public Builder completionRear(int completionRear) {
            if (completionRear < 0) {
                throw new IllegalArgumentException("completionRear must be >= 0");
            }
            this.completionRear = completionRear;
            return this;
        }

        /**
         * Sets the pages-per-signature value (Groups B and C only; use 0 for Group A).
         *
         * @param signatureSize must be zero or positive
         * @return this builder
         * @throws IllegalArgumentException if negative
         */
        public Builder signatureSize(int signatureSize) {
            if (signatureSize < 0) {
                throw new IllegalArgumentException("signatureSize must be >= 0");
            }
            this.signatureSize = signatureSize;
            return this;
        }

        /** Builds the {@link PaddingConfig}. */
        public PaddingConfig build() {
            return new PaddingConfig(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PaddingConfig other)) {
            return false;
        }
        return aestheticFront == other.aestheticFront
                && aestheticRear == other.aestheticRear
                && completionFront == other.completionFront
                && completionRear == other.completionRear
                && signatureSize == other.signatureSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(aestheticFront, aestheticRear, completionFront, completionRear, signatureSize);
    }

    @Override
    public String toString() {
        return "PaddingConfig{"
                + "aestheticFront=" + aestheticFront
                + ", aestheticRear=" + aestheticRear
                + ", completionFront=" + completionFront
                + ", completionRear=" + completionRear
                + ", signatureSize=" + signatureSize
                + '}';
    }
}
