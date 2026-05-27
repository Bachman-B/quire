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
package com.quire.test.system;

import com.quire.core.imposition.ImpositionEngine;
import com.quire.core.model.BindingTechnique;
import com.quire.core.model.CreepConfig;
import com.quire.core.model.ImpositionLayout;
import com.quire.core.model.ImposedSheet;
import com.quire.core.model.MarkConfig;
import com.quire.core.model.NumberingConfig;
import com.quire.core.model.PaddingConfig;
import com.quire.core.model.PaperSize;
import com.quire.core.model.QuireProject;
import com.quire.core.model.ReadingDirection;
import com.quire.core.model.Signature;
import com.quire.core.pdf.PdfImpositionWriter;
import com.quire.core.pdf.PdfPageLoader;
import com.quire.test.util.TestPdfGenerator;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that the folio imposition formula places source pages on the correct sheet halves
 * in the output PDF, for both LTR and RTL reading directions.
 *
 * <p>For 4 content pages in LTR order the formula requires:
 * <ul>
 *   <li>Sheet 0 front — left half: page 4 (index 3), right half: page 1 (index 0)</li>
 *   <li>Sheet 0 back — left half: page 2 (index 1), right half: page 3 (index 2)</li>
 * </ul>
 *
 * <p>For RTL:
 * <ul>
 *   <li>Sheet 0 front — left half: page 1 (index 0), right half: page 4 (index 3)</li>
 *   <li>Sheet 0 back — left half: page 3 (index 2), right half: page 2 (index 1)</li>
 * </ul>
 *
 * <p>Ordering is verified both at the structural level (source page indices on composed
 * sheets) and at the PDF content level (text extracted from left/right half regions).
 */
class FolioOrderVerificationTest {

    @TempDir
    Path tempDir;

    private QuireProject buildProject(ReadingDirection direction, Path src) throws IOException {
        return QuireProject.builder()
                .name("Order Test")
                .bindingTechnique(BindingTechnique.SADDLE_STITCH)
                .paperSize(PaperSize.A4)
                .readingDirection(direction)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(PdfPageLoader.load(src))
                .paddingConfig(PaddingConfig.builder().build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().build())
                .build();
    }

    @Test
    void ltrFolioFrontSheetStructure() throws IOException {
        Path src = TestPdfGenerator.generate(4, tempDir.resolve("src.pdf"));
        List<Signature> sigs = ImpositionEngine.impose(buildProject(ReadingDirection.LTR, src));

        ImposedSheet sheet0 = sigs.get(0).getSheets().get(0);
        // LTR front: [pages[N-1], pages[0]] = [index 3, index 0]
        assertEquals(3, sheet0.getFrontPages().get(0).getSourcePageIndex().orElseThrow());
        assertEquals(0, sheet0.getFrontPages().get(1).getSourcePageIndex().orElseThrow());
    }

    @Test
    void ltrFolioBackSheetStructure() throws IOException {
        Path src = TestPdfGenerator.generate(4, tempDir.resolve("src.pdf"));
        List<Signature> sigs = ImpositionEngine.impose(buildProject(ReadingDirection.LTR, src));

        ImposedSheet sheet0 = sigs.get(0).getSheets().get(0);
        // LTR back: [pages[1], pages[2]]
        assertEquals(1, sheet0.getBackPages().get(0).getSourcePageIndex().orElseThrow());
        assertEquals(2, sheet0.getBackPages().get(1).getSourcePageIndex().orElseThrow());
    }

    @Test
    void rtlFolioFrontSheetStructure() throws IOException {
        Path src = TestPdfGenerator.generate(4, tempDir.resolve("src.pdf"));
        List<Signature> sigs = ImpositionEngine.impose(buildProject(ReadingDirection.RTL, src));

        ImposedSheet sheet0 = sigs.get(0).getSheets().get(0);
        // RTL front: [pages[0], pages[N-1]] = [index 0, index 3]
        assertEquals(0, sheet0.getFrontPages().get(0).getSourcePageIndex().orElseThrow());
        assertEquals(3, sheet0.getFrontPages().get(1).getSourcePageIndex().orElseThrow());
    }

    @Test
    void rtlFolioBackSheetStructure() throws IOException {
        Path src = TestPdfGenerator.generate(4, tempDir.resolve("src.pdf"));
        List<Signature> sigs = ImpositionEngine.impose(buildProject(ReadingDirection.RTL, src));

        ImposedSheet sheet0 = sigs.get(0).getSheets().get(0);
        // RTL back: [pages[N-2], pages[1]] = [index 2, index 1]
        assertEquals(2, sheet0.getBackPages().get(0).getSourcePageIndex().orElseThrow());
        assertEquals(1, sheet0.getBackPages().get(1).getSourcePageIndex().orElseThrow());
    }

    @Test
    void ltrFolioFrontSheetContentInOutputPdf() throws IOException {
        Path src = TestPdfGenerator.generate(4, tempDir.resolve("src.pdf"));
        Path out = tempDir.resolve("out.pdf");
        List<Signature> sigs = ImpositionEngine.impose(buildProject(ReadingDirection.LTR, src));
        PdfImpositionWriter.write(sigs, src, out, PaperSize.A4);

        // Output page 0 is the front of sheet 0.
        // Left half should contain "4" (source page index 3), right half "1" (index 0).
        String[] halves = extractHalves(out, 0, PDRectangle.A4);
        assertEquals("4", halves[0].trim(), "LTR front left half should be page 4");
        assertEquals("1", halves[1].trim(), "LTR front right half should be page 1");
    }

    @Test
    void ltrFolioBackSheetContentInOutputPdf() throws IOException {
        Path src = TestPdfGenerator.generate(4, tempDir.resolve("src.pdf"));
        Path out = tempDir.resolve("out.pdf");
        List<Signature> sigs = ImpositionEngine.impose(buildProject(ReadingDirection.LTR, src));
        PdfImpositionWriter.write(sigs, src, out, PaperSize.A4);

        // Output page 1 is the back of sheet 0.
        // Left half: "2" (index 1), right half: "3" (index 2).
        String[] halves = extractHalves(out, 1, PDRectangle.A4);
        assertEquals("2", halves[0].trim(), "LTR back left half should be page 2");
        assertEquals("3", halves[1].trim(), "LTR back right half should be page 3");
    }

    @Test
    void rtlFolioFrontSheetContentInOutputPdf() throws IOException {
        Path src = TestPdfGenerator.generate(4, tempDir.resolve("src.pdf"));
        Path out = tempDir.resolve("out_rtl.pdf");
        List<Signature> sigs = ImpositionEngine.impose(buildProject(ReadingDirection.RTL, src));
        PdfImpositionWriter.write(sigs, src, out, PaperSize.A4);

        // RTL front: left half = page 1 (index 0), right half = page 4 (index 3).
        String[] halves = extractHalves(out, 0, PDRectangle.A4);
        assertEquals("1", halves[0].trim(), "RTL front left half should be page 1");
        assertEquals("4", halves[1].trim(), "RTL front right half should be page 4");
    }

    /**
     * Extracts text from the left and right halves of the given output page.
     *
     * @param pdfPath   path to the imposed PDF
     * @param pageIndex 0-based page index
     * @param bookRect  single-page book rectangle (half-sheet width)
     * @return two-element array: [leftHalfText, rightHalfText]
     */
    private String[] extractHalves(Path pdfPath, int pageIndex, PDRectangle bookRect)
            throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdfPath.toFile())) {
            PDPage page = doc.getPage(pageIndex);
            float halfW = bookRect.getWidth();
            float pageH = page.getMediaBox().getHeight();

            // PDFTextStripperByArea uses Java2D coordinates (top-left origin, y down).
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            stripper.addRegion("left", new Rectangle2D.Float(0, 0, halfW, pageH));
            stripper.addRegion("right", new Rectangle2D.Float(halfW, 0, halfW, pageH));
            stripper.extractRegions(page);
            return new String[]{
                    stripper.getTextForRegion("left"),
                    stripper.getTextForRegion("right")
            };
        }
    }
}
