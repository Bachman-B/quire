/*
 * Copyright 2025 Quire Contributors
 *
 * This file is part of Quire.
 *
 * Quire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Quire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Quire.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.quire.guides.loader;

import com.quire.guides.model.BindingGuide;
import com.quire.guides.model.GuideMetadata;
import com.quire.guides.model.GuideSection;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Loads bundled binding guides from the classpath.
 *
 * <p>Guide resources follow the path convention:
 * <pre>
 *   /guides/{technique-id-with-hyphens}/{locale}.md
 * </pre>
 *
 * <p>Each file uses YAML front matter delimited by {@code ---} on its own line,
 * followed by a Markdown body.
 *
 * <p>The set of bundled guides is listed in {@code /guides/index}, one entry per line
 * in the format {@code technique-id/locale} (e.g. {@code saddle-stitch/en}).
 */
public final class GuideLoader {

    private static final String GUIDES_INDEX = "/guides/index";
    private static final String GUIDES_BASE = "/guides/";
    private static final String FRONTMATTER_DELIMITER = "---";

    private GuideLoader() {
    }

    /**
     * Loads all bundled guides listed in {@code /guides/index}.
     *
     * @return an unmodifiable list of all available guides, in index order
     * @throws IOException if any guide resource cannot be read or parsed
     */
    public static List<BindingGuide> loadAll() throws IOException {
        try (InputStream in = GuideLoader.class.getResourceAsStream(GUIDES_INDEX)) {
            if (in == null) {
                return List.of();
            }
            String index = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            List<BindingGuide> guides = new ArrayList<>();
            for (String line : index.lines().toList()) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] parts = trimmed.split("/", 2);
                if (parts.length == 2) {
                    // Index uses hyphens; load() expects underscores for the techniqueId
                    guides.add(load(parts[0].replace('-', '_'), parts[1]));
                }
            }
            return List.copyOf(guides);
        }
    }

    /**
     * Loads a single guide by technique ID and locale.
     *
     * @param techniqueId the binding technique identifier using underscores
     *                    (e.g. {@code saddle_stitch}); must not be null
     * @param locale      the BCP 47 locale tag (e.g. {@code en}); must not be null
     * @return the parsed guide
     * @throws IOException if the resource cannot be found or parsed
     */
    public static BindingGuide load(String techniqueId, String locale) throws IOException {
        Objects.requireNonNull(techniqueId, "techniqueId");
        Objects.requireNonNull(locale, "locale");
        String resourcePath = GUIDES_BASE + techniqueId.replace('_', '-') + "/" + locale + ".md";
        try (InputStream in = GuideLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Guide resource not found: " + resourcePath);
            }
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return parse(content, resourcePath);
        }
    }

    static BindingGuide parse(String content, String resourcePath) throws IOException {
        String[] lines = content.split("\n", -1);
        if (lines.length < 2 || !lines[0].trim().equals(FRONTMATTER_DELIMITER)) {
            throw new IOException("Guide at '" + resourcePath + "' does not start with '---'");
        }
        int endIdx = -1;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().equals(FRONTMATTER_DELIMITER)) {
                endIdx = i;
                break;
            }
        }
        if (endIdx < 0) {
            throw new IOException(
                    "Guide at '" + resourcePath + "' has no closing '---' for frontmatter");
        }
        String yamlBlock = String.join("\n", Arrays.copyOfRange(lines, 1, endIdx));
        String body = String.join("\n",
                Arrays.copyOfRange(lines, endIdx + 1, lines.length)).stripLeading();
        return new BindingGuide(parseMetadata(yamlBlock), body);
    }

    @SuppressWarnings("unchecked")
    private static GuideMetadata parseMetadata(String yaml) {
        Yaml parser = new Yaml();
        Map<String, Object> map = parser.load(yaml);
        if (map == null) {
            map = Map.of();
        }

        List<GuideSection> sections = new ArrayList<>();
        Object rawSections = map.get("sections");
        if (rawSections instanceof List<?> sectionList) {
            for (Object s : sectionList) {
                if (s instanceof Map<?, ?> sm) {
                    String id = Objects.toString(sm.get("id"), "");
                    String title = Objects.toString(sm.get("title"), "");
                    if (!id.isBlank() && !title.isBlank()) {
                        sections.add(new GuideSection(id, title));
                    }
                }
            }
        }

        return GuideMetadata.builder()
                .quireGuideVersion(Objects.toString(map.get("quire_guide_version"), ""))
                .techniqueId(Objects.toString(map.get("technique_id"), ""))
                .locale(Objects.toString(map.get("locale"), ""))
                .title(Objects.toString(map.get("title"), ""))
                .subtitle(Objects.toString(map.get("subtitle"), ""))
                .group(Objects.toString(map.get("group"), ""))
                .difficulty(Objects.toString(map.get("difficulty"), ""))
                .timeEstimate(Objects.toString(map.get("time_estimate"), ""))
                .schematicDescription(Objects.toString(map.get("schematic_description"), ""))
                .tools(toStringList(map.get("tools")))
                .materials(toStringList(map.get("materials")))
                .readingDirectionsSupported(toStringList(map.get("reading_directions_supported")))
                .relatedTechniques(toStringList(map.get("related_techniques")))
                .sections(sections)
                .build();
    }

    private static List<String> toStringList(Object obj) {
        if (!(obj instanceof List<?> list)) {
            return List.of();
        }
        return list.stream()
                .map(item -> Objects.toString(item, ""))
                .filter(s -> !s.isBlank())
                .toList();
    }
}
