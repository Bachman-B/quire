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
package com.maiitsoh.quirebind.desktop.state;

import com.maiitsoh.quirebind.core.model.BindingTechnique;
import com.maiitsoh.quirebind.core.model.PaperSize;
import com.maiitsoh.quirebind.core.model.ReadingDirection;
import com.maiitsoh.quirebind.core.model.Signature;

import java.nio.file.Path;
import java.util.List;

/** Mutable state shared across wizard steps within a single session. */
public final class WizardState {

    private Path inputPdf;
    private int pageCount;
    private BindingTechnique technique = BindingTechnique.SADDLE_STITCH;
    private PaperSize paperSize = PaperSize.A4;
    private int pagesPerSignature = 16;
    private ReadingDirection readingDirection = ReadingDirection.LTR;
    private double paperThicknessMm = 0.0;
    private List<Signature> impositionResult;
    private Path outputPdf;

    /** Returns the source PDF path, or {@code null} if none has been chosen. */
    public Path getInputPdf() {
        return inputPdf;
    }

    /** Sets the source PDF path. */
    public void setInputPdf(Path inputPdf) {
        this.inputPdf = inputPdf;
    }

    /** Returns the number of pages loaded from the source PDF. */
    public int getPageCount() {
        return pageCount;
    }

    /** Sets the page count after loading the source PDF. */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /** Returns the chosen binding technique. */
    public BindingTechnique getTechnique() {
        return technique;
    }

    /** Sets the binding technique. */
    public void setTechnique(BindingTechnique technique) {
        this.technique = technique;
    }

    /** Returns the chosen output paper size. */
    public PaperSize getPaperSize() {
        return paperSize;
    }

    /** Sets the output paper size. */
    public void setPaperSize(PaperSize paperSize) {
        this.paperSize = paperSize;
    }

    /** Returns the number of pages per signature. */
    public int getPagesPerSignature() {
        return pagesPerSignature;
    }

    /** Sets the number of pages per signature. */
    public void setPagesPerSignature(int pagesPerSignature) {
        this.pagesPerSignature = pagesPerSignature;
    }

    /** Returns the reading direction. */
    public ReadingDirection getReadingDirection() {
        return readingDirection;
    }

    /** Sets the reading direction. */
    public void setReadingDirection(ReadingDirection readingDirection) {
        this.readingDirection = readingDirection;
    }

    /** Returns the paper thickness in mm used for creep calculation, or {@code 0.0} to skip. */
    public double getPaperThicknessMm() {
        return paperThicknessMm;
    }

    /** Sets the paper thickness in mm. */
    public void setPaperThicknessMm(double paperThicknessMm) {
        this.paperThicknessMm = paperThicknessMm;
    }

    /** Returns the computed imposition result, or {@code null} if imposition has not run yet. */
    public List<Signature> getImpositionResult() {
        return impositionResult;
    }

    /** Stores the computed imposition result. */
    public void setImpositionResult(List<Signature> impositionResult) {
        this.impositionResult = impositionResult;
    }

    /** Returns the chosen output PDF path, or {@code null} if none has been chosen. */
    public Path getOutputPdf() {
        return outputPdf;
    }

    /** Sets the output PDF path. */
    public void setOutputPdf(Path outputPdf) {
        this.outputPdf = outputPdf;
    }

    /** Returns {@code true} if a source PDF has been chosen. */
    public boolean hasInputPdf() {
        return inputPdf != null;
    }

    /** Returns {@code true} if imposition has already been computed. */
    public boolean hasImpositionResult() {
        return impositionResult != null;
    }
}
