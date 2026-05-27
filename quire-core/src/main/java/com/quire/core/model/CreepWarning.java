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

/**
 * A spine margin warning produced when creep exceeds the available spine margin
 * on one or more pages.
 *
 * <p>Produced by {@code CreepMarginAnalyser} when
 * {@code creepMm > availableMarginMm} for any sheet position.
 */
public final class CreepWarning {

    private final WarningSeverity severity;
    private final int sheetIndex;
    private final List<Integer> affectedPages;
    private final double creepMm;
    private final double availableMarginMm;
    private final double deficitMm;

    private CreepWarning(Builder builder) {
        this.severity = builder.severity;
        this.sheetIndex = builder.sheetIndex;
        this.affectedPages = List.copyOf(builder.affectedPages);
        this.creepMm = builder.creepMm;
        this.availableMarginMm = builder.availableMarginMm;
        this.deficitMm = builder.deficitMm;
    }

    /** Returns the severity of this warning. */
    public WarningSeverity getSeverity() {
        return severity;
    }

    /** Returns the 0-based sheet index within the signature that triggered the warning. */
    public int getSheetIndex() {
        return sheetIndex;
    }

    /** Returns the logical page numbers affected by this creep warning. */
    public List<Integer> getAffectedPages() {
        return affectedPages;
    }

    /** Returns the calculated creep shift for this sheet in millimetres. */
    public double getCreepMm() {
        return creepMm;
    }

    /** Returns the available spine margin detected from the PDF in millimetres. */
    public double getAvailableMarginMm() {
        return availableMarginMm;
    }

    /** Returns the deficit: {@code creepMm - availableMarginMm} in millimetres. */
    public double getDeficitMm() {
        return deficitMm;
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link CreepWarning}. */
    public static final class Builder {

        private WarningSeverity severity;
        private int sheetIndex;
        private List<Integer> affectedPages = List.of();
        private double creepMm;
        private double availableMarginMm;
        private double deficitMm;

        private Builder() {
        }

        /**
         * Sets the warning severity.
         *
         * @param severity must not be null
         * @return this builder
         */
        public Builder severity(WarningSeverity severity) {
            this.severity = Objects.requireNonNull(severity, "severity");
            return this;
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
         * Sets the logical page numbers affected by this warning.
         *
         * @param affectedPages must not be null
         * @return this builder
         */
        public Builder affectedPages(List<Integer> affectedPages) {
            this.affectedPages = Objects.requireNonNull(affectedPages, "affectedPages");
            return this;
        }

        /**
         * Sets the creep shift in millimetres.
         *
         * @param creepMm must be positive
         * @return this builder
         */
        public Builder creepMm(double creepMm) {
            if (creepMm <= 0) {
                throw new IllegalArgumentException("creepMm must be > 0 for a warning to exist");
            }
            this.creepMm = creepMm;
            return this;
        }

        /**
         * Sets the available spine margin in millimetres.
         *
         * @param availableMarginMm must be zero or positive
         * @return this builder
         */
        public Builder availableMarginMm(double availableMarginMm) {
            if (availableMarginMm < 0) {
                throw new IllegalArgumentException("availableMarginMm must be >= 0");
            }
            this.availableMarginMm = availableMarginMm;
            return this;
        }

        /**
         * Sets the deficit in millimetres.
         *
         * @param deficitMm must be positive (a warning only exists when there is a deficit)
         * @return this builder
         */
        public Builder deficitMm(double deficitMm) {
            if (deficitMm <= 0) {
                throw new IllegalArgumentException("deficitMm must be > 0");
            }
            this.deficitMm = deficitMm;
            return this;
        }

        /**
         * Builds the {@link CreepWarning}.
         *
         * @return a new immutable instance
         * @throws NullPointerException if severity is not set
         */
        public CreepWarning build() {
            Objects.requireNonNull(severity, "severity must be set");
            return new CreepWarning(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CreepWarning other)) {
            return false;
        }
        return sheetIndex == other.sheetIndex
                && Double.compare(creepMm, other.creepMm) == 0
                && Double.compare(availableMarginMm, other.availableMarginMm) == 0
                && Double.compare(deficitMm, other.deficitMm) == 0
                && severity == other.severity
                && affectedPages.equals(other.affectedPages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(severity, sheetIndex, affectedPages, creepMm, availableMarginMm, deficitMm);
    }

    @Override
    public String toString() {
        return "CreepWarning{"
                + "severity=" + severity
                + ", sheetIndex=" + sheetIndex
                + ", creepMm=" + creepMm
                + ", deficitMm=" + deficitMm
                + '}';
    }
}
