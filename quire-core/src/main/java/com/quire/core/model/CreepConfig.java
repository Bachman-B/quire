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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Stores paper physical properties and per-sheet creep calculation results.
 *
 * <p>This is the single authoritative model for creep data across both Phase 1 and Phase 2.
 * Phase 2 fields ({@link #isTransformApplied()}, {@link #isApplyToOutput()}) are present from
 * the start so no model changes are required in Phase 2. In Phase 1 both are always false.
 */
public final class CreepConfig {

    private final Optional<Double> paperWeightGsm;
    private final Optional<Double> paperThicknessMm;
    private final Optional<Double> calculatedCreepMm;
    private final List<CreepSheetResult> sheetResults;
    private final boolean transformApplied;
    private final boolean applyToOutput;

    private CreepConfig(Builder builder) {
        this.paperWeightGsm = builder.paperWeightGsm;
        this.paperThicknessMm = builder.paperThicknessMm;
        this.calculatedCreepMm = builder.calculatedCreepMm;
        this.sheetResults = List.copyOf(builder.sheetResults);
        this.transformApplied = builder.transformApplied;
        this.applyToOutput = builder.applyToOutput;
    }

    /** Returns the paper weight in grams per square metre, if specified. */
    public Optional<Double> getPaperWeightGsm() {
        return paperWeightGsm;
    }

    /**
     * Returns the paper thickness in millimetres, if known directly.
     * If absent, thickness is derived from {@link #getPaperWeightGsm()}.
     */
    public Optional<Double> getPaperThicknessMm() {
        return paperThicknessMm;
    }

    /** Returns the maximum calculated creep across the signature, if computed. */
    public Optional<Double> getCalculatedCreepMm() {
        return calculatedCreepMm;
    }

    /** Returns per-sheet creep results. Empty until {@code CreepCalculator} has run. */
    public List<CreepSheetResult> getSheetResults() {
        return sheetResults;
    }

    /**
     * Returns true if the affine PDF transform has been applied.
     * Always false in Phase 1.
     */
    public boolean isTransformApplied() {
        return transformApplied;
    }

    /**
     * Returns true if the user has requested creep compensation to be applied to the output PDF.
     * Always false in Phase 1.
     */
    public boolean isApplyToOutput() {
        return applyToOutput;
    }

    /**
     * Returns a new builder pre-populated with the values of this instance.
     *
     * @return a builder copying all fields from this instance
     */
    public Builder toBuilder() {
        return new Builder()
                .paperWeightGsm(this.paperWeightGsm.orElse(null))
                .paperThicknessMm(this.paperThicknessMm.orElse(null))
                .calculatedCreepMm(this.calculatedCreepMm.orElse(null))
                .sheetResults(this.sheetResults)
                .transformApplied(this.transformApplied)
                .applyToOutput(this.applyToOutput);
    }

    /** Returns a new empty {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link CreepConfig}. */
    public static final class Builder {

        private Optional<Double> paperWeightGsm = Optional.empty();
        private Optional<Double> paperThicknessMm = Optional.empty();
        private Optional<Double> calculatedCreepMm = Optional.empty();
        private List<CreepSheetResult> sheetResults = List.of();
        private boolean transformApplied = false;
        private boolean applyToOutput = false;

        private Builder() {
        }

        /**
         * Sets the paper weight in gsm, or null to clear.
         *
         * @param paperWeightGsm positive value, or null
         * @return this builder
         */
        public Builder paperWeightGsm(Double paperWeightGsm) {
            if (paperWeightGsm != null && paperWeightGsm <= 0) {
                throw new IllegalArgumentException("paperWeightGsm must be > 0");
            }
            this.paperWeightGsm = Optional.ofNullable(paperWeightGsm);
            return this;
        }

        /**
         * Sets the paper thickness in mm, or null to clear.
         *
         * @param paperThicknessMm positive value, or null
         * @return this builder
         */
        public Builder paperThicknessMm(Double paperThicknessMm) {
            if (paperThicknessMm != null && paperThicknessMm <= 0) {
                throw new IllegalArgumentException("paperThicknessMm must be > 0");
            }
            this.paperThicknessMm = Optional.ofNullable(paperThicknessMm);
            return this;
        }

        /**
         * Sets the maximum calculated creep in mm, or null to clear.
         *
         * @param calculatedCreepMm zero or positive value, or null
         * @return this builder
         */
        public Builder calculatedCreepMm(Double calculatedCreepMm) {
            if (calculatedCreepMm != null && calculatedCreepMm < 0) {
                throw new IllegalArgumentException("calculatedCreepMm must be >= 0");
            }
            this.calculatedCreepMm = Optional.ofNullable(calculatedCreepMm);
            return this;
        }

        /**
         * Sets the per-sheet creep results.
         *
         * @param sheetResults must not be null
         * @return this builder
         */
        public Builder sheetResults(List<CreepSheetResult> sheetResults) {
            this.sheetResults = Objects.requireNonNull(sheetResults, "sheetResults");
            return this;
        }

        /**
         * Sets whether the affine PDF transform has been applied. Always false in Phase 1.
         *
         * @param transformApplied true if applied
         * @return this builder
         */
        public Builder transformApplied(boolean transformApplied) {
            this.transformApplied = transformApplied;
            return this;
        }

        /**
         * Sets whether creep compensation should be applied to the output PDF.
         * Always false in Phase 1.
         *
         * @param applyToOutput true to apply
         * @return this builder
         */
        public Builder applyToOutput(boolean applyToOutput) {
            this.applyToOutput = applyToOutput;
            return this;
        }

        /** Builds the {@link CreepConfig}. */
        public CreepConfig build() {
            return new CreepConfig(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CreepConfig other)) {
            return false;
        }
        return transformApplied == other.transformApplied
                && applyToOutput == other.applyToOutput
                && paperWeightGsm.equals(other.paperWeightGsm)
                && paperThicknessMm.equals(other.paperThicknessMm)
                && calculatedCreepMm.equals(other.calculatedCreepMm)
                && sheetResults.equals(other.sheetResults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paperWeightGsm, paperThicknessMm, calculatedCreepMm,
                sheetResults, transformApplied, applyToOutput);
    }

    @Override
    public String toString() {
        return "CreepConfig{"
                + "paperWeightGsm=" + paperWeightGsm
                + ", paperThicknessMm=" + paperThicknessMm
                + ", calculatedCreepMm=" + calculatedCreepMm
                + ", transformApplied=" + transformApplied
                + ", applyToOutput=" + applyToOutput
                + '}';
    }
}
