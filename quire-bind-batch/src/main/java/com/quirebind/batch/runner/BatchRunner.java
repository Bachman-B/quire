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
package com.quirebind.batch.runner;

import com.quirebind.batch.model.BatchConfig;
import com.quirebind.batch.model.BatchCreepConfig;
import com.quirebind.batch.model.BatchDefaults;
import com.quirebind.batch.model.BatchJob;
import com.quirebind.batch.model.BatchMarksConfig;
import com.quirebind.batch.model.BatchNumberingConfig;
import com.quirebind.batch.model.BatchPaddingConfig;
import com.quirebind.core.imposition.ImpositionEngine;
import com.quirebind.core.model.BindingTechnique;
import com.quirebind.core.model.CreepConfig;
import com.quirebind.core.model.FolioStyle;
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Executes the jobs defined in a {@link BatchConfig}.
 *
 * <p>Each job produces an imposed PDF at its configured output path unless
 * {@code dryRun} is {@code true}, in which case the pipeline runs but no file is written.
 */
public final class BatchRunner {

    private BatchRunner() {
    }

    /**
     * A single job's execution result.
     *
     * @param name    the job name
     * @param success {@code true} if the job completed without error
     * @param message a human-readable summary or error description
     */
    public record JobResult(String name, boolean success, String message) {
    }

    /**
     * Runs all jobs in the given configuration.
     *
     * @param config  the parsed batch configuration; must not be null
     * @param baseDir directory used to resolve relative source and output paths; must not be null
     * @param dryRun  if {@code true}, the imposition pipeline runs but no files are written
     * @return an unmodifiable list of results, one per job, in definition order
     */
    public static List<JobResult> run(BatchConfig config, Path baseDir, boolean dryRun) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(baseDir, "baseDir");
        List<JobResult> results = new ArrayList<>();
        for (BatchJob job : config.jobs()) {
            results.add(executeJob(job, config.defaults(), baseDir, dryRun));
        }
        return List.copyOf(results);
    }

    private static JobResult executeJob(
            BatchJob job, BatchDefaults defaults, Path baseDir, boolean dryRun) {
        String name = job.name() != null ? job.name() : "<unnamed>";
        try {
            Path sourcePath = resolve(baseDir, job.source());
            Path outputPath = resolve(baseDir, job.output());
            QuireProject project = buildProject(job, defaults, sourcePath);
            List<Signature> signatures = ImpositionEngine.impose(project);
            if (!dryRun) {
                PdfImpositionWriter.write(signatures, sourcePath, outputPath,
                        project.getPaperSize());
            }
            String msg = dryRun
                    ? "dry-run OK — " + signatures.size() + " signature(s)"
                    : "written → " + outputPath;
            return new JobResult(name, true, msg);
        } catch (Exception e) {
            return new JobResult(name, false, e.getMessage());
        }
    }

    private static Path resolve(Path baseDir, String pathStr) {
        if (pathStr == null || pathStr.isBlank()) {
            throw new IllegalArgumentException("path must not be blank");
        }
        return baseDir.resolve(pathStr).normalize();
    }

    private static QuireProject buildProject(
            BatchJob job, BatchDefaults defaults, Path sourcePath) throws IOException {
        String techniqueStr = job.technique();
        if (techniqueStr == null || techniqueStr.isBlank()) {
            throw new IllegalArgumentException("job '" + job.name() + "' has no technique");
        }
        BindingTechnique technique = BindingTechnique.valueOf(techniqueStr.toUpperCase());

        String dirStr = coalesce(job.readingDirection(), defaults.readingDirection(), "ltr");
        ReadingDirection direction = ReadingDirection.valueOf(dirStr.toUpperCase());

        String sizeStr = coalesce(job.paperSize(), defaults.paperSize(), "a4");
        PaperSize paperSize = PaperSize.valueOf(sizeStr.toUpperCase());

        BatchPaddingConfig bp = job.padding() != null ? job.padding() : BatchPaddingConfig.NONE;
        PaddingConfig padding = PaddingConfig.builder()
                .aestheticFront(bp.aestheticFront())
                .aestheticRear(bp.aestheticRear())
                .completionFront(bp.completionFront())
                .completionRear(bp.completionRear())
                .signatureSize(job.signatureSize())
                .build();

        BatchNumberingConfig bn = coalesceNumbering(job.numbering(), defaults.numbering());
        NumberingConfig numbering = NumberingConfig.builder()
                .frontMatterStyle(toFolioStyle(bn.frontMatterStyle(), FolioStyle.NONE))
                .bodyStyle(toFolioStyle(bn.bodyStyle(), FolioStyle.ARABIC))
                .bodyStartNumber(bn.bodyStart())
                .suppressFirstBodyFolio(bn.suppressFirstBodyFolio())
                .build();

        BatchMarksConfig bm = coalesceMarks(job.marks(), defaults.marks());
        MarkConfig marks = MarkConfig.builder()
                .foldLines(bm.foldLines())
                .signatureProofMarkers(bm.signatureProofMarkers())
                .build();

        CreepConfig creep = buildCreepConfig(job.creep());

        return QuireProject.builder()
                .name(job.name() != null ? job.name() : "batch-job")
                .bindingTechnique(technique)
                .paperSize(paperSize)
                .readingDirection(direction)
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(PdfPageLoader.load(sourcePath))
                .paddingConfig(padding)
                .numberingConfig(numbering)
                .markConfig(marks)
                .creepConfig(creep)
                .build();
    }

    private static CreepConfig buildCreepConfig(BatchCreepConfig bc) {
        if (bc == null) {
            return CreepConfig.builder().build();
        }
        CreepConfig.Builder b = CreepConfig.builder();
        if (bc.paperWeightGsm() != null) {
            b.paperWeightGsm(bc.paperWeightGsm());
        }
        if (bc.paperThicknessMm() != null) {
            b.paperThicknessMm(bc.paperThicknessMm());
        }
        return b.build();
    }

    private static FolioStyle toFolioStyle(String value, FolioStyle fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return FolioStyle.valueOf(value.toUpperCase());
    }

    private static String coalesce(String a, String b, String c) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        if (b != null && !b.isBlank()) {
            return b;
        }
        return c;
    }

    private static BatchNumberingConfig coalesceNumbering(
            BatchNumberingConfig job, BatchNumberingConfig def) {
        if (job != null && job != BatchNumberingConfig.DEFAULT) {
            return job;
        }
        return def != null ? def : BatchNumberingConfig.DEFAULT;
    }

    private static BatchMarksConfig coalesceMarks(BatchMarksConfig job, BatchMarksConfig def) {
        if (job != null && job != BatchMarksConfig.DEFAULT) {
            return job;
        }
        return def != null ? def : BatchMarksConfig.DEFAULT;
    }
}
