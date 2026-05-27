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

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConversionResultTest {

    @Test
    void buildSuccess() {
        Path out = Path.of("/out/result.pdf");
        ConversionResult result = ConversionResult.builder()
                .success(true)
                .outputPath(out)
                .build();
        assertTrue(result.isSuccess());
        assertEquals(out, result.getOutputPath());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    void buildFailureWithNullOutputPath() {
        ConversionResult result = ConversionResult.builder()
                .success(false)
                .outputPath(null)
                .build();
        assertFalse(result.isSuccess());
        assertNull(result.getOutputPath());
    }

    @Test
    void buildWithWarnings() {
        ConversionWarning w = ConversionWarning.builder()
                .severity(WarningSeverity.WARNING)
                .message("Some warning")
                .build();
        ConversionResult result = ConversionResult.builder()
                .success(true)
                .warnings(List.of(w))
                .build();
        assertEquals(1, result.getWarnings().size());
    }

    @Test
    void nullWarningsThrows() {
        assertThrows(NullPointerException.class,
                () -> ConversionResult.builder().warnings(null));
    }

    @Test
    void equalsSameValues() {
        Path out = Path.of("/out.pdf");
        ConversionResult a = ConversionResult.builder().success(true).outputPath(out).build();
        ConversionResult b = ConversionResult.builder().success(true).outputPath(out).build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsBothNullOutputPath() {
        ConversionResult a = ConversionResult.builder().success(false).build();
        ConversionResult b = ConversionResult.builder().success(false).build();
        assertEquals(a, b);
    }

    @Test
    void equalsDifferentSuccess() {
        ConversionResult a = ConversionResult.builder().success(true).build();
        ConversionResult b = ConversionResult.builder().success(false).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsSelf() {
        ConversionResult a = ConversionResult.builder().success(true).build();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        assertNotEquals(ConversionResult.builder().build(), null);
    }

    @Test
    void equalsWrongType() {
        assertNotEquals(ConversionResult.builder().build(), "other");
    }

    @Test
    void equalsDifferentOutputPath() {
        ConversionResult a = ConversionResult.builder().success(true).outputPath(Path.of("/a.pdf")).build();
        ConversionResult b = ConversionResult.builder().success(true).outputPath(Path.of("/b.pdf")).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentWarnings() {
        Path out = Path.of("/out.pdf");
        ConversionWarning w = ConversionWarning.builder()
                .severity(WarningSeverity.INFO).message("warn").build();
        ConversionResult a = ConversionResult.builder().success(true).outputPath(out).build();
        ConversionResult b = ConversionResult.builder().success(true).outputPath(out)
                .warnings(List.of(w)).build();
        assertNotEquals(a, b);
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(ConversionResult.builder().success(true).build().toString());
    }
}
