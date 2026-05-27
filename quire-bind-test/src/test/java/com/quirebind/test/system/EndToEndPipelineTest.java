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
package com.quirebind.test.system;

import com.quirebind.core.imposition.ImpositionEngine;
import com.quirebind.core.model.BindingTechnique;
import com.quirebind.core.model.CreepConfig;
import com.quirebind.core.model.ImpositionLayout;
import com.quirebind.core.model.MarkConfig;
import com.quirebind.core.model.NumberingConfig;
import com.quirebind.core.model.PaddingConfig;
import com.quirebind.core.model.PaperSize;
import com.quirebind.core.model.QuireProject;
import com.quirebind.core.model.ReadingDirection;
import com.quirebind.core.model.Signature;
import com.quirebind.core.pdf.PdfImpositionWriter;
import com.quirebind.core.pdf.PdfPageLoader;
import com.quirebind.test.util.TestPdfGenerator;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * End-to-end system tests for the full imposition pipeline.
 *
 * <p>Covers all nine binding techniques (three imposition groups), verifying that:
 * <ul>
 *   <li>The output PDF is created and valid</li>
 *   <li>The output has the correct number of pages</li>
 *   <li>Each output page is a landscape sheet with width = 2 × book page width</li>
 * </ul>
 */
class EndToEndPipelineTest {

    @TempDir
    Path tempDir;

    static Stream<Arguments> pipelineScenarios() {
        return Stream.of(
                // Group A: pagesPerSig=4 — each sig = 1 sheet = 2 output PDF pages
                Arguments.of(BindingTechnique.PERFECT_BINDING, 4, 2),
                Arguments.of(BindingTechnique.PERFECT_BINDING, 8, 4),
                Arguments.of(BindingTechnique.PERFECT_BINDING, 12, 6),
                Arguments.of(BindingTechnique.SPIRAL, 4, 2),
                Arguments.of(BindingTechnique.JAPANESE_STAB, 8, 4),
                // Group B: all pages in one sig
                Arguments.of(BindingTechnique.SADDLE_STITCH, 4, 2),
                Arguments.of(BindingTechnique.SADDLE_STITCH, 8, 4),
                Arguments.of(BindingTechnique.SADDLE_STITCH, 16, 8),
                Arguments.of(BindingTechnique.PAMPHLET, 4, 2),
                Arguments.of(BindingTechnique.BOOKLET, 8, 4),
                // Group C: signatureSize=4 → 16 pages per sig
                Arguments.of(BindingTechnique.SEWN_SIGNATURES, 16, 8),
                Arguments.of(BindingTechnique.HARDCOVER, 32, 16),
                Arguments.of(BindingTechnique.COPTIC, 16, 8)
        );
    }

    @ParameterizedTest(name = "{0} — {1} pages → {2} output pages")
    @MethodSource("pipelineScenarios")
    void fullPipelineProducesCorrectOutputPageCount(
            BindingTechnique technique, int sourcePageCount, int expectedOutputPages)
            throws IOException {
        // signatureSize=4 is needed by Group C; Group A/B ignore it
        int sigSize = technique.group() == com.quirebind.core.model.ImpositionGroup.C ? 4 : 0;
        Path src = TestPdfGenerator.generate(sourcePageCount, tempDir.resolve("src.pdf"));
        Path out = tempDir.resolve("out_" + technique + "_" + sourcePageCount + ".pdf");

        QuireProject project = QuireProject.builder()
                .name("System Test")
                .bindingTechnique(technique)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(PdfPageLoader.load(src))
                .paddingConfig(PaddingConfig.builder().signatureSize(sigSize).build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().build())
                .build();

        List<Signature> signatures = ImpositionEngine.impose(project);
        PdfImpositionWriter.write(signatures, src, out, PaperSize.A4);

        try (PDDocument doc = Loader.loadPDF(out.toFile())) {
            assertEquals(expectedOutputPages, doc.getNumberOfPages());
        }
    }

    @ParameterizedTest(name = "{0} — output page is landscape double-wide")
    @MethodSource("pipelineScenarios")
    void outputPageIsLandscapeDoubleWide(
            BindingTechnique technique, int sourcePageCount, int expectedOutputPages)
            throws IOException {
        int sigSize = technique.group() == com.quirebind.core.model.ImpositionGroup.C ? 4 : 0;
        Path src = TestPdfGenerator.generate(sourcePageCount, tempDir.resolve("src_dim.pdf"));
        Path out = tempDir.resolve("out_dim_" + technique + ".pdf");

        QuireProject project = QuireProject.builder()
                .name("Dimension Test")
                .bindingTechnique(technique)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(PdfPageLoader.load(src))
                .paddingConfig(PaddingConfig.builder().signatureSize(sigSize).build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().build())
                .build();

        List<Signature> signatures = ImpositionEngine.impose(project);
        PdfImpositionWriter.write(signatures, src, out, PaperSize.A4);

        try (PDDocument doc = Loader.loadPDF(out.toFile())) {
            PDRectangle box = doc.getPage(0).getMediaBox();
            assertEquals(PDRectangle.A4.getWidth() * 2, box.getWidth(), 0.5f);
            assertEquals(PDRectangle.A4.getHeight(), box.getHeight(), 0.5f);
        }
    }
}
