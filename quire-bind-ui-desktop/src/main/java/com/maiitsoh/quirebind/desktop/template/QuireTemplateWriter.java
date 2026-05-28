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
package com.maiitsoh.quirebind.desktop.template;

import com.maiitsoh.quirebind.desktop.state.WizardState;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serialises the current {@link WizardState} as a {@code .quire} YAML template file.
 *
 * <p>The output is a batch config with a single job whose {@code input} and {@code output}
 * paths are taken from the wizard state, so the template can be re-used for future runs
 * by editing only those two paths.
 */
public final class QuireTemplateWriter {

    private QuireTemplateWriter() { }

    /**
     * Writes the wizard state as a {@code .quire} YAML file to {@code outputPath}.
     *
     * @param state      the current wizard state; must not be null
     * @param outputPath destination path; will be created or overwritten
     * @throws IOException if writing fails
     */
    public static void write(WizardState state, Path outputPath) throws IOException {
        Map<String, Object> root = buildMap(state);
        DumperOptions opts = new DumperOptions();
        opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        opts.setPrettyFlow(true);
        Yaml yaml = new Yaml(opts);
        try (Writer w = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            yaml.dump(root, w);
        }
    }

    private static Map<String, Object> buildMap(WizardState state) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("quire-version", "1.0");

        Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("technique", toKebab(state.getTechnique().name()));
        defaults.put("paper-size", state.getPaperSize().name().toLowerCase());
        defaults.put("reading-direction", state.getReadingDirection().name().toLowerCase());
        defaults.put("pages-per-signature", state.getPagesPerSignature());
        if (state.getPaperThicknessMm() > 0) {
            defaults.put("paper-thickness-mm", state.getPaperThicknessMm());
        }

        Map<String, Object> marks = new LinkedHashMap<>();
        marks.put("fold-lines", state.isFoldLines());
        marks.put("stitch-marks", state.isStitchMarks());
        marks.put("sewing-holes", state.isSewingHoles());
        marks.put("trim-lines", state.isTrimLines());
        defaults.put("marks", marks);
        root.put("defaults", defaults);

        Map<String, Object> job = new LinkedHashMap<>();
        job.put("name", state.getInputPdf() != null
            ? stripExtension(state.getInputPdf().getFileName().toString())
            : "my-book");
        job.put("input", state.getInputPdf() != null ? state.getInputPdf().toString() : "input.pdf");
        job.put("output", state.getOutputPdf() != null
            ? state.getOutputPdf().toString() : "output-imposed.pdf");
        root.put("jobs", List.of(job));

        return root;
    }

    private static String toKebab(String enumName) {
        return enumName.toLowerCase().replace('_', '-');
    }

    private static String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }
}
