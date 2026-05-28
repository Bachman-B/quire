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
package com.maiitsoh.quirebind.batch.runner;

import com.maiitsoh.quirebind.batch.model.BatchConfig;
import com.maiitsoh.quirebind.batch.model.BatchJob;
import com.maiitsoh.quirebind.batch.model.BatchPaddingConfig;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Applies a {@link BatchConfig} template to an explicit list of input PDF paths.
 *
 * <p>Unlike {@link BatchRunner}, which reads {@code source} and {@code output} from
 * per-job entries in the config, this runner accepts a set of input PDFs chosen at
 * run time and derives output paths via a caller-supplied mapping function.
 *
 * <p>All binding settings (technique, paper size, numbering, marks, …) come from the
 * template's {@code defaults} section; any {@code jobs} in the file are ignored.
 */
public final class BatchTemplateRunner {

    private BatchTemplateRunner() {
    }

    /**
     * Result for a single input file.
     *
     * @param inputPath  the source PDF that was processed
     * @param outputPath the path the output was (or would have been) written to
     * @param success    {@code true} if the job completed without error
     * @param message    a human-readable summary or error description
     */
    public record TemplateJobResult(Path inputPath, Path outputPath, boolean success,
            String message) {
    }

    /**
     * Processes each input PDF using the template's defaults, writing outputs
     * to paths produced by {@code outputMapper}.
     *
     * @param template     parsed template configuration; must not be null
     * @param inputs       ordered list of source PDF paths; must not be null
     * @param outputMapper maps each input path to its output path; must not be null
     * @param dryRun       if {@code true}, the pipeline runs but no files are written
     * @return one result per input, in order
     */
    public static List<TemplateJobResult> run(
            BatchConfig template,
            List<Path> inputs,
            Function<Path, Path> outputMapper,
            boolean dryRun) {
        Objects.requireNonNull(template, "template");
        Objects.requireNonNull(inputs, "inputs");
        Objects.requireNonNull(outputMapper, "outputMapper");

        List<TemplateJobResult> results = new ArrayList<>();
        for (Path input : inputs) {
            Path output = outputMapper.apply(input);
            BatchJob syntheticJob = new BatchJob(
                    stripExtension(input.getFileName().toString()),
                    input.toString(),
                    output.toString(),
                    null,
                    "folio",
                    0,
                    null,
                    null,
                    BatchPaddingConfig.NONE,
                    null,
                    null,
                    null);
            Path baseDir = input.toAbsolutePath().getParent();
            BatchRunner.JobResult jr = BatchRunner.runSingleJob(
                    syntheticJob, template.defaults(), baseDir, dryRun);
            results.add(new TemplateJobResult(input, output, jr.success(), jr.message()));
        }
        return List.copyOf(results);
    }

    /**
     * Convenience: derive output paths by inserting {@code suffix} before the
     * {@code .pdf} extension (or appending it if there is no extension).
     *
     * <p>Each output is placed in the same directory as its input.
     * Example: {@code /docs/book.pdf} + suffix {@code -imposed} →
     * {@code /docs/book-imposed.pdf}.
     *
     * @param template parsed template configuration; must not be null
     * @param inputs   source PDF paths; must not be null
     * @param suffix   string to insert before the {@code .pdf} extension; must not be blank
     * @param dryRun   skip writing output files
     * @return one result per input, in order
     */
    public static List<TemplateJobResult> runWithSuffix(
            BatchConfig template, List<Path> inputs, String suffix, boolean dryRun) {
        Objects.requireNonNull(suffix, "suffix");
        return run(template, inputs, input -> {
            String name = input.getFileName().toString();
            int dot = name.lastIndexOf('.');
            String outName = dot > 0
                    ? name.substring(0, dot) + suffix + name.substring(dot)
                    : name + suffix;
            Path dir = input.getParent();
            return dir != null ? dir.resolve(outName) : Path.of(outName);
        }, dryRun);
    }

    /**
     * Convenience: write all outputs to a single directory, preserving the original
     * file name.
     *
     * @param template  parsed template configuration; must not be null
     * @param inputs    source PDF paths; must not be null
     * @param outputDir directory where outputs are written; must not be null
     * @param dryRun    skip writing output files
     * @return one result per input, in order
     */
    public static List<TemplateJobResult> runWithDirectory(
            BatchConfig template, List<Path> inputs, Path outputDir, boolean dryRun) {
        Objects.requireNonNull(outputDir, "outputDir");
        return run(template, inputs, input -> outputDir.resolve(input.getFileName()), dryRun);
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
