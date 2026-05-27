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

import static org.junit.jupiter.api.Assertions.assertEquals;

class FolioPositionTest {

    @Test
    void valuesExist() {
        assertEquals(2, FolioPosition.values().length);
    }

    @Test
    void valueOfRoundtrips() {
        assertEquals(FolioPosition.INNER_MARGIN, FolioPosition.valueOf("INNER_MARGIN"));
        assertEquals(FolioPosition.OUTER_MARGIN, FolioPosition.valueOf("OUTER_MARGIN"));
    }
}
