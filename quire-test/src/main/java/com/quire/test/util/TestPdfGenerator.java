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
package com.quire.test.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Generates test PDFs with one large page number per page for use in system tests.
 *
 * <p>Each page is A4 portrait and displays its 1-based page number at a fixed location
 * in 72-point Helvetica Bold — large enough to survive scaling and be reliably extracted
 * by PDFBox's text stripper.
 */
public final class TestPdfGenerator {

    private TestPdfGenerator() {
    }

    /**
     * Creates a PDF with {@code pageCount} numbered A4 pages and saves it to {@code outputPath}.
     *
     * @param pageCount  number of pages to generate; must be positive
     * @param outputPath destination path; must not be null
     * @return {@code outputPath}, as a convenience for chaining
     * @throws IOException              if the file cannot be written
     * @throws IllegalArgumentException if {@code pageCount} is not positive
     */
    public static Path generate(int pageCount, Path outputPath) throws IOException {
        if (pageCount <= 0) {
            throw new IllegalArgumentException("pageCount must be positive");
        }
        try (PDDocument doc = new PDDocument()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            for (int i = 0; i < pageCount; i++) {
                PDPage page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    cs.beginText();
                    cs.setFont(font, 72);
                    cs.newLineAtOffset(200, 400);
                    cs.showText(String.valueOf(i + 1));
                    cs.endText();
                }
            }
            doc.save(outputPath.toFile());
        }
        return outputPath;
    }
}
