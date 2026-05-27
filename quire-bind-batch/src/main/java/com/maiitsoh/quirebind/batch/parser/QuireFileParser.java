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
package com.maiitsoh.quirebind.batch.parser;

import com.maiitsoh.quirebind.batch.model.BatchConfig;
import com.maiitsoh.quirebind.batch.model.BatchCreepConfig;
import com.maiitsoh.quirebind.batch.model.BatchDefaults;
import com.maiitsoh.quirebind.batch.model.BatchJob;
import com.maiitsoh.quirebind.batch.model.BatchMarksConfig;
import com.maiitsoh.quirebind.batch.model.BatchNumberingConfig;
import com.maiitsoh.quirebind.batch.model.BatchPaddingConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Parses a {@code .quire} YAML batch configuration file into a {@link BatchConfig}.
 *
 * <p>Unknown keys are silently ignored. Missing optional keys use safe defaults.
 * The only required fields are each job's {@code name}, {@code source},
 * {@code output}, and {@code technique}.
 */
public final class QuireFileParser {

    private QuireFileParser() {
    }

    /**
     * Parses the given {@code .quire} file.
     *
     * @param configPath path to the {@code .quire} file; must not be null
     * @return the parsed configuration
     * @throws IOException if the file cannot be read or contains invalid YAML
     */
    public static BatchConfig parse(Path configPath) throws IOException {
        Objects.requireNonNull(configPath, "configPath");
        String content = Files.readString(configPath, StandardCharsets.UTF_8);
        return parseString(content);
    }

    /**
     * Parses a {@code .quire} file from its string content.
     *
     * @param content the YAML content; must not be null
     * @return the parsed configuration
     * @throws IOException if the content is not valid YAML
     */
    public static BatchConfig parseString(String content) throws IOException {
        Objects.requireNonNull(content, "content");
        Yaml yaml = new Yaml();
        Map<String, Object> root;
        try {
            root = yaml.load(content);
        } catch (Exception e) {
            throw new IOException("Failed to parse .quire YAML: " + e.getMessage(), e);
        }
        if (root == null) {
            root = Map.of();
        }
        String version = str(root, "quire_version");
        BatchDefaults defaults = parseDefaults(map(root, "defaults"));
        List<BatchJob> jobs = parseJobs(list(root, "jobs"));
        return new BatchConfig(version, defaults, jobs);
    }

    private static BatchDefaults parseDefaults(Map<String, Object> m) {
        if (m == null) {
            return BatchDefaults.factoryDefault();
        }
        return new BatchDefaults(
                strOr(m, "reading_direction", "ltr"),
                strOr(m, "paper_size", "a4"),
                parseNumbering(map(m, "numbering")),
                parseMarks(map(m, "marks")));
    }

    private static List<BatchJob> parseJobs(List<Object> rawJobs) {
        if (rawJobs == null) {
            return List.of();
        }
        List<BatchJob> jobs = new ArrayList<>();
        for (Object item : rawJobs) {
            if (item instanceof Map<?, ?> rawMap) {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) rawMap;
                jobs.add(parseJob(m));
            }
        }
        return List.copyOf(jobs);
    }

    private static BatchJob parseJob(Map<String, Object> m) {
        return new BatchJob(
                str(m, "name"),
                str(m, "source"),
                str(m, "output"),
                str(m, "technique"),
                strOr(m, "layout", "folio"),
                intOr(m, "signature_size", 0),
                str(m, "reading_direction"),
                str(m, "paper_size"),
                parsePadding(map(m, "padding")),
                parseNumbering(map(m, "numbering")),
                parseCreep(map(m, "creep")),
                parseMarks(map(m, "marks")));
    }

    private static BatchPaddingConfig parsePadding(Map<String, Object> m) {
        if (m == null) {
            return BatchPaddingConfig.NONE;
        }
        return new BatchPaddingConfig(
                intOr(m, "aesthetic_front", 0),
                intOr(m, "aesthetic_rear", 0),
                intOr(m, "completion_front", 0),
                intOr(m, "completion_rear", 0));
    }

    private static BatchNumberingConfig parseNumbering(Map<String, Object> m) {
        if (m == null) {
            return BatchNumberingConfig.DEFAULT;
        }
        return new BatchNumberingConfig(
                str(m, "front_matter_style"),
                strOr(m, "body_style", "arabic"),
                intOr(m, "body_start", 1),
                boolOr(m, "suppress_first_body_folio", true));
    }

    private static BatchCreepConfig parseCreep(Map<String, Object> m) {
        if (m == null) {
            return BatchCreepConfig.NONE;
        }
        return new BatchCreepConfig(
                doubleOrNull(m, "paper_weight_gsm"),
                doubleOrNull(m, "paper_thickness_mm"));
    }

    private static BatchMarksConfig parseMarks(Map<String, Object> m) {
        if (m == null) {
            return BatchMarksConfig.DEFAULT;
        }
        return new BatchMarksConfig(
                boolOr(m, "fold_lines", true),
                boolOr(m, "signature_proof_markers", true));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static String str(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v != null ? v.toString() : null;
    }

    private static String strOr(Map<String, Object> m, String key, String fallback) {
        String v = str(m, key);
        return (v != null && !v.isBlank()) ? v : fallback;
    }

    private static int intOr(Map<String, Object> m, String key, int fallback) {
        Object v = m.get(key);
        if (v instanceof Number n) {
            return n.intValue();
        }
        return fallback;
    }

    private static boolean boolOr(Map<String, Object> m, String key, boolean fallback) {
        Object v = m.get(key);
        if (v instanceof Boolean b) {
            return b;
        }
        return fallback;
    }

    private static Double doubleOrNull(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v instanceof Number n) {
            return n.doubleValue();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> map(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v instanceof Map<?, ?>) {
            return (Map<String, Object>) v;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> list(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v instanceof List<?>) {
            return (List<Object>) v;
        }
        return null;
    }
}
