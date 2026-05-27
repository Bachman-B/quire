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

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConversionRequestTest {

    private ConversionRequest.Builder validBuilder() {
        return ConversionRequest.builder()
                .inputFormat(InputFormat.PDF)
                .sourcePath(Path.of("/in/source.pdf"))
                .outputPath(Path.of("/out/result.pdf"));
    }

    @Test
    void builderSetsAllFields() {
        ConversionRequest req = validBuilder().build();
        assertEquals(InputFormat.PDF, req.getInputFormat());
        assertEquals(Path.of("/in/source.pdf"), req.getSourcePath());
        assertEquals(Path.of("/out/result.pdf"), req.getOutputPath());
    }

    @Test
    void missingInputFormatThrows() {
        assertThrows(NullPointerException.class,
                () -> ConversionRequest.builder()
                        .sourcePath(Path.of("/in.pdf"))
                        .outputPath(Path.of("/out.pdf"))
                        .build());
    }

    @Test
    void missingSourcePathThrows() {
        assertThrows(NullPointerException.class,
                () -> ConversionRequest.builder()
                        .inputFormat(InputFormat.PDF)
                        .outputPath(Path.of("/out.pdf"))
                        .build());
    }

    @Test
    void missingOutputPathThrows() {
        assertThrows(NullPointerException.class,
                () -> ConversionRequest.builder()
                        .inputFormat(InputFormat.PDF)
                        .sourcePath(Path.of("/in.pdf"))
                        .build());
    }

    @Test
    void nullInputFormatInSetterThrows() {
        assertThrows(NullPointerException.class,
                () -> ConversionRequest.builder().inputFormat(null));
    }

    @Test
    void nullSourcePathInSetterThrows() {
        assertThrows(NullPointerException.class,
                () -> ConversionRequest.builder().sourcePath(null));
    }

    @Test
    void nullOutputPathInSetterThrows() {
        assertThrows(NullPointerException.class,
                () -> ConversionRequest.builder().outputPath(null));
    }

    @Test
    void equalsSameValues() {
        ConversionRequest a = validBuilder().build();
        ConversionRequest b = validBuilder().build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentFormat() {
        ConversionRequest a = validBuilder().build();
        ConversionRequest b = validBuilder().inputFormat(InputFormat.DOCX).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsSelf() {
        ConversionRequest a = validBuilder().build();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        assertNotEquals(validBuilder().build(), null);
    }

    @Test
    void equalsWrongType() {
        assertNotEquals(validBuilder().build(), "other");
    }

    @Test
    void equalsDifferentSourcePath() {
        ConversionRequest a = validBuilder().build();
        ConversionRequest b = validBuilder().sourcePath(Path.of("/other/source.pdf")).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentOutputPath() {
        ConversionRequest a = validBuilder().build();
        ConversionRequest b = validBuilder().outputPath(Path.of("/other/out.pdf")).build();
        assertNotEquals(a, b);
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(validBuilder().build().toString());
    }
}
