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
package com.quirebind.guides.model;

import java.util.List;
import java.util.Objects;

/**
 * Immutable metadata parsed from the YAML frontmatter of a binding guide file.
 *
 * <p>Fields map directly to the frontmatter keys defined in the guide format:
 * <pre>
 * quire_guide_version: "1.0"
 * technique_id: saddle_stitch
 * locale: en
 * title: "Saddle stitch"
 * ...
 * </pre>
 */
public final class GuideMetadata {

    private final String quireGuideVersion;
    private final String techniqueId;
    private final String locale;
    private final String title;
    private final String subtitle;
    private final String group;
    private final String difficulty;
    private final String timeEstimate;
    private final String schematicDescription;
    private final List<String> tools;
    private final List<String> materials;
    private final List<String> readingDirectionsSupported;
    private final List<String> relatedTechniques;
    private final List<GuideSection> sections;

    private GuideMetadata(Builder builder) {
        this.quireGuideVersion = builder.quireGuideVersion;
        this.techniqueId = builder.techniqueId;
        this.locale = builder.locale;
        this.title = builder.title;
        this.subtitle = builder.subtitle;
        this.group = builder.group;
        this.difficulty = builder.difficulty;
        this.timeEstimate = builder.timeEstimate;
        this.schematicDescription = builder.schematicDescription;
        this.tools = List.copyOf(builder.tools);
        this.materials = List.copyOf(builder.materials);
        this.readingDirectionsSupported = List.copyOf(builder.readingDirectionsSupported);
        this.relatedTechniques = List.copyOf(builder.relatedTechniques);
        this.sections = List.copyOf(builder.sections);
    }

    /** Returns the guide format version (e.g. {@code "1.0"}). */
    public String getQuireGuideVersion() {
        return quireGuideVersion;
    }

    /** Returns the binding technique identifier (e.g. {@code saddle_stitch}). */
    public String getTechniqueId() {
        return techniqueId;
    }

    /** Returns the BCP 47 locale tag (e.g. {@code en}). */
    public String getLocale() {
        return locale;
    }

    /** Returns the display title of the guide. */
    public String getTitle() {
        return title;
    }

    /** Returns the subtitle, or an empty string if absent. */
    public String getSubtitle() {
        return subtitle;
    }

    /** Returns the imposition group letter ({@code A}, {@code B}, or {@code C}). */
    public String getGroup() {
        return group;
    }

    /** Returns the difficulty level (e.g. {@code beginner}, {@code intermediate}). */
    public String getDifficulty() {
        return difficulty;
    }

    /** Returns the estimated time to complete the binding (e.g. {@code "30–60 minutes"}). */
    public String getTimeEstimate() {
        return timeEstimate;
    }

    /** Returns a short description of the schematic illustration. */
    public String getSchematicDescription() {
        return schematicDescription;
    }

    /** Returns the list of tools required. */
    public List<String> getTools() {
        return tools;
    }

    /** Returns the list of materials required. */
    public List<String> getMaterials() {
        return materials;
    }

    /** Returns the supported reading directions (e.g. {@code ["ltr", "rtl"]}). */
    public List<String> getReadingDirectionsSupported() {
        return readingDirectionsSupported;
    }

    /** Returns the technique IDs of related binding methods. */
    public List<String> getRelatedTechniques() {
        return relatedTechniques;
    }

    /** Returns the ordered list of navigable sections in this guide. */
    public List<GuideSection> getSections() {
        return sections;
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link GuideMetadata}. */
    public static final class Builder {

        private String quireGuideVersion = "";
        private String techniqueId = "";
        private String locale = "";
        private String title = "";
        private String subtitle = "";
        private String group = "";
        private String difficulty = "";
        private String timeEstimate = "";
        private String schematicDescription = "";
        private List<String> tools = List.of();
        private List<String> materials = List.of();
        private List<String> readingDirectionsSupported = List.of();
        private List<String> relatedTechniques = List.of();
        private List<GuideSection> sections = List.of();

        private Builder() {
        }

        /** Sets {@code quire_guide_version}. */
        public Builder quireGuideVersion(String v) {
            this.quireGuideVersion = Objects.requireNonNullElse(v, "");
            return this;
        }

        /** Sets {@code technique_id}. */
        public Builder techniqueId(String id) {
            this.techniqueId = Objects.requireNonNullElse(id, "");
            return this;
        }

        /** Sets {@code locale}. */
        public Builder locale(String locale) {
            this.locale = Objects.requireNonNullElse(locale, "");
            return this;
        }

        /** Sets {@code title}. */
        public Builder title(String title) {
            this.title = Objects.requireNonNullElse(title, "");
            return this;
        }

        /** Sets {@code subtitle}. */
        public Builder subtitle(String subtitle) {
            this.subtitle = Objects.requireNonNullElse(subtitle, "");
            return this;
        }

        /** Sets {@code group}. */
        public Builder group(String group) {
            this.group = Objects.requireNonNullElse(group, "");
            return this;
        }

        /** Sets {@code difficulty}. */
        public Builder difficulty(String difficulty) {
            this.difficulty = Objects.requireNonNullElse(difficulty, "");
            return this;
        }

        /** Sets {@code time_estimate}. */
        public Builder timeEstimate(String timeEstimate) {
            this.timeEstimate = Objects.requireNonNullElse(timeEstimate, "");
            return this;
        }

        /** Sets {@code schematic_description}. */
        public Builder schematicDescription(String desc) {
            this.schematicDescription = Objects.requireNonNullElse(desc, "");
            return this;
        }

        /** Sets {@code tools}. */
        public Builder tools(List<String> tools) {
            this.tools = tools != null ? tools : List.of();
            return this;
        }

        /** Sets {@code materials}. */
        public Builder materials(List<String> materials) {
            this.materials = materials != null ? materials : List.of();
            return this;
        }

        /** Sets {@code reading_directions_supported}. */
        public Builder readingDirectionsSupported(List<String> dirs) {
            this.readingDirectionsSupported = dirs != null ? dirs : List.of();
            return this;
        }

        /** Sets {@code related_techniques}. */
        public Builder relatedTechniques(List<String> techniques) {
            this.relatedTechniques = techniques != null ? techniques : List.of();
            return this;
        }

        /** Sets {@code sections}. */
        public Builder sections(List<GuideSection> sections) {
            this.sections = sections != null ? sections : List.of();
            return this;
        }

        /** Builds the {@link GuideMetadata}. */
        public GuideMetadata build() {
            return new GuideMetadata(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GuideMetadata other)) {
            return false;
        }
        return techniqueId.equals(other.techniqueId)
                && locale.equals(other.locale)
                && title.equals(other.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(techniqueId, locale, title);
    }

    @Override
    public String toString() {
        return "GuideMetadata{techniqueId='" + techniqueId + "', locale='" + locale
                + "', title='" + title + "'}";
    }
}
