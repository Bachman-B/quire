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

import java.util.Objects;

/**
 * Configuration for sewing hole placement along the spine.
 *
 * <p>Two styles are supported:
 * <ul>
 *   <li><b>SIMPLE</b> — holes evenly distributed between end margins; outermost holes act as
 *       kettle-stitch anchors. Controlled by {@link #getHoleCount()} and
 *       {@link #getEndMarginMm()}.</li>
 *   <li><b>BANDED</b> — kettle stitches at head and tail of spine, with pairs of holes
 *       straddling each band/tape. Total holes = 2 + 2 × bandCount. Controlled by
 *       {@link #getBandCount()}, {@link #getBandWidthMm()}, and {@link #getEndMarginMm()}.</li>
 * </ul>
 */
public final class SewingConfig {

    /** Determines how holes are distributed along the spine. */
    public enum SewingStyle {
        /** Evenly-spaced holes; outermost holes act as kettle stitches. */
        SIMPLE,
        /** Kettle stitches at head and tail; pairs of holes straddling each band/tape. */
        BANDED
    }

    private static final SewingStyle DEFAULT_STYLE = SewingStyle.SIMPLE;
    private static final int DEFAULT_HOLE_COUNT = 5;
    private static final double DEFAULT_END_MARGIN_MM = 15.0;
    private static final int DEFAULT_BAND_COUNT = 3;
    private static final double DEFAULT_BAND_WIDTH_MM = 10.0;

    private final SewingStyle style;
    private final int holeCount;
    private final double endMarginMm;
    private final int bandCount;
    private final double bandWidthMm;

    private SewingConfig(Builder builder) {
        this.style = builder.style;
        this.holeCount = builder.holeCount;
        this.endMarginMm = builder.endMarginMm;
        this.bandCount = builder.bandCount;
        this.bandWidthMm = builder.bandWidthMm;
    }

    /** Returns a {@link SewingConfig} with default values (SIMPLE, 5 holes, 15 mm end margin). */
    public static SewingConfig defaults() {
        return builder().build();
    }

    /** Returns the sewing style. */
    public SewingStyle getStyle() {
        return style;
    }

    /** Returns the total number of sewing holes (SIMPLE mode). */
    public int getHoleCount() {
        return holeCount;
    }

    /** Returns the distance in mm from head/tail of the spine to the outermost hole. */
    public double getEndMarginMm() {
        return endMarginMm;
    }

    /** Returns the number of bands/tapes (BANDED mode). */
    public int getBandCount() {
        return bandCount;
    }

    /** Returns the band/tape width in mm (BANDED mode). */
    public double getBandWidthMm() {
        return bandWidthMm;
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link SewingConfig}. */
    public static final class Builder {

        private SewingStyle style = DEFAULT_STYLE;
        private int holeCount = DEFAULT_HOLE_COUNT;
        private double endMarginMm = DEFAULT_END_MARGIN_MM;
        private int bandCount = DEFAULT_BAND_COUNT;
        private double bandWidthMm = DEFAULT_BAND_WIDTH_MM;

        private Builder() {
        }

        /**
         * Sets the sewing style.
         *
         * @param style must not be null
         * @return this builder
         */
        public Builder style(SewingStyle style) {
            Objects.requireNonNull(style, "style");
            this.style = style;
            return this;
        }

        /**
         * Sets the number of sewing holes (SIMPLE mode).
         *
         * @param holeCount must be at least 2
         * @return this builder
         * @throws IllegalArgumentException if {@code holeCount < 2}
         */
        public Builder holeCount(int holeCount) {
            if (holeCount < 2) {
                throw new IllegalArgumentException("holeCount must be >= 2, got: " + holeCount);
            }
            this.holeCount = holeCount;
            return this;
        }

        /**
         * Sets the end margin in millimetres.
         *
         * @param endMarginMm must be positive
         * @return this builder
         * @throws IllegalArgumentException if {@code endMarginMm <= 0}
         */
        public Builder endMarginMm(double endMarginMm) {
            if (endMarginMm <= 0) {
                throw new IllegalArgumentException(
                        "endMarginMm must be > 0, got: " + endMarginMm);
            }
            this.endMarginMm = endMarginMm;
            return this;
        }

        /**
         * Sets the number of bands/tapes (BANDED mode).
         *
         * @param bandCount must be at least 1
         * @return this builder
         * @throws IllegalArgumentException if {@code bandCount < 1}
         */
        public Builder bandCount(int bandCount) {
            if (bandCount < 1) {
                throw new IllegalArgumentException("bandCount must be >= 1, got: " + bandCount);
            }
            this.bandCount = bandCount;
            return this;
        }

        /**
         * Sets the band/tape width in millimetres (BANDED mode).
         *
         * @param bandWidthMm must be positive
         * @return this builder
         * @throws IllegalArgumentException if {@code bandWidthMm <= 0}
         */
        public Builder bandWidthMm(double bandWidthMm) {
            if (bandWidthMm <= 0) {
                throw new IllegalArgumentException(
                        "bandWidthMm must be > 0, got: " + bandWidthMm);
            }
            this.bandWidthMm = bandWidthMm;
            return this;
        }

        /** Builds the {@link SewingConfig}. */
        public SewingConfig build() {
            return new SewingConfig(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SewingConfig other)) {
            return false;
        }
        return style == other.style
                && holeCount == other.holeCount
                && Double.compare(endMarginMm, other.endMarginMm) == 0
                && bandCount == other.bandCount
                && Double.compare(bandWidthMm, other.bandWidthMm) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(style, holeCount, endMarginMm, bandCount, bandWidthMm);
    }

    @Override
    public String toString() {
        return "SewingConfig{"
                + "style=" + style
                + ", holeCount=" + holeCount
                + ", endMarginMm=" + endMarginMm
                + ", bandCount=" + bandCount
                + ", bandWidthMm=" + bandWidthMm
                + '}';
    }
}
