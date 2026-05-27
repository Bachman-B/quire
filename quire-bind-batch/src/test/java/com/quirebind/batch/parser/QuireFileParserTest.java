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
package com.quirebind.batch.parser;

import com.quirebind.batch.model.BatchConfig;
import com.quirebind.batch.model.BatchJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class QuireFileParserTest {

    @TempDir
    Path tempDir;

    private static final String MINIMAL = """
            quire_version: "1.0"
            jobs:
              - name: "Test job"
                source: "./input.pdf"
                output: "./output.pdf"
                technique: saddle_stitch
            """;

    private static final String WITH_DEFAULTS = """
            quire_version: "1.0"
            defaults:
              reading_direction: rtl
              paper_size: a5
              numbering:
                body_style: roman
                body_start: 3
                suppress_first_body_folio: false
              marks:
                fold_lines: false
                signature_proof_markers: false
            jobs:
              - name: "RTL job"
                source: "./in.pdf"
                output: "./out.pdf"
                technique: hardcover
                signature_size: 4
            """;

    @Test
    void nullPathThrows() {
        assertThrows(NullPointerException.class, () -> QuireFileParser.parse(null));
    }

    @Test
    void nullContentThrows() {
        assertThrows(NullPointerException.class, () -> QuireFileParser.parseString(null));
    }

    @Test
    void nonExistentFileThrowsIoException() {
        assertThrows(IOException.class,
                () -> QuireFileParser.parse(Path.of("/nonexistent/file.quire")));
    }

    @Test
    void parseMinimalYamlReturnsConfig() throws IOException {
        BatchConfig config = QuireFileParser.parseString(MINIMAL);
        assertNotNull(config);
    }

    @Test
    void parseVersionField() throws IOException {
        BatchConfig config = QuireFileParser.parseString(MINIMAL);
        assertEquals("1.0", config.quireVersion());
    }

    @Test
    void parseJobCount() throws IOException {
        BatchConfig config = QuireFileParser.parseString(MINIMAL);
        assertEquals(1, config.jobs().size());
    }

    @Test
    void parseJobName() throws IOException {
        BatchJob job = QuireFileParser.parseString(MINIMAL).jobs().get(0);
        assertEquals("Test job", job.name());
    }

    @Test
    void parseJobSource() throws IOException {
        BatchJob job = QuireFileParser.parseString(MINIMAL).jobs().get(0);
        assertEquals("./input.pdf", job.source());
    }

    @Test
    void parseJobOutput() throws IOException {
        BatchJob job = QuireFileParser.parseString(MINIMAL).jobs().get(0);
        assertEquals("./output.pdf", job.output());
    }

    @Test
    void parseJobTechnique() throws IOException {
        BatchJob job = QuireFileParser.parseString(MINIMAL).jobs().get(0);
        assertEquals("saddle_stitch", job.technique());
    }

    @Test
    void emptyYamlProducesEmptyJobList() throws IOException {
        BatchConfig config = QuireFileParser.parseString("quire_version: \"1.0\"");
        assertTrue(config.jobs().isEmpty());
    }

    @Test
    void jobListIsUnmodifiable() throws IOException {
        BatchConfig config = QuireFileParser.parseString(MINIMAL);
        assertThrows(UnsupportedOperationException.class, () -> config.jobs().add(null));
    }

    @Test
    void defaultsReadingDirectionParsed() throws IOException {
        BatchConfig config = QuireFileParser.parseString(WITH_DEFAULTS);
        assertEquals("rtl", config.defaults().readingDirection());
    }

    @Test
    void defaultsPaperSizeParsed() throws IOException {
        BatchConfig config = QuireFileParser.parseString(WITH_DEFAULTS);
        assertEquals("a5", config.defaults().paperSize());
    }

    @Test
    void defaultsNumberingBodyStyleParsed() throws IOException {
        BatchConfig config = QuireFileParser.parseString(WITH_DEFAULTS);
        assertEquals("roman", config.defaults().numbering().bodyStyle());
    }

    @Test
    void defaultsMarksFoldLinesParsed() throws IOException {
        BatchConfig config = QuireFileParser.parseString(WITH_DEFAULTS);
        assertFalse(config.defaults().marks().foldLines());
    }

    @Test
    void jobSignatureSizeParsed() throws IOException {
        BatchJob job = QuireFileParser.parseString(WITH_DEFAULTS).jobs().get(0);
        assertEquals(4, job.signatureSize());
    }

    @Test
    void paddingFieldsParsed() throws IOException {
        String yaml = """
                quire_version: "1.0"
                jobs:
                  - name: "p"
                    source: "./s.pdf"
                    output: "./o.pdf"
                    technique: saddle_stitch
                    padding:
                      aesthetic_front: 2
                      aesthetic_rear: 3
                """;
        BatchJob job = QuireFileParser.parseString(yaml).jobs().get(0);
        assertEquals(2, job.padding().aestheticFront());
        assertEquals(3, job.padding().aestheticRear());
    }

    @Test
    void creepGsmParsed() throws IOException {
        String yaml = """
                quire_version: "1.0"
                jobs:
                  - name: "c"
                    source: "./s.pdf"
                    output: "./o.pdf"
                    technique: saddle_stitch
                    creep:
                      paper_weight_gsm: 90.0
                """;
        BatchJob job = QuireFileParser.parseString(yaml).jobs().get(0);
        assertEquals(90.0, job.creep().paperWeightGsm(), 1e-9);
    }

    @Test
    void parseFromFile() throws IOException {
        Path file = tempDir.resolve("test.quire");
        Files.writeString(file, MINIMAL, StandardCharsets.UTF_8);
        BatchConfig config = QuireFileParser.parse(file);
        assertEquals(1, config.jobs().size());
    }

    @Test
    void missingDefaultsUsesFactoryDefault() throws IOException {
        BatchConfig config = QuireFileParser.parseString(MINIMAL);
        assertNotNull(config.defaults());
        assertEquals("ltr", config.defaults().readingDirection());
    }

    @Test
    void exampleQuireFileParsesSuccessfully() throws IOException {
        try (InputStream in = getClass().getResourceAsStream(
                "/examples/example.quire")) {
            assertNotNull(in, "example.quire not found on classpath");
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            BatchConfig config = QuireFileParser.parseString(content);
            assertEquals(4, config.jobs().size());
        }
    }
}
