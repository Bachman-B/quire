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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageSequenceTest {

    private QuirePage page(int position) {
        return QuirePage.builder()
                .physicalPosition(position)
                .pageType(PageType.CONTENT)
                .build();
    }

    @Test
    void emptySequenceHasZeroCount() {
        PageSequence seq = new PageSequence();
        assertEquals(0, seq.pageCount());
        assertTrue(seq.getPages().isEmpty());
    }

    @Test
    void constructorWithListCopiesPages() {
        List<QuirePage> pages = List.of(page(0), page(1));
        PageSequence seq = new PageSequence(pages);
        assertEquals(2, seq.pageCount());
    }

    @Test
    void constructorNullThrows() {
        assertThrows(NullPointerException.class, () -> new PageSequence(null));
    }

    @Test
    void insertPageAtStart() {
        PageSequence seq = new PageSequence();
        QuirePage p = page(0);
        seq.insertPage(0, p);
        assertEquals(1, seq.pageCount());
        assertEquals(p, seq.getPages().get(0));
    }

    @Test
    void insertPageNullThrows() {
        PageSequence seq = new PageSequence();
        assertThrows(NullPointerException.class, () -> seq.insertPage(0, null));
    }

    @Test
    void insertPageOutOfBoundsThrows() {
        PageSequence seq = new PageSequence();
        assertThrows(IndexOutOfBoundsException.class, () -> seq.insertPage(1, page(0)));
    }

    @Test
    void insertPageNegativeIndexThrows() {
        PageSequence seq = new PageSequence();
        assertThrows(IndexOutOfBoundsException.class, () -> seq.insertPage(-1, page(0)));
    }

    @Test
    void removePageReturnRemovedPage() {
        PageSequence seq = new PageSequence(List.of(page(0), page(1)));
        QuirePage removed = seq.removePage(0);
        assertEquals(PageType.CONTENT, removed.getPageType());
        assertEquals(1, seq.pageCount());
    }

    @Test
    void movePageSamePositionIsNoOp() {
        PageSequence seq = new PageSequence(List.of(page(0), page(1)));
        QuirePage first = seq.getPages().get(0);
        seq.movePage(0, 0);
        assertEquals(first, seq.getPages().get(0));
    }

    @Test
    void movePageShiftsCorrectly() {
        QuirePage a = QuirePage.builder().physicalPosition(0).pageType(PageType.CONTENT)
                .logicalPageNumber(1).build();
        QuirePage b = QuirePage.builder().physicalPosition(1).pageType(PageType.AESTHETIC).build();
        PageSequence seq = new PageSequence(List.of(a, b));
        seq.movePage(0, 1);
        assertEquals(PageType.AESTHETIC, seq.getPages().get(0).getPageType());
        assertEquals(PageType.CONTENT, seq.getPages().get(1).getPageType());
    }

    @Test
    void reindexUpdatesPhysicalPositions() {
        QuirePage a = QuirePage.builder().physicalPosition(99).pageType(PageType.CONTENT).build();
        QuirePage b = QuirePage.builder().physicalPosition(99).pageType(PageType.AESTHETIC).build();
        PageSequence seq = new PageSequence(List.of(a, b));
        seq.reindex();
        assertEquals(0, seq.getPages().get(0).getPhysicalPosition());
        assertEquals(1, seq.getPages().get(1).getPhysicalPosition());
    }

    @Test
    void reindexSkipsAlreadyCorrectPositions() {
        QuirePage a = QuirePage.builder().physicalPosition(0).pageType(PageType.CONTENT).build();
        PageSequence seq = new PageSequence(List.of(a));
        seq.reindex();
        assertEquals(0, seq.getPages().get(0).getPhysicalPosition());
    }

    @Test
    void getPageReturnsUnmodifiableView() {
        PageSequence seq = new PageSequence(List.of(page(0)));
        assertThrows(UnsupportedOperationException.class,
                () -> seq.getPages().add(page(1)));
    }

    @Test
    void isValidForBindingGroupARequiresNonEmpty() {
        PageSequence empty = new PageSequence();
        assertFalse(empty.isValidForBinding(ImpositionGroup.A, 0));

        PageSequence one = new PageSequence(List.of(page(0)));
        assertTrue(one.isValidForBinding(ImpositionGroup.A, 0));
    }

    @Test
    void isValidForBindingGroupBRequiresMultipleOfFour() {
        PageSequence three = new PageSequence(List.of(page(0), page(1), page(2)));
        assertFalse(three.isValidForBinding(ImpositionGroup.B, 0));

        PageSequence four = new PageSequence(List.of(page(0), page(1), page(2), page(3)));
        assertTrue(four.isValidForBinding(ImpositionGroup.B, 0));
    }

    @Test
    void isValidForBindingGroupCRequiresMultipleOfFourTimesSignatureSize() {
        PageSequence empty = new PageSequence();
        assertFalse(empty.isValidForBinding(ImpositionGroup.C, 4));

        PageSequence sixteen = new PageSequence();
        for (int i = 0; i < 16; i++) {
            sixteen.insertPage(i, page(i));
        }
        assertTrue(sixteen.isValidForBinding(ImpositionGroup.C, 4));
        assertFalse(sixteen.isValidForBinding(ImpositionGroup.C, 5));
    }

    @Test
    void isValidForBindingGroupCZeroSignatureSizeReturnsFalse() {
        PageSequence seq = new PageSequence(List.of(page(0)));
        assertFalse(seq.isValidForBinding(ImpositionGroup.C, 0));
    }

    @Test
    void isValidForBindingNullGroupThrows() {
        PageSequence seq = new PageSequence(List.of(page(0)));
        assertThrows(NullPointerException.class,
                () -> seq.isValidForBinding(null, 4));
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(new PageSequence().toString());
    }
}
