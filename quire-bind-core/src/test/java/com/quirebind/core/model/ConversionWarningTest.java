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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConversionWarningTest {

    @Test
    void builderSetsAllFields() {
        ConversionWarning w = ConversionWarning.builder()
                .severity(WarningSeverity.WARNING)
                .message("Font not embedded")
                .build();
        assertEquals(WarningSeverity.WARNING, w.getSeverity());
        assertEquals("Font not embedded", w.getMessage());
    }

    @Test
    void missingSeverityInBuildThrows() {
        assertThrows(NullPointerException.class,
                () -> ConversionWarning.builder().message("msg").build());
    }

    @Test
    void missingMessageInBuildThrows() {
        assertThrows(NullPointerException.class,
                () -> ConversionWarning.builder().severity(WarningSeverity.INFO).build());
    }

    @Test
    void nullSeverityInSetterThrows() {
        assertThrows(NullPointerException.class,
                () -> ConversionWarning.builder().severity(null));
    }

    @Test
    void nullMessageInSetterThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ConversionWarning.builder().message(null));
    }

    @Test
    void blankMessageInSetterThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ConversionWarning.builder().message("   "));
    }

    @Test
    void equalsSameValues() {
        ConversionWarning a = ConversionWarning.builder()
                .severity(WarningSeverity.INFO).message("msg").build();
        ConversionWarning b = ConversionWarning.builder()
                .severity(WarningSeverity.INFO).message("msg").build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentMessage() {
        ConversionWarning a = ConversionWarning.builder()
                .severity(WarningSeverity.INFO).message("alpha").build();
        ConversionWarning b = ConversionWarning.builder()
                .severity(WarningSeverity.INFO).message("beta").build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsSelf() {
        ConversionWarning a = ConversionWarning.builder()
                .severity(WarningSeverity.ERROR).message("boom").build();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        assertNotEquals(ConversionWarning.builder()
                .severity(WarningSeverity.INFO).message("x").build(), null);
    }

    @Test
    void equalsWrongType() {
        assertNotEquals(ConversionWarning.builder()
                .severity(WarningSeverity.INFO).message("x").build(), "other");
    }

    @Test
    void equalsDifferentSeverity() {
        ConversionWarning a = ConversionWarning.builder()
                .severity(WarningSeverity.INFO).message("x").build();
        ConversionWarning b = ConversionWarning.builder()
                .severity(WarningSeverity.ERROR).message("x").build();
        assertNotEquals(a, b);
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(ConversionWarning.builder()
                .severity(WarningSeverity.WARNING).message("warn").build().toString());
    }
}
