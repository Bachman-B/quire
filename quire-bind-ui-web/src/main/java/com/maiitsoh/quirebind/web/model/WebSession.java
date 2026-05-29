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
package com.maiitsoh.quirebind.web.model;

import com.maiitsoh.quirebind.core.model.BindingTechnique;
import com.maiitsoh.quirebind.core.model.FolioPosition;
import com.maiitsoh.quirebind.core.model.FolioStyle;
import com.maiitsoh.quirebind.core.model.PaperSize;
import com.maiitsoh.quirebind.core.model.ReadingDirection;
import com.maiitsoh.quirebind.core.model.Signature;

import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Holds all wizard state for a single browser session. */
@Component
@SessionScope
public class WebSession {

    private Path sourcePdf;
    private String originalFilename;

    private BindingTechnique technique = BindingTechnique.SEWN_SIGNATURES;
    private PaperSize paperSize = PaperSize.A4;
    private ReadingDirection readingDirection = ReadingDirection.LTR;
    private int signatureSize = 4;

    private int frontMatterPageCount = 0;
    private int rearMatterPageCount = 0;

    private FolioStyle frontMatterFolioStyle = FolioStyle.NONE;
    private FolioStyle bodyFolioStyle = FolioStyle.ARABIC;
    private FolioStyle rearMatterFolioStyle = FolioStyle.NONE;
    private int frontMatterStartNumber = 1;
    private int bodyStartNumber = 1;
    private int rearMatterStartNumber = 1;
    private boolean suppressFirstFolio = false;
    private FolioPosition folioPosition = FolioPosition.BOTTOM_OUTER;

    private boolean foldLines = false;
    private boolean stitchMarks = false;
    private boolean sewingHoles = false;
    private boolean trimLines = false;

    private List<Signature> impositionResult;

    /** Deletes the temp PDF upload when the session expires. */
    @PreDestroy
    public void cleanup() {
        if (sourcePdf != null) {
            try {
                Files.deleteIfExists(sourcePdf);
            } catch (IOException ignored) {
                // best-effort cleanup
            }
        }
    }

    /** Returns true if a PDF has been uploaded and imposition has been computed. */
    public boolean hasImpositionResult() {
        return impositionResult != null && !impositionResult.isEmpty();
    }

    /** Returns the path to the uploaded source PDF, or null if not yet uploaded. */
    public Path getSourcePdf() {
        return sourcePdf;
    }

    /** Sets the path to the uploaded source PDF, deleting any previous temp file. */
    public void setSourcePdf(Path sourcePdf) {
        if (this.sourcePdf != null && !this.sourcePdf.equals(sourcePdf)) {
            try {
                Files.deleteIfExists(this.sourcePdf);
            } catch (IOException ignored) {
                // best-effort
            }
        }
        this.sourcePdf = sourcePdf;
    }

    /** Returns the original filename of the uploaded PDF. */
    public String getOriginalFilename() {
        return originalFilename;
    }

    /** Sets the original filename of the uploaded PDF. */
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    /** Returns the selected binding technique. */
    public BindingTechnique getTechnique() {
        return technique;
    }

    /** Sets the binding technique. */
    public void setTechnique(BindingTechnique technique) {
        this.technique = technique;
    }

    /** Returns the selected output paper size. */
    public PaperSize getPaperSize() {
        return paperSize;
    }

    /** Sets the output paper size. */
    public void setPaperSize(PaperSize paperSize) {
        this.paperSize = paperSize;
    }

    /** Returns the reading direction. */
    public ReadingDirection getReadingDirection() {
        return readingDirection;
    }

    /** Sets the reading direction. */
    public void setReadingDirection(ReadingDirection readingDirection) {
        this.readingDirection = readingDirection;
    }

    /** Returns the signature size (sheets per signature). */
    public int getSignatureSize() {
        return signatureSize;
    }

    /** Sets the signature size. */
    public void setSignatureSize(int signatureSize) {
        this.signatureSize = signatureSize;
    }

    /** Returns the number of front-matter pages. */
    public int getFrontMatterPageCount() {
        return frontMatterPageCount;
    }

    /** Sets the number of front-matter pages. */
    public void setFrontMatterPageCount(int frontMatterPageCount) {
        this.frontMatterPageCount = frontMatterPageCount;
    }

    /** Returns the number of rear-matter pages. */
    public int getRearMatterPageCount() {
        return rearMatterPageCount;
    }

    /** Sets the number of rear-matter pages. */
    public void setRearMatterPageCount(int rearMatterPageCount) {
        this.rearMatterPageCount = rearMatterPageCount;
    }

    /** Returns the folio style for front matter. */
    public FolioStyle getFrontMatterFolioStyle() {
        return frontMatterFolioStyle;
    }

    /** Sets the folio style for front matter. */
    public void setFrontMatterFolioStyle(FolioStyle frontMatterFolioStyle) {
        this.frontMatterFolioStyle = frontMatterFolioStyle;
    }

    /** Returns the folio style for the body. */
    public FolioStyle getBodyFolioStyle() {
        return bodyFolioStyle;
    }

    /** Sets the folio style for the body. */
    public void setBodyFolioStyle(FolioStyle bodyFolioStyle) {
        this.bodyFolioStyle = bodyFolioStyle;
    }

    /** Returns the folio style for rear matter. */
    public FolioStyle getRearMatterFolioStyle() {
        return rearMatterFolioStyle;
    }

    /** Sets the folio style for rear matter. */
    public void setRearMatterFolioStyle(FolioStyle rearMatterFolioStyle) {
        this.rearMatterFolioStyle = rearMatterFolioStyle;
    }

    /** Returns the start number for front-matter folios. */
    public int getFrontMatterStartNumber() {
        return frontMatterStartNumber;
    }

    /** Sets the start number for front-matter folios. */
    public void setFrontMatterStartNumber(int frontMatterStartNumber) {
        this.frontMatterStartNumber = frontMatterStartNumber;
    }

    /** Returns the start number for body folios. */
    public int getBodyStartNumber() {
        return bodyStartNumber;
    }

    /** Sets the start number for body folios. */
    public void setBodyStartNumber(int bodyStartNumber) {
        this.bodyStartNumber = bodyStartNumber;
    }

    /** Returns the start number for rear-matter folios. */
    public int getRearMatterStartNumber() {
        return rearMatterStartNumber;
    }

    /** Sets the start number for rear-matter folios. */
    public void setRearMatterStartNumber(int rearMatterStartNumber) {
        this.rearMatterStartNumber = rearMatterStartNumber;
    }

    /** Returns whether the first body folio is suppressed. */
    public boolean isSuppressFirstFolio() {
        return suppressFirstFolio;
    }

    /** Sets whether to suppress the first body folio. */
    public void setSuppressFirstFolio(boolean suppressFirstFolio) {
        this.suppressFirstFolio = suppressFirstFolio;
    }

    /** Returns the folio position. */
    public FolioPosition getFolioPosition() {
        return folioPosition;
    }

    /** Sets the folio position. */
    public void setFolioPosition(FolioPosition folioPosition) {
        this.folioPosition = folioPosition;
    }

    /** Returns whether fold lines should be printed. */
    public boolean isFoldLines() {
        return foldLines;
    }

    /** Sets whether fold lines should be printed. */
    public void setFoldLines(boolean foldLines) {
        this.foldLines = foldLines;
    }

    /** Returns whether stitch marks should be printed. */
    public boolean isStitchMarks() {
        return stitchMarks;
    }

    /** Sets whether stitch marks should be printed. */
    public void setStitchMarks(boolean stitchMarks) {
        this.stitchMarks = stitchMarks;
    }

    /** Returns whether sewing holes should be printed. */
    public boolean isSewingHoles() {
        return sewingHoles;
    }

    /** Sets whether sewing holes should be printed. */
    public void setSewingHoles(boolean sewingHoles) {
        this.sewingHoles = sewingHoles;
    }

    /** Returns whether trim lines should be printed. */
    public boolean isTrimLines() {
        return trimLines;
    }

    /** Sets whether trim lines should be printed. */
    public void setTrimLines(boolean trimLines) {
        this.trimLines = trimLines;
    }

    /** Returns the computed imposition result, or null if not yet computed. */
    public List<Signature> getImpositionResult() {
        return impositionResult;
    }

    /** Stores the computed imposition result. */
    public void setImpositionResult(List<Signature> impositionResult) {
        this.impositionResult = impositionResult;
    }
}
