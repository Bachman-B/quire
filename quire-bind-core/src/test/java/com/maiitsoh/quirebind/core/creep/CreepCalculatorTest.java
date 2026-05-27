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
package com.maiitsoh.quirebind.core.creep;

import com.maiitsoh.quirebind.core.model.BindingTechnique;
import com.maiitsoh.quirebind.core.model.CreepConfig;
import com.maiitsoh.quirebind.core.model.CreepSheetResult;
import com.maiitsoh.quirebind.core.model.ImpositionLayout;
import com.maiitsoh.quirebind.core.model.MarkConfig;
import com.maiitsoh.quirebind.core.model.NumberingConfig;
import com.maiitsoh.quirebind.core.model.PaddingConfig;
import com.maiitsoh.quirebind.core.model.PaperSize;
import com.maiitsoh.quirebind.core.model.PageSequence;
import com.maiitsoh.quirebind.core.model.PageType;
import com.maiitsoh.quirebind.core.model.QuirePage;
import com.maiitsoh.quirebind.core.model.QuireProject;
import com.maiitsoh.quirebind.core.model.ReadingDirection;
import com.maiitsoh.quirebind.core.model.Signature;
import com.maiitsoh.quirebind.core.imposition.ImpositionEngine;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreepCalculatorTest {

    private static QuirePage content(int pos) {
        return QuirePage.builder().physicalPosition(pos).pageType(PageType.CONTENT)
                .logicalPageNumber(pos + 1).build();
    }

    private static List<Signature> fourPageSig() {
        PageSequence seq = new PageSequence();
        for (int i = 0; i < 4; i++) {
            seq.insertPage(i, content(i));
        }
        QuireProject project = QuireProject.builder()
                .name("Test")
                .bindingTechnique(BindingTechnique.SADDLE_STITCH)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(seq)
                .paddingConfig(PaddingConfig.builder().build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().build())
                .build();
        return ImpositionEngine.impose(project);
    }

    private static List<Signature> eightPageSig() {
        PageSequence seq = new PageSequence();
        for (int i = 0; i < 8; i++) {
            seq.insertPage(i, content(i));
        }
        QuireProject project = QuireProject.builder()
                .name("Test")
                .bindingTechnique(BindingTechnique.SADDLE_STITCH)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(seq)
                .paddingConfig(PaddingConfig.builder().build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().build())
                .build();
        return ImpositionEngine.impose(project);
    }

    @Test
    void nullSignaturesThrows() {
        assertThrows(NullPointerException.class,
                () -> CreepCalculator.calculate(null, CreepConfig.builder().build()));
    }

    @Test
    void nullConfigThrows() {
        assertThrows(NullPointerException.class,
                () -> CreepCalculator.calculate(List.of(), null));
    }

    @Test
    void noPaperInfoReturnsConfigUnchanged() {
        CreepConfig config = CreepConfig.builder().build();
        CreepConfig result = CreepCalculator.calculate(fourPageSig(), config);
        assertSame(config, result);
    }

    @Test
    void directThicknessUsed() {
        CreepConfig config = CreepConfig.builder().paperThicknessMm(0.1).build();
        CreepConfig result = CreepCalculator.calculate(fourPageSig(), config);
        assertTrue(result.getCalculatedCreepMm().isPresent());
    }

    @Test
    void gsmDerivedThickness() {
        CreepConfig config = CreepConfig.builder().paperWeightGsm(80.0).build();
        CreepConfig result = CreepCalculator.calculate(fourPageSig(), config);
        assertTrue(result.getCalculatedCreepMm().isPresent());
        assertEquals(0.0, result.getCalculatedCreepMm().get(), 1e-9);
    }

    @Test
    void fourPageOneSheetOutermostZeroCreep() {
        CreepConfig config = CreepConfig.builder().paperThicknessMm(0.1).build();
        CreepConfig result = CreepCalculator.calculate(fourPageSig(), config);
        assertEquals(1, result.getSheetResults().size());
        assertEquals(0.0, result.getSheetResults().get(0).getCreepMm(), 1e-9);
    }

    @Test
    void eightPageTwoSheetsCreepValues() {
        CreepConfig config = CreepConfig.builder().paperThicknessMm(0.1).build();
        CreepConfig result = CreepCalculator.calculate(eightPageSig(), config);
        List<CreepSheetResult> results = result.getSheetResults();
        assertEquals(2, results.size());
        assertEquals(0.0, results.get(0).getCreepMm(), 1e-9);
        assertEquals(0.2, results.get(1).getCreepMm(), 1e-9);
    }

    @Test
    void calculatedCreepMmIsMaximum() {
        CreepConfig config = CreepConfig.builder().paperThicknessMm(0.1).build();
        CreepConfig result = CreepCalculator.calculate(eightPageSig(), config);
        assertEquals(0.2, result.getCalculatedCreepMm().orElseThrow(), 1e-9);
    }

    @Test
    void emptySignaturesZeroCreep() {
        CreepConfig config = CreepConfig.builder().paperThicknessMm(0.1).build();
        CreepConfig result = CreepCalculator.calculate(List.of(), config);
        assertEquals(0.0, result.getCalculatedCreepMm().orElseThrow(), 1e-9);
        assertTrue(result.getSheetResults().isEmpty());
    }

    @Test
    void sheetResultPageNumbersPopulated() {
        CreepConfig config = CreepConfig.builder().paperThicknessMm(0.1).build();
        CreepConfig result = CreepCalculator.calculate(fourPageSig(), config);
        assertFalse(result.getSheetResults().get(0).getPageNumbers().isEmpty());
    }

    @Test
    void unnumberedPagesProduceEmptyPageNumbers() {
        PageSequence seq = new PageSequence();
        for (int i = 0; i < 4; i++) {
            seq.insertPage(i, QuirePage.builder().physicalPosition(i)
                    .pageType(PageType.CONTENT).build());
        }
        QuireProject project = QuireProject.builder()
                .name("Test")
                .bindingTechnique(BindingTechnique.SADDLE_STITCH)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(seq)
                .paddingConfig(PaddingConfig.builder().build())
                .numberingConfig(NumberingConfig.builder().bodyStyle(
                        com.maiitsoh.quirebind.core.model.FolioStyle.NONE).build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().build())
                .build();
        List<Signature> sigs = ImpositionEngine.impose(project);
        CreepConfig config = CreepConfig.builder().paperThicknessMm(0.1).build();
        CreepConfig result = CreepCalculator.calculate(sigs, config);
        assertTrue(result.getSheetResults().get(0).getPageNumbers().isEmpty());
    }

    @Test
    void gsmFactorCorrect() {
        assertEquals(0.1, 80.0 * CreepCalculator.GSM_TO_MM, 1e-9);
    }

    @Test
    void sheetIndexPreservedInResult() {
        CreepConfig config = CreepConfig.builder().paperThicknessMm(0.1).build();
        CreepConfig result = CreepCalculator.calculate(eightPageSig(), config);
        assertEquals(0, result.getSheetResults().get(0).getSheetIndex());
        assertEquals(1, result.getSheetResults().get(1).getSheetIndex());
    }
}
