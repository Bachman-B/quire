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

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * The outcome of a document format conversion.
 *
 * <p><strong>Phase 2 placeholder.</strong> In Phase 1, this class exists only to keep the
 * model shape stable. The conversion service (Phase 2) will produce and consume it.
 */
public final class ConversionResult {

    private final boolean success;
    private final Path outputPath;
    private final List<ConversionWarning> warnings;

    private ConversionResult(Builder builder) {
        this.success = builder.success;
        this.outputPath = builder.outputPath;
        this.warnings = List.copyOf(builder.warnings);
    }

    /** Returns true if the conversion succeeded. */
    public boolean isSuccess() {
        return success;
    }

    /** Returns the path of the converted PDF, or null if conversion failed. */
    public Path getOutputPath() {
        return outputPath;
    }

    /** Returns any warnings produced during conversion. */
    public List<ConversionWarning> getWarnings() {
        return warnings;
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link ConversionResult}. */
    public static final class Builder {

        private boolean success;
        private Path outputPath;
        private List<ConversionWarning> warnings = List.of();

        private Builder() {
        }

        /**
         * Sets whether the conversion succeeded.
         *
         * @param success true if successful
         * @return this builder
         */
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        /**
         * Sets the output path of the converted PDF, or null if conversion failed.
         *
         * @param outputPath the path, or null
         * @return this builder
         */
        public Builder outputPath(Path outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        /**
         * Sets the conversion warnings.
         *
         * @param warnings must not be null
         * @return this builder
         */
        public Builder warnings(List<ConversionWarning> warnings) {
            this.warnings = Objects.requireNonNull(warnings, "warnings");
            return this;
        }

        /** Builds the {@link ConversionResult}. */
        public ConversionResult build() {
            return new ConversionResult(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConversionResult other)) {
            return false;
        }
        return success == other.success
                && Objects.equals(outputPath, other.outputPath)
                && warnings.equals(other.warnings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, outputPath, warnings);
    }

    @Override
    public String toString() {
        return "ConversionResult{"
                + "success=" + success
                + ", warningCount=" + warnings.size()
                + '}';
    }
}
