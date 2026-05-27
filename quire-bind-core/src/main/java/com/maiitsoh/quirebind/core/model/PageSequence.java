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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An ordered, mutable sequence of {@link QuirePage} instances representing the logical content
 * to be imposed.
 *
 * <p>After any structural change (insert, remove, move), call {@link #reindex()} to update
 * each page's {@link QuirePage#getPhysicalPosition()} to match its current list index.
 *
 * <p>This is the only mutable domain object. Callers must synchronise externally if accessed
 * from multiple threads.
 */
public final class PageSequence {

    private final List<QuirePage> pages;

    /** Creates an empty sequence. */
    public PageSequence() {
        this.pages = new ArrayList<>();
    }

    /**
     * Creates a sequence pre-populated with the given pages.
     *
     * @param pages must not be null
     */
    public PageSequence(List<QuirePage> pages) {
        this.pages = new ArrayList<>(Objects.requireNonNull(pages, "pages"));
    }

    /** Returns an unmodifiable view of the current page list. */
    public List<QuirePage> getPages() {
        return Collections.unmodifiableList(pages);
    }

    /** Returns the number of pages in this sequence. */
    public int pageCount() {
        return pages.size();
    }

    /**
     * Inserts a page at the given 0-based position, shifting subsequent pages right.
     * Call {@link #reindex()} after all inserts are complete.
     *
     * @param position 0-based index at which to insert; must be in [0, pageCount()]
     * @param page     the page to insert; must not be null
     */
    public void insertPage(int position, QuirePage page) {
        if (position < 0 || position > pages.size()) {
            throw new IndexOutOfBoundsException(
                    "position " + position + " out of bounds for size " + pages.size());
        }
        pages.add(position, Objects.requireNonNull(page, "page"));
    }

    /**
     * Removes the page at the given 0-based position.
     * Call {@link #reindex()} after all removals are complete.
     *
     * @param position 0-based index to remove; must be in [0, pageCount())
     * @return the removed page
     */
    public QuirePage removePage(int position) {
        return pages.remove(position);
    }

    /**
     * Moves the page at {@code fromPosition} to {@code toPosition}.
     * Call {@link #reindex()} after the move.
     *
     * @param fromPosition source index; must be in [0, pageCount())
     * @param toPosition   target index; must be in [0, pageCount())
     */
    public void movePage(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        QuirePage page = pages.remove(fromPosition);
        pages.add(toPosition, page);
    }

    /**
     * Reassigns {@link QuirePage#getPhysicalPosition()} on every page to match its current
     * list index. Must be called after any structural change to keep positions accurate.
     */
    public void reindex() {
        for (int i = 0; i < pages.size(); i++) {
            QuirePage page = pages.get(i);
            if (page.getPhysicalPosition() != i) {
                pages.set(i, page.toBuilder().physicalPosition(i).build());
            }
        }
    }

    /**
     * Returns {@code true} if this sequence's page count is valid for the given binding group
     * and signature size.
     *
     * <ul>
     *   <li>Group A: any positive page count is valid.</li>
     *   <li>Group B: page count must be a positive multiple of 4.</li>
     *   <li>Group C: page count must be a positive multiple of {@code 4 × signatureSize}.</li>
     * </ul>
     *
     * @param group         the imposition group; must not be null
     * @param signatureSize sheets per signature (relevant only for Group C); must be positive
     * @return true if the sequence is valid for the given constraints
     */
    public boolean isValidForBinding(ImpositionGroup group, int signatureSize) {
        Objects.requireNonNull(group, "group");
        int count = pages.size();
        if (count <= 0) {
            return false;
        }
        return switch (group) {
            case A -> true;
            case B -> count % 4 == 0;
            case C -> signatureSize > 0 && count % (4 * signatureSize) == 0;
        };
    }

    @Override
    public String toString() {
        return "PageSequence{pageCount=" + pages.size() + '}';
    }
}
