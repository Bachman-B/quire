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
package com.quire.guides.model;

import java.util.Objects;

/**
 * An immutable binding guide consisting of YAML-parsed {@link GuideMetadata} and a
 * raw Markdown body.
 *
 * <p>Instances are produced by {@link com.quire.guides.loader.GuideLoader} from the
 * bundled {@code .md} resources under {@code /guides/}.
 */
public final class BindingGuide {

    private final GuideMetadata metadata;
    private final String body;

    /**
     * Creates a binding guide.
     *
     * @param metadata parsed YAML frontmatter; must not be null
     * @param body     raw Markdown body (everything after the frontmatter); must not be null
     */
    public BindingGuide(GuideMetadata metadata, String body) {
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.body = Objects.requireNonNull(body, "body");
    }

    /** Returns the parsed metadata from the YAML frontmatter. */
    public GuideMetadata getMetadata() {
        return metadata;
    }

    /** Returns the raw Markdown body of the guide (everything after the frontmatter). */
    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BindingGuide other)) {
            return false;
        }
        return metadata.equals(other.metadata) && body.equals(other.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata, body);
    }

    @Override
    public String toString() {
        return "BindingGuide{" + metadata + "}";
    }
}
