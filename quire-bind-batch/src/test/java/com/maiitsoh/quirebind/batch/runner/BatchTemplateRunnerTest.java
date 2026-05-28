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
package com.maiitsoh.quirebind.batch.runner;

import com.maiitsoh.quirebind.batch.model.BatchConfig;
import com.maiitsoh.quirebind.batch.parser.QuireFileParser;
import com.maiitsoh.quirebind.batch.runner.BatchTemplateRunner.TemplateJobResult;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BatchTemplateRunnerTest {

    @TempDir
    Path tempDir;

    private static final String TEMPLATE_YAML = """
            quire_version: "1.0"
            defaults:
              technique: saddle_stitch
              paper_size: a4
              reading_direction: ltr
            """;

    private Path createPdf(int pages) throws IOException {
        Path p = tempDir.resolve("src" + pages + ".pdf");
        try (PDDocument doc = new PDDocument()) {
            for (int i = 0; i < pages; i++) {
                doc.addPage(new PDPage(PDRectangle.A4));
            }
            doc.save(p.toFile());
        }
        return p;
    }

    @Test
    void nullTemplateThrows() {
        assertThrows(NullPointerException.class,
            () -> BatchTemplateRunner.run(null, List.of(), p -> p, false));
    }

    @Test
    void nullInputsThrows() throws IOException {
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        assertThrows(NullPointerException.class,
            () -> BatchTemplateRunner.run(t, null, p -> p, false));
    }

    @Test
    void nullMapperThrows() throws IOException {
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        assertThrows(NullPointerException.class,
            () -> BatchTemplateRunner.run(t, List.of(), null, false));
    }

    @Test
    void emptyInputsReturnsEmptyList() throws IOException {
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        List<TemplateJobResult> results = BatchTemplateRunner.run(t, List.of(), p -> p, true);
        assertTrue(results.isEmpty());
    }

    @Test
    void resultsListIsUnmodifiable() throws IOException {
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        List<TemplateJobResult> results = BatchTemplateRunner.run(t, List.of(), p -> p, true);
        assertThrows(UnsupportedOperationException.class, () -> results.add(null));
    }

    @Test
    void runWithSuffixDryRunSucceeds() throws IOException {
        Path src = createPdf(4);
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        List<TemplateJobResult> results =
            BatchTemplateRunner.runWithSuffix(t, List.of(src), "-imposed", true);
        assertEquals(1, results.size());
        assertTrue(results.get(0).success(), results.get(0).message());
    }

    @Test
    void runWithSuffixWritesOutputFile() throws IOException {
        Path src = createPdf(4);
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        List<TemplateJobResult> results =
            BatchTemplateRunner.runWithSuffix(t, List.of(src), "-imposed", false);
        assertTrue(results.get(0).success(), results.get(0).message());
        Path expected = tempDir.resolve("src4-imposed.pdf");
        assertTrue(Files.exists(expected));
        try (PDDocument doc = Loader.loadPDF(expected.toFile())) {
            assertTrue(doc.getNumberOfPages() > 0);
        }
    }

    @Test
    void runWithSuffixDerivesCorrectOutputPath() throws IOException {
        Path src = createPdf(4);
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        List<TemplateJobResult> results =
            BatchTemplateRunner.runWithSuffix(t, List.of(src), "-done", true);
        assertEquals(tempDir.resolve("src4-done.pdf"), results.get(0).outputPath());
    }

    @Test
    void runWithDirectoryWritesOutputToDir() throws IOException {
        Path src = createPdf(4);
        Path outDir = tempDir.resolve("output");
        Files.createDirectories(outDir);
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        List<TemplateJobResult> results =
            BatchTemplateRunner.runWithDirectory(t, List.of(src), outDir, false);
        assertTrue(results.get(0).success(), results.get(0).message());
        assertTrue(Files.exists(outDir.resolve("src4.pdf")));
    }

    @Test
    void runWithDirectoryNullDirThrows() throws IOException {
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        assertThrows(NullPointerException.class,
            () -> BatchTemplateRunner.runWithDirectory(t, List.of(), null, true));
    }

    @Test
    void missingInputFileProducesFailedResult() throws IOException {
        Path missing = tempDir.resolve("nonexistent.pdf");
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        List<TemplateJobResult> results =
            BatchTemplateRunner.runWithSuffix(t, List.of(missing), "-imposed", false);
        assertEquals(1, results.size());
        assertFalse(results.get(0).success());
    }

    @Test
    void multipleInputsAllProcessed() throws IOException {
        Path src1 = createPdf(4);
        Path src2 = tempDir.resolve("src8.pdf");
        try (PDDocument doc = new PDDocument()) {
            for (int i = 0; i < 8; i++) {
                doc.addPage(new PDPage(PDRectangle.A4));
            }
            doc.save(src2.toFile());
        }
        BatchConfig t = QuireFileParser.parseString(TEMPLATE_YAML);
        List<TemplateJobResult> results =
            BatchTemplateRunner.runWithSuffix(t, List.of(src1, src2), "-imp", false);
        assertEquals(2, results.size());
        assertTrue(results.get(0).success(), results.get(0).message());
        assertTrue(results.get(1).success(), results.get(1).message());
    }

    @Test
    void techniqueFromDefaultsIsUsed() throws IOException {
        Path src = createPdf(16);
        String yaml = """
                quire_version: "1.0"
                defaults:
                  technique: sewn_signatures
                  signature_size: 4
                  paper_size: a4
                  reading_direction: ltr
                """;
        BatchConfig t = QuireFileParser.parseString(yaml);
        List<TemplateJobResult> results =
            BatchTemplateRunner.runWithSuffix(t, List.of(src), "-sigs", false);
        assertTrue(results.get(0).success(), results.get(0).message());
    }
}
