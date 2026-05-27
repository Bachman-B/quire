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
package com.maiitsoh.quirebind.test.system;

import com.maiitsoh.quirebind.core.creep.CreepCalculator;
import com.maiitsoh.quirebind.core.imposition.ImpositionEngine;
import com.maiitsoh.quirebind.core.model.BindingTechnique;
import com.maiitsoh.quirebind.core.model.CreepConfig;
import com.maiitsoh.quirebind.core.model.ImpositionLayout;
import com.maiitsoh.quirebind.core.model.MarkConfig;
import com.maiitsoh.quirebind.core.model.NumberingConfig;
import com.maiitsoh.quirebind.core.model.PaddingConfig;
import com.maiitsoh.quirebind.core.model.PaperSize;
import com.maiitsoh.quirebind.core.model.QuireProject;
import com.maiitsoh.quirebind.core.model.ReadingDirection;
import com.maiitsoh.quirebind.core.model.Signature;
import com.maiitsoh.quirebind.core.pdf.PdfImpositionWriter;
import com.maiitsoh.quirebind.core.pdf.PdfPageLoader;
import com.maiitsoh.quirebind.test.util.TestPdfGenerator;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the creep calculation pipeline.
 *
 * <p>Verifies that creep offsets are correctly calculated for different binding techniques
 * and that the output PDF is valid after the full pipeline runs.
 */
class CreepIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void saddleStitchEightPagesCreepCalculated() throws IOException {
        Path src = TestPdfGenerator.generate(8, tempDir.resolve("src.pdf"));
        QuireProject project = QuireProject.builder()
                .name("Creep Test")
                .bindingTechnique(BindingTechnique.SADDLE_STITCH)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(PdfPageLoader.load(src))
                .paddingConfig(PaddingConfig.builder().build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().paperThicknessMm(0.1).build())
                .build();

        List<Signature> signatures = ImpositionEngine.impose(project);
        CreepConfig result = CreepCalculator.calculate(signatures, project.getCreepConfig());

        assertTrue(result.getCalculatedCreepMm().isPresent());
        // 2 sheets: sheet 0 = 0.0 mm, sheet 1 = 0.2 mm; max = 0.2 mm
        assertEquals(0.2, result.getCalculatedCreepMm().get(), 1e-9);
        assertEquals(2, result.getSheetResults().size());
    }

    @Test
    void perfectBindingEightPagesTwoSigsNoCreep() throws IOException {
        Path src = TestPdfGenerator.generate(8, tempDir.resolve("src.pdf"));
        QuireProject project = QuireProject.builder()
                .name("Group A Creep Test")
                .bindingTechnique(BindingTechnique.PERFECT_BINDING)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(PdfPageLoader.load(src))
                .paddingConfig(PaddingConfig.builder().build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().paperThicknessMm(0.1).build())
                .build();

        List<Signature> signatures = ImpositionEngine.impose(project);
        // Group A: 8 pages → 2 sigs × 1 sheet each.
        // Each sheet is the only (outermost) sheet in its signature, so sheetIndex=0 and creep=0.
        CreepConfig result = CreepCalculator.calculate(signatures, project.getCreepConfig());

        assertEquals(2, result.getSheetResults().size());
        assertEquals(0.0, result.getSheetResults().get(0).getCreepMm(), 1e-9);
        assertEquals(0.0, result.getSheetResults().get(1).getCreepMm(), 1e-9);
        assertEquals(0.0, result.getCalculatedCreepMm().orElseThrow(), 1e-9);
    }

    @Test
    void gsmBasedCreepCalculation() throws IOException {
        Path src = TestPdfGenerator.generate(8, tempDir.resolve("src.pdf"));
        QuireProject project = QuireProject.builder()
                .name("GSM Creep Test")
                .bindingTechnique(BindingTechnique.SADDLE_STITCH)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(PdfPageLoader.load(src))
                .paddingConfig(PaddingConfig.builder().build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().paperWeightGsm(80.0).build())
                .build();

        List<Signature> signatures = ImpositionEngine.impose(project);
        CreepConfig result = CreepCalculator.calculate(signatures, project.getCreepConfig());

        // 80 gsm × 0.00125 = 0.1 mm; 2 sheets → max creep = 0.0 + 0.2 = 0.2 mm
        assertTrue(result.getCalculatedCreepMm().isPresent());
        assertEquals(0.2, result.getCalculatedCreepMm().get(), 1e-9);
    }

    @Test
    void creepConfiguredPipelineProducesValidOutputPdf() throws IOException {
        Path src = TestPdfGenerator.generate(8, tempDir.resolve("src.pdf"));
        Path out = tempDir.resolve("out.pdf");
        QuireProject project = QuireProject.builder()
                .name("Creep Output Test")
                .bindingTechnique(BindingTechnique.SADDLE_STITCH)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(PdfPageLoader.load(src))
                .paddingConfig(PaddingConfig.builder().build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().paperThicknessMm(0.1).build())
                .build();

        List<Signature> signatures = ImpositionEngine.impose(project);
        PdfImpositionWriter.write(signatures, src, out, PaperSize.A4);

        try (PDDocument doc = Loader.loadPDF(out.toFile())) {
            assertEquals(4, doc.getNumberOfPages());
        }
    }

    @Test
    void sewnSignaturesThirtyTwoPagesCreepAcrossSignatures() throws IOException {
        Path src = TestPdfGenerator.generate(32, tempDir.resolve("src.pdf"));
        // signatureSize=4 → 16 pages per sig → 4 sheets per sig → 2 sigs → 8 total sheets
        QuireProject project = QuireProject.builder()
                .name("Group C Creep Test")
                .bindingTechnique(BindingTechnique.SEWN_SIGNATURES)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(PdfPageLoader.load(src))
                .paddingConfig(PaddingConfig.builder().signatureSize(4).build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().paperThicknessMm(0.1).build())
                .build();

        List<Signature> signatures = ImpositionEngine.impose(project);
        CreepConfig result = CreepCalculator.calculate(signatures, project.getCreepConfig());

        assertTrue(result.getCalculatedCreepMm().isPresent());
        // 8 total sheets; innermost (index 7) → 7 × 2 × 0.1 = 1.4 mm
        assertFalse(result.getSheetResults().isEmpty());
        assertEquals(0.0, result.getSheetResults().get(0).getCreepMm(), 1e-9);
    }
}
