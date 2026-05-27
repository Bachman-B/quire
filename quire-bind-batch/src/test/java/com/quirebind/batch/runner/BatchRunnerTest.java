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
package com.quirebind.batch.runner;

import com.quirebind.batch.model.BatchConfig;
import com.quirebind.batch.parser.QuireFileParser;
import com.quirebind.batch.runner.BatchRunner.JobResult;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BatchRunnerTest {

    @TempDir
    Path tempDir;

    private Path createPdf(int pages) throws IOException {
        Path p = tempDir.resolve("src.pdf");
        try (PDDocument doc = new PDDocument()) {
            for (int i = 0; i < pages; i++) {
                doc.addPage(new PDPage(PDRectangle.A4));
            }
            doc.save(p.toFile());
        }
        return p;
    }

    @Test
    void nullConfigThrows() {
        assertThrows(NullPointerException.class,
                () -> BatchRunner.run(null, tempDir, false));
    }

    @Test
    void nullBaseDirThrows() throws IOException {
        BatchConfig cfg = QuireFileParser.parseString("quire_version: \"1.0\"");
        assertThrows(NullPointerException.class,
                () -> BatchRunner.run(cfg, null, false));
    }

    @Test
    void emptyJobListReturnsEmptyResults() throws IOException {
        BatchConfig cfg = QuireFileParser.parseString("quire_version: \"1.0\"");
        List<JobResult> results = BatchRunner.run(cfg, tempDir, false);
        assertTrue(results.isEmpty());
    }

    @Test
    void resultsListIsUnmodifiable() throws IOException {
        BatchConfig cfg = QuireFileParser.parseString("quire_version: \"1.0\"");
        List<JobResult> results = BatchRunner.run(cfg, tempDir, false);
        assertThrows(UnsupportedOperationException.class, () -> results.add(null));
    }

    @Test
    void saddleStitchFourPagesDryRunSucceeds() throws IOException {
        Path src = createPdf(4);
        String yaml = """
                quire_version: "1.0"
                jobs:
                  - name: "dry-run test"
                    source: "%s"
                    output: "./out.pdf"
                    technique: saddle_stitch
                """.formatted(src.getFileName());
        BatchConfig cfg = QuireFileParser.parseString(yaml);
        List<JobResult> results = BatchRunner.run(cfg, tempDir, true);
        assertEquals(1, results.size());
        assertTrue(results.get(0).success(), results.get(0).message());
        assertTrue(results.get(0).message().contains("dry-run"));
    }

    @Test
    void saddleStitchFourPagesWritesOutputPdf() throws IOException {
        Path src = createPdf(4);
        Path out = tempDir.resolve("output.pdf");
        String yaml = """
                quire_version: "1.0"
                jobs:
                  - name: "output test"
                    source: "%s"
                    output: "%s"
                    technique: saddle_stitch
                """.formatted(src.getFileName(), out.getFileName());
        BatchConfig cfg = QuireFileParser.parseString(yaml);
        List<JobResult> results = BatchRunner.run(cfg, tempDir, false);
        assertTrue(results.get(0).success(), results.get(0).message());
        assertTrue(out.toFile().exists());
        try (PDDocument doc = Loader.loadPDF(out.toFile())) {
            assertEquals(2, doc.getNumberOfPages());
        }
    }

    @Test
    void multipleJobsAllSucceed() throws IOException {
        Path src = createPdf(4);
        String yaml = """
                quire_version: "1.0"
                jobs:
                  - name: "job1"
                    source: "%s"
                    output: "./out1.pdf"
                    technique: saddle_stitch
                  - name: "job2"
                    source: "%s"
                    output: "./out2.pdf"
                    technique: perfect_binding
                """.formatted(src.getFileName(), src.getFileName());
        BatchConfig cfg = QuireFileParser.parseString(yaml);
        List<JobResult> results = BatchRunner.run(cfg, tempDir, false);
        assertEquals(2, results.size());
        assertTrue(results.get(0).success());
        assertTrue(results.get(1).success());
    }

    @Test
    void missingSourceFileProducesFailedResult() throws IOException {
        String yaml = """
                quire_version: "1.0"
                jobs:
                  - name: "missing"
                    source: "./nonexistent.pdf"
                    output: "./out.pdf"
                    technique: saddle_stitch
                """;
        BatchConfig cfg = QuireFileParser.parseString(yaml);
        List<JobResult> results = BatchRunner.run(cfg, tempDir, false);
        assertEquals(1, results.size());
        assertFalse(results.get(0).success());
        assertEquals("missing", results.get(0).name());
    }

    @Test
    void unknownTechniqueProducesFailedResult() throws IOException {
        Path src = createPdf(4);
        String yaml = """
                quire_version: "1.0"
                jobs:
                  - name: "bad-technique"
                    source: "%s"
                    output: "./out.pdf"
                    technique: unknown_binding
                """.formatted(src.getFileName());
        BatchConfig cfg = QuireFileParser.parseString(yaml);
        List<JobResult> results = BatchRunner.run(cfg, tempDir, false);
        assertFalse(results.get(0).success());
    }

    @Test
    void defaultsAppliedWhenJobOmitsField() throws IOException {
        Path src = createPdf(4);
        String yaml = """
                quire_version: "1.0"
                defaults:
                  reading_direction: ltr
                  paper_size: a4
                jobs:
                  - name: "defaults test"
                    source: "%s"
                    output: "./out.pdf"
                    technique: saddle_stitch
                """.formatted(src.getFileName());
        BatchConfig cfg = QuireFileParser.parseString(yaml);
        List<JobResult> results = BatchRunner.run(cfg, tempDir, false);
        assertTrue(results.get(0).success(), results.get(0).message());
    }

    @Test
    void rtlJobSucceeds() throws IOException {
        Path src = createPdf(4);
        String yaml = """
                quire_version: "1.0"
                jobs:
                  - name: "rtl"
                    source: "%s"
                    output: "./out_rtl.pdf"
                    technique: saddle_stitch
                    reading_direction: rtl
                    paper_size: a4
                """.formatted(src.getFileName());
        BatchConfig cfg = QuireFileParser.parseString(yaml);
        List<JobResult> results = BatchRunner.run(cfg, tempDir, false);
        assertTrue(results.get(0).success(), results.get(0).message());
    }

    @Test
    void groupCJobWithSignatureSizeSucceeds() throws IOException {
        Path src = createPdf(16);
        String yaml = """
                quire_version: "1.0"
                jobs:
                  - name: "sewn"
                    source: "%s"
                    output: "./out_sewn.pdf"
                    technique: sewn_signatures
                    signature_size: 4
                    paper_size: a4
                """.formatted(src.getFileName());
        BatchConfig cfg = QuireFileParser.parseString(yaml);
        List<JobResult> results = BatchRunner.run(cfg, tempDir, false);
        assertTrue(results.get(0).success(), results.get(0).message());
    }

    @Test
    void failedJobDoesNotBlockSubsequentJobs() throws IOException {
        Path src = createPdf(4);
        String yaml = """
                quire_version: "1.0"
                jobs:
                  - name: "bad"
                    source: "./missing.pdf"
                    output: "./out.pdf"
                    technique: saddle_stitch
                  - name: "good"
                    source: "%s"
                    output: "./out2.pdf"
                    technique: saddle_stitch
                """.formatted(src.getFileName());
        BatchConfig cfg = QuireFileParser.parseString(yaml);
        List<JobResult> results = BatchRunner.run(cfg, tempDir, false);
        assertFalse(results.get(0).success());
        assertTrue(results.get(1).success(), results.get(1).message());
    }
}
