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
import java.util.Objects;

/**
 * Carries the parameters for a document format conversion.
 *
 * <p><strong>Phase 2 placeholder.</strong> In Phase 1, this class exists only to keep the
 * model shape stable. The conversion service (Phase 2) will populate and consume it.
 */
public final class ConversionRequest {

    private final InputFormat inputFormat;
    private final Path sourcePath;
    private final Path outputPath;

    private ConversionRequest(Builder builder) {
        this.inputFormat = builder.inputFormat;
        this.sourcePath = builder.sourcePath;
        this.outputPath = builder.outputPath;
    }

    /** Returns the format of the source document. */
    public InputFormat getInputFormat() {
        return inputFormat;
    }

    /** Returns the path to the source document. */
    public Path getSourcePath() {
        return sourcePath;
    }

    /** Returns the target output path for the converted PDF. */
    public Path getOutputPath() {
        return outputPath;
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link ConversionRequest}. */
    public static final class Builder {

        private InputFormat inputFormat;
        private Path sourcePath;
        private Path outputPath;

        private Builder() {
        }

        /**
         * Sets the input format.
         *
         * @param inputFormat must not be null
         * @return this builder
         */
        public Builder inputFormat(InputFormat inputFormat) {
            this.inputFormat = Objects.requireNonNull(inputFormat, "inputFormat");
            return this;
        }

        /**
         * Sets the source document path.
         *
         * @param sourcePath must not be null
         * @return this builder
         */
        public Builder sourcePath(Path sourcePath) {
            this.sourcePath = Objects.requireNonNull(sourcePath, "sourcePath");
            return this;
        }

        /**
         * Sets the output PDF path.
         *
         * @param outputPath must not be null
         * @return this builder
         */
        public Builder outputPath(Path outputPath) {
            this.outputPath = Objects.requireNonNull(outputPath, "outputPath");
            return this;
        }

        /**
         * Builds the {@link ConversionRequest}.
         *
         * @return a new immutable instance
         * @throws NullPointerException if any required field is not set
         */
        public ConversionRequest build() {
            Objects.requireNonNull(inputFormat, "inputFormat must be set");
            Objects.requireNonNull(sourcePath, "sourcePath must be set");
            Objects.requireNonNull(outputPath, "outputPath must be set");
            return new ConversionRequest(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConversionRequest other)) {
            return false;
        }
        return inputFormat == other.inputFormat
                && sourcePath.equals(other.sourcePath)
                && outputPath.equals(other.outputPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputFormat, sourcePath, outputPath);
    }

    @Override
    public String toString() {
        return "ConversionRequest{"
                + "inputFormat=" + inputFormat
                + ", sourcePath=" + sourcePath
                + '}';
    }
}
