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
package com.maiitsoh.quirebind.core.model;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class QuireProjectTest {

    private QuireProject.Builder validBuilder() {
        return QuireProject.builder()
                .name("Test Project")
                .bindingTechnique(BindingTechnique.SADDLE_STITCH)
                .paperSize(PaperSize.A4)
                .readingDirection(ReadingDirection.LTR)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(new PageSequence())
                .paddingConfig(PaddingConfig.builder().build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(CreepConfig.builder().build());
    }

    @Test
    void buildSucceeds() {
        QuireProject project = validBuilder().build();
        assertEquals("Test Project", project.getName());
        assertEquals(BindingTechnique.SADDLE_STITCH, project.getBindingTechnique());
        assertEquals(ImpositionGroup.B, project.getImpositionGroup());
        assertEquals(PaperSize.A4, project.getPaperSize());
        assertEquals(ReadingDirection.LTR, project.getReadingDirection());
        assertEquals(ImpositionLayout.FOLIO, project.getLayout());
        assertNotNull(project.getPageSequence());
        assertNotNull(project.getPaddingConfig());
        assertNotNull(project.getNumberingConfig());
        assertNotNull(project.getMarkConfig());
        assertNotNull(project.getCreepConfig());
        assertTrue(project.getOutputPath().isEmpty());
    }

    @Test
    void impositionGroupDerivedFromTechnique() {
        assertEquals(ImpositionGroup.A,
                validBuilder().bindingTechnique(BindingTechnique.PERFECT_BINDING).build()
                        .getImpositionGroup());
        assertEquals(ImpositionGroup.C,
                validBuilder().bindingTechnique(BindingTechnique.COPTIC).build()
                        .getImpositionGroup());
    }

    @Test
    void outputPathIsSet() {
        Path p = Path.of("/tmp/out.pdf");
        QuireProject project = validBuilder().outputPath(p).build();
        assertEquals(p, project.getOutputPath().orElseThrow());
    }

    @Test
    void outputPathNullClearsOptional() {
        QuireProject project = validBuilder().outputPath(null).build();
        assertTrue(project.getOutputPath().isEmpty());
    }

    @Test
    void nullNameThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> validBuilder().name(null));
    }

    @Test
    void blankNameThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> validBuilder().name("   "));
    }

    @Test
    void missingNameInBuildThrows() {
        assertThrows(NullPointerException.class,
                () -> QuireProject.builder()
                        .bindingTechnique(BindingTechnique.SADDLE_STITCH)
                        .paperSize(PaperSize.A4)
                        .readingDirection(ReadingDirection.LTR)
                        .layout(ImpositionLayout.FOLIO)
                        .pageSequence(new PageSequence())
                        .paddingConfig(PaddingConfig.builder().build())
                        .numberingConfig(NumberingConfig.builder().build())
                        .markConfig(MarkConfig.builder().build())
                        .creepConfig(CreepConfig.builder().build())
                        .build());
    }

    @Test
    void nullBindingTechniqueThrows() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().bindingTechnique(null));
    }

    @Test
    void nullPaperSizeThrows() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().paperSize(null));
    }

    @Test
    void nullReadingDirectionThrows() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().readingDirection(null));
    }

    @Test
    void nullLayoutThrows() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().layout(null));
    }

    @Test
    void nullPageSequenceThrows() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().pageSequence(null));
    }

    @Test
    void nullPaddingConfigThrows() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().paddingConfig(null));
    }

    @Test
    void nullNumberingConfigThrows() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().numberingConfig(null));
    }

    @Test
    void nullMarkConfigThrows() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().markConfig(null));
    }

    @Test
    void nullCreepConfigThrows() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().creepConfig(null));
    }

    @Test
    void toBuilderRoundtripSameSequenceInstance() {
        QuireProject original = validBuilder().build();
        QuireProject copy = original.toBuilder().build();
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getBindingTechnique(), copy.getBindingTechnique());
        assertSame(original.getPageSequence(), copy.getPageSequence());
    }

    @Test
    void equalsSameSequenceInstance() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).build();
        QuireProject b = validBuilder().pageSequence(seq).build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsDifferentSequenceInstances() {
        QuireProject a = validBuilder().pageSequence(new PageSequence()).build();
        QuireProject b = validBuilder().pageSequence(new PageSequence()).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsSelf() {
        QuireProject a = validBuilder().build();
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        assertNotEquals(validBuilder().build(), null);
    }

    @Test
    void equalsWrongType() {
        assertNotEquals(validBuilder().build(), "other");
    }

    @Test
    void equalsDifferentName() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).name("A").build();
        QuireProject b = validBuilder().pageSequence(seq).name("B").build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentBindingTechnique() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).build();
        QuireProject b = validBuilder().pageSequence(seq)
                .bindingTechnique(BindingTechnique.PERFECT_BINDING).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentPaperSize() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).build();
        QuireProject b = validBuilder().pageSequence(seq).paperSize(PaperSize.LETTER).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentReadingDirection() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).build();
        QuireProject b = validBuilder().pageSequence(seq)
                .readingDirection(ReadingDirection.RTL).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentLayout() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).build();
        QuireProject b = validBuilder().pageSequence(seq)
                .layout(ImpositionLayout.QUARTO).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentPaddingConfig() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).build();
        QuireProject b = validBuilder().pageSequence(seq)
                .paddingConfig(PaddingConfig.builder().aestheticFront(1).build()).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentNumberingConfig() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).build();
        QuireProject b = validBuilder().pageSequence(seq)
                .numberingConfig(NumberingConfig.builder().bodyStartNumber(2).build()).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentMarkConfig() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).build();
        QuireProject b = validBuilder().pageSequence(seq)
                .markConfig(MarkConfig.builder().foldLines(true).build()).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentCreepConfig() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).build();
        QuireProject b = validBuilder().pageSequence(seq)
                .creepConfig(CreepConfig.builder().transformApplied(true).build()).build();
        assertNotEquals(a, b);
    }

    @Test
    void equalsDifferentOutputPath() {
        PageSequence seq = new PageSequence();
        QuireProject a = validBuilder().pageSequence(seq).build();
        QuireProject b = validBuilder().pageSequence(seq)
                .outputPath(Path.of("/out.pdf")).build();
        assertNotEquals(a, b);
    }

    @Test
    void toStringDoesNotThrow() {
        assertNotNull(validBuilder().build().toString());
    }
}
