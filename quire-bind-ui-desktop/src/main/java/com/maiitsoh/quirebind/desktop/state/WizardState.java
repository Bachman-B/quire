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

import com.maiitsoh.quirebind.batch.model.BatchConfig;
import com.maiitsoh.quirebind.core.model.BindingTechnique;
import com.maiitsoh.quirebind.core.model.PageSequence;
import com.maiitsoh.quirebind.core.model.PaperSize;
import com.maiitsoh.quirebind.core.model.ReadingDirection;
import com.maiitsoh.quirebind.core.model.Signature;

import java.nio.file.Path;
import java.util.List;

/** Mutable state shared across wizard steps within a single session. */
public final class WizardState {

    /** Whether the user is working with a single PDF or a batch .quire file. */
    public enum WizardMode {
        SINGLE_PDF,
        BATCH
    }

    private WizardMode mode = WizardMode.SINGLE_PDF;

    // Single PDF mode
    private Path inputPdf;
    private int pageCount;
    private PageSequence pageSequence;

    // Batch mode
    private Path batchConfigPath;
    private BatchConfig batchConfig;

    // Binding options (shared)
    private BindingTechnique technique = BindingTechnique.SADDLE_STITCH;
    private PaperSize paperSize = PaperSize.A4;
    private int pagesPerSignature = 16;
    private ReadingDirection readingDirection = ReadingDirection.LTR;
    private double paperThicknessMm = 0.0;

    // Output marks
    private boolean foldLines = false;
    private boolean stitchMarks = false;
    private boolean sewingHoles = false;
    private boolean trimLines = false;

    // Imposition result
    private List<Signature> impositionResult;

    // Export
    private Path outputPdf;

    /** Returns the current wizard mode. */
    public WizardMode getMode() {
        return mode;
    }

    /** Sets the wizard mode. */
    public void setMode(WizardMode mode) {
        this.mode = mode;
    }

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

    /** Returns the mutable page sequence, or {@code null} if not yet loaded. */
    public PageSequence getPageSequence() {
        return pageSequence;
    }

    /** Sets the page sequence loaded from the source PDF. */
    public void setPageSequence(PageSequence pageSequence) {
        this.pageSequence = pageSequence;
    }

    /** Returns the path to the .quire batch config file. */
    public Path getBatchConfigPath() {
        return batchConfigPath;
    }

    /** Sets the .quire batch config file path. */
    public void setBatchConfigPath(Path batchConfigPath) {
        this.batchConfigPath = batchConfigPath;
    }

    /** Returns the parsed batch configuration, or {@code null} if not yet loaded. */
    public BatchConfig getBatchConfig() {
        return batchConfig;
    }

    /** Sets the parsed batch configuration. */
    public void setBatchConfig(BatchConfig batchConfig) {
        this.batchConfig = batchConfig;
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

    /** Returns the number of pages per signature (relevant for group C techniques). */
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

    /** Returns whether fold lines should be printed on the output. */
    public boolean isFoldLines() {
        return foldLines;
    }

    /** Sets whether fold lines should be printed. */
    public void setFoldLines(boolean foldLines) {
        this.foldLines = foldLines;
    }

    /** Returns whether stitch marks should be printed on the output. */
    public boolean isStitchMarks() {
        return stitchMarks;
    }

    /** Sets whether stitch marks should be printed. */
    public void setStitchMarks(boolean stitchMarks) {
        this.stitchMarks = stitchMarks;
    }

    /** Returns whether sewing holes should be marked on the output. */
    public boolean isSewingHoles() {
        return sewingHoles;
    }

    /** Sets whether sewing holes should be marked. */
    public void setSewingHoles(boolean sewingHoles) {
        this.sewingHoles = sewingHoles;
    }

    /** Returns whether trim lines should be printed on the output. */
    public boolean isTrimLines() {
        return trimLines;
    }

    /** Sets whether trim lines should be printed. */
    public void setTrimLines(boolean trimLines) {
        this.trimLines = trimLines;
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

    /** Returns {@code true} if a .quire batch config has been loaded. */
    public boolean hasBatchConfig() {
        return batchConfig != null;
    }

    /** Returns {@code true} if imposition has already been computed. */
    public boolean hasImpositionResult() {
        return impositionResult != null;
    }

    /** Clears all state, resetting to defaults for a new project. */
    public void reset() {
        mode = WizardMode.SINGLE_PDF;
        inputPdf = null;
        pageCount = 0;
        pageSequence = null;
        batchConfigPath = null;
        batchConfig = null;
        technique = BindingTechnique.SADDLE_STITCH;
        paperSize = PaperSize.A4;
        pagesPerSignature = 16;
        readingDirection = ReadingDirection.LTR;
        paperThicknessMm = 0.0;
        foldLines = false;
        stitchMarks = false;
        sewingHoles = false;
        trimLines = false;
        impositionResult = null;
        outputPdf = null;
    }
}
