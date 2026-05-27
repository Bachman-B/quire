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

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Top-level domain object representing a Quire imposition project.
 *
 * <p>The {@link ImpositionGroup} is derived from the chosen {@link BindingTechnique} and cannot
 * be set independently. All configuration objects are required at build time. The
 * {@link PageSequence} is mutable and shared by reference; equality uses identity comparison
 * for that field.
 */
public final class QuireProject {

    private final String name;
    private final BindingTechnique bindingTechnique;
    private final ImpositionGroup impositionGroup;
    private final PaperSize paperSize;
    private final ReadingDirection readingDirection;
    private final ImpositionLayout layout;
    private final PageSequence pageSequence;
    private final PaddingConfig paddingConfig;
    private final NumberingConfig numberingConfig;
    private final MarkConfig markConfig;
    private final CreepConfig creepConfig;
    private final Optional<Path> outputPath;

    private QuireProject(Builder builder) {
        this.name = builder.name;
        this.bindingTechnique = builder.bindingTechnique;
        this.impositionGroup = builder.bindingTechnique.group();
        this.paperSize = builder.paperSize;
        this.readingDirection = builder.readingDirection;
        this.layout = builder.layout;
        this.pageSequence = builder.pageSequence;
        this.paddingConfig = builder.paddingConfig;
        this.numberingConfig = builder.numberingConfig;
        this.markConfig = builder.markConfig;
        this.creepConfig = builder.creepConfig;
        this.outputPath = builder.outputPath;
    }

    /** Returns the project name. */
    public String getName() {
        return name;
    }

    /** Returns the binding technique. */
    public BindingTechnique getBindingTechnique() {
        return bindingTechnique;
    }

    /** Returns the imposition group derived from the binding technique. */
    public ImpositionGroup getImpositionGroup() {
        return impositionGroup;
    }

    /** Returns the target paper size. */
    public PaperSize getPaperSize() {
        return paperSize;
    }

    /** Returns the reading direction. */
    public ReadingDirection getReadingDirection() {
        return readingDirection;
    }

    /** Returns the imposition layout (always {@link ImpositionLayout#FOLIO} in Phase 1). */
    public ImpositionLayout getLayout() {
        return layout;
    }

    /** Returns the logical page sequence for this project. */
    public PageSequence getPageSequence() {
        return pageSequence;
    }

    /** Returns the padding configuration. */
    public PaddingConfig getPaddingConfig() {
        return paddingConfig;
    }

    /** Returns the folio numbering configuration. */
    public NumberingConfig getNumberingConfig() {
        return numberingConfig;
    }

    /** Returns the print mark configuration. */
    public MarkConfig getMarkConfig() {
        return markConfig;
    }

    /** Returns the creep configuration. */
    public CreepConfig getCreepConfig() {
        return creepConfig;
    }

    /** Returns the output PDF path, if configured. */
    public Optional<Path> getOutputPath() {
        return outputPath;
    }

    /**
     * Returns a new builder pre-populated with the values of this instance.
     * The {@link PageSequence} is shared, not deep-copied.
     *
     * @return a builder copying all fields
     */
    public Builder toBuilder() {
        return new Builder()
                .name(this.name)
                .bindingTechnique(this.bindingTechnique)
                .paperSize(this.paperSize)
                .readingDirection(this.readingDirection)
                .layout(this.layout)
                .pageSequence(this.pageSequence)
                .paddingConfig(this.paddingConfig)
                .numberingConfig(this.numberingConfig)
                .markConfig(this.markConfig)
                .creepConfig(this.creepConfig)
                .outputPath(this.outputPath.orElse(null));
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link QuireProject}. */
    public static final class Builder {

        private String name;
        private BindingTechnique bindingTechnique;
        private PaperSize paperSize;
        private ReadingDirection readingDirection = ReadingDirection.LTR;
        private ImpositionLayout layout = ImpositionLayout.FOLIO;
        private PageSequence pageSequence;
        private PaddingConfig paddingConfig;
        private NumberingConfig numberingConfig;
        private MarkConfig markConfig;
        private CreepConfig creepConfig;
        private Optional<Path> outputPath = Optional.empty();

        private Builder() {
        }

        /**
         * Sets the project name.
         *
         * @param name must not be null or blank
         * @return this builder
         */
        public Builder name(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name must not be null or blank");
            }
            this.name = name;
            return this;
        }

        /**
         * Sets the binding technique. The imposition group is derived from this value.
         *
         * @param bindingTechnique must not be null
         * @return this builder
         */
        public Builder bindingTechnique(BindingTechnique bindingTechnique) {
            this.bindingTechnique = Objects.requireNonNull(bindingTechnique, "bindingTechnique");
            return this;
        }

        /**
         * Sets the paper size.
         *
         * @param paperSize must not be null
         * @return this builder
         */
        public Builder paperSize(PaperSize paperSize) {
            this.paperSize = Objects.requireNonNull(paperSize, "paperSize");
            return this;
        }

        /**
         * Sets the reading direction.
         *
         * @param readingDirection must not be null
         * @return this builder
         */
        public Builder readingDirection(ReadingDirection readingDirection) {
            this.readingDirection = Objects.requireNonNull(readingDirection, "readingDirection");
            return this;
        }

        /**
         * Sets the imposition layout. Phase 1 always uses {@link ImpositionLayout#FOLIO}.
         *
         * @param layout must not be null
         * @return this builder
         */
        public Builder layout(ImpositionLayout layout) {
            this.layout = Objects.requireNonNull(layout, "layout");
            return this;
        }

        /**
         * Sets the page sequence.
         *
         * @param pageSequence must not be null
         * @return this builder
         */
        public Builder pageSequence(PageSequence pageSequence) {
            this.pageSequence = Objects.requireNonNull(pageSequence, "pageSequence");
            return this;
        }

        /**
         * Sets the padding configuration.
         *
         * @param paddingConfig must not be null
         * @return this builder
         */
        public Builder paddingConfig(PaddingConfig paddingConfig) {
            this.paddingConfig = Objects.requireNonNull(paddingConfig, "paddingConfig");
            return this;
        }

        /**
         * Sets the numbering configuration.
         *
         * @param numberingConfig must not be null
         * @return this builder
         */
        public Builder numberingConfig(NumberingConfig numberingConfig) {
            this.numberingConfig = Objects.requireNonNull(numberingConfig, "numberingConfig");
            return this;
        }

        /**
         * Sets the mark configuration.
         *
         * @param markConfig must not be null
         * @return this builder
         */
        public Builder markConfig(MarkConfig markConfig) {
            this.markConfig = Objects.requireNonNull(markConfig, "markConfig");
            return this;
        }

        /**
         * Sets the creep configuration.
         *
         * @param creepConfig must not be null
         * @return this builder
         */
        public Builder creepConfig(CreepConfig creepConfig) {
            this.creepConfig = Objects.requireNonNull(creepConfig, "creepConfig");
            return this;
        }

        /**
         * Sets the output path, or null to clear.
         *
         * @param outputPath the output file path, or null
         * @return this builder
         */
        public Builder outputPath(Path outputPath) {
            this.outputPath = Optional.ofNullable(outputPath);
            return this;
        }

        /**
         * Builds the {@link QuireProject}.
         *
         * @return a new immutable instance
         * @throws NullPointerException     if any required field is not set
         * @throws IllegalArgumentException if name is blank
         */
        public QuireProject build() {
            Objects.requireNonNull(name, "name must be set");
            Objects.requireNonNull(bindingTechnique, "bindingTechnique must be set");
            Objects.requireNonNull(paperSize, "paperSize must be set");
            Objects.requireNonNull(readingDirection, "readingDirection must be set");
            Objects.requireNonNull(layout, "layout must be set");
            Objects.requireNonNull(pageSequence, "pageSequence must be set");
            Objects.requireNonNull(paddingConfig, "paddingConfig must be set");
            Objects.requireNonNull(numberingConfig, "numberingConfig must be set");
            Objects.requireNonNull(markConfig, "markConfig must be set");
            Objects.requireNonNull(creepConfig, "creepConfig must be set");
            return new QuireProject(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QuireProject other)) {
            return false;
        }
        return name.equals(other.name)
                && bindingTechnique == other.bindingTechnique
                && paperSize == other.paperSize
                && readingDirection == other.readingDirection
                && layout == other.layout
                && pageSequence == other.pageSequence
                && paddingConfig.equals(other.paddingConfig)
                && numberingConfig.equals(other.numberingConfig)
                && markConfig.equals(other.markConfig)
                && creepConfig.equals(other.creepConfig)
                && outputPath.equals(other.outputPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, bindingTechnique, paperSize, readingDirection, layout,
                System.identityHashCode(pageSequence),
                paddingConfig, numberingConfig, markConfig, creepConfig, outputPath);
    }

    @Override
    public String toString() {
        return "QuireProject{"
                + "name='" + name + '\''
                + ", bindingTechnique=" + bindingTechnique
                + ", impositionGroup=" + impositionGroup
                + ", paperSize=" + paperSize
                + '}';
    }
}
