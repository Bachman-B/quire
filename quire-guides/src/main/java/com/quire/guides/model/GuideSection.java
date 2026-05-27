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
 * A single navigable section within a {@link BindingGuide}.
 *
 * <p>The {@link #id} matches the HTML anchor produced when the guide's markdown body is
 * rendered (e.g. the section {@code id: overview} corresponds to {@code {#overview}}).
 */
public final class GuideSection {

    private final String id;
    private final String title;

    /**
     * Creates a guide section.
     *
     * @param id    the anchor identifier; must not be null or blank
     * @param title the display title; must not be null or blank
     */
    public GuideSection(String id, String title) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be null or blank");
        }
        this.id = id;
        this.title = title;
    }

    /** Returns the anchor identifier for this section. */
    public String getId() {
        return id;
    }

    /** Returns the display title of this section. */
    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GuideSection other)) {
            return false;
        }
        return id.equals(other.id) && title.equals(other.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "GuideSection{id='" + id + "', title='" + title + "'}";
    }
}
