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
package com.maiitsoh.quirebind.desktop;

import com.maiitsoh.quirebind.core.model.BindingTechnique;
import com.maiitsoh.quirebind.core.model.PaperSize;
import com.maiitsoh.quirebind.core.model.ReadingDirection;
import com.maiitsoh.quirebind.desktop.state.WizardState;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WizardStateTest {

    @Test
    void defaultsAreSet() {
        WizardState state = new WizardState();
        assertEquals(BindingTechnique.SADDLE_STITCH, state.getTechnique());
        assertEquals(PaperSize.A4, state.getPaperSize());
        assertEquals(16, state.getPagesPerSignature());
        assertEquals(ReadingDirection.LTR, state.getReadingDirection());
        assertEquals(0.0, state.getPaperThicknessMm());
        assertFalse(state.hasInputPdf());
        assertFalse(state.hasImpositionResult());
        assertNull(state.getOutputPdf());
    }

    @Test
    void settersRoundTrip() {
        WizardState state = new WizardState();
        Path path = Path.of("/tmp/test.pdf");
        state.setInputPdf(path);
        state.setPageCount(20);
        state.setTechnique(BindingTechnique.COPTIC);
        state.setPaperSize(PaperSize.A5);
        state.setPagesPerSignature(8);
        state.setReadingDirection(ReadingDirection.RTL);
        state.setPaperThicknessMm(0.12);

        assertEquals(path, state.getInputPdf());
        assertEquals(20, state.getPageCount());
        assertEquals(BindingTechnique.COPTIC, state.getTechnique());
        assertEquals(PaperSize.A5, state.getPaperSize());
        assertEquals(8, state.getPagesPerSignature());
        assertEquals(ReadingDirection.RTL, state.getReadingDirection());
        assertEquals(0.12, state.getPaperThicknessMm());
        assertTrue(state.hasInputPdf());
    }
}
