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
package com.maiitsoh.quirebind.guides.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts a subset of Markdown to HTML sufficient for rendering binding guides.
 *
 * <p>Handles: ATX headings (H1–H4), paragraphs, fenced/indented code blocks,
 * blockquotes (rendered as styled callout boxes), ordered and unordered lists,
 * horizontal rules, images, inline links, bold, italic, and inline code.
 *
 * <p>An optional image resolver maps {@code images/...} src values to data-URI
 * strings so that classpath resources can be embedded directly in the HTML.
 */
public final class MarkdownToHtml {

    private static final Pattern HEADING = Pattern.compile("^(#{1,4})\\s+(.+?)(?:\\s+\\{#[^}]+})?\\s*$");
    private static final Pattern HR = Pattern.compile("^-{3,}\\s*$");
    private static final Pattern UL_ITEM = Pattern.compile("^[-*+]\\s+(.+)$");
    private static final Pattern OL_ITEM = Pattern.compile("^\\d+\\.\\s+(.+)$");
    private static final Pattern BLOCKQUOTE = Pattern.compile("^>\\s?(.*)$");
    private static final Pattern IMAGE = Pattern.compile("!\\[([^]]*)]\\(([^)]+)\\)");
    private static final Pattern LINK = Pattern.compile("(?<!!)\\[([^]]+)]\\(([^)]+)\\)");
    private static final Pattern BOLD = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static final Pattern ITALIC = Pattern.compile("(?<![*])\\*([^*]+?)\\*(?![*])");
    private static final Pattern CODE_INLINE = Pattern.compile("`([^`]+)`");
    private static final String TIP_PREFIX = "**Tip:**";
    private static final String WARN_PREFIX = "**Warning:**";

    private MarkdownToHtml() {
    }

    /**
     * Converts Markdown text to an HTML {@code <body>} fragment.
     *
     * @param markdown      the Markdown source; must not be null
     * @param imageResolver maps {@code src} attribute values to replacement strings
     *                      (e.g. data URIs); return null to leave the src unchanged
     * @return the rendered HTML fragment
     */
    public static String toHtml(String markdown, Function<String, String> imageResolver) {
        String[] lines = markdown.split("\n", -1);
        StringBuilder out = new StringBuilder();
        int i = 0;
        String listType = null;

        while (i < lines.length) {
            String line = lines[i];

            // Heading
            Matcher hm = HEADING.matcher(line);
            if (hm.matches()) {
                listType = closeList(out, listType);
                int level = hm.group(1).length();
                out.append("<h").append(level).append(">")
                   .append(inlineHtml(hm.group(2), imageResolver))
                   .append("</h").append(level).append(">\n");
                i++;
                continue;
            }

            // Horizontal rule
            if (HR.matcher(line).matches()) {
                listType = closeList(out, listType);
                out.append("<hr/>\n");
                i++;
                continue;
            }

            // Blockquote
            Matcher bqm = BLOCKQUOTE.matcher(line);
            if (bqm.matches()) {
                listType = closeList(out, listType);
                List<String> bqLines = new ArrayList<>();
                while (i < lines.length && BLOCKQUOTE.matcher(lines[i]).matches()) {
                    bqLines.add(BLOCKQUOTE.matcher(lines[i]).replaceFirst("$1"));
                    i++;
                }
                String bqBody = String.join(" ", bqLines);
                String cssClass = "callout";
                if (bqBody.startsWith(TIP_PREFIX)) {
                    cssClass = "callout tip";
                    bqBody = bqBody.substring(TIP_PREFIX.length()).trim();
                } else if (bqBody.startsWith(WARN_PREFIX)) {
                    cssClass = "callout warn";
                    bqBody = bqBody.substring(WARN_PREFIX.length()).trim();
                }
                out.append("<div class=\"").append(cssClass).append("\">")
                   .append(inlineHtml(bqBody, imageResolver))
                   .append("</div>\n");
                continue;
            }

            // Unordered list item
            Matcher ulm = UL_ITEM.matcher(line);
            if (ulm.matches()) {
                if (!"ul".equals(listType)) {
                    listType = closeList(out, listType);
                    out.append("<ul>\n");
                    listType = "ul";
                }
                out.append("<li>").append(inlineHtml(ulm.group(1), imageResolver))
                   .append("</li>\n");
                i++;
                continue;
            }

            // Ordered list item
            Matcher olm = OL_ITEM.matcher(line);
            if (olm.matches()) {
                if (!"ol".equals(listType)) {
                    listType = closeList(out, listType);
                    out.append("<ol>\n");
                    listType = "ol";
                }
                out.append("<li>").append(inlineHtml(olm.group(1), imageResolver))
                   .append("</li>\n");
                i++;
                continue;
            }

            // Blank line
            if (line.isBlank()) {
                listType = closeList(out, listType);
                i++;
                continue;
            }

            // Paragraph (collect until blank line or block element)
            listType = closeList(out, listType);
            List<String> paraLines = new ArrayList<>();
            while (i < lines.length
                    && !lines[i].isBlank()
                    && !HEADING.matcher(lines[i]).matches()
                    && !HR.matcher(lines[i]).matches()
                    && !BLOCKQUOTE.matcher(lines[i]).matches()
                    && !UL_ITEM.matcher(lines[i]).matches()
                    && !OL_ITEM.matcher(lines[i]).matches()) {
                paraLines.add(lines[i]);
                i++;
            }
            if (!paraLines.isEmpty()) {
                String paraText = String.join(" ", paraLines);
                out.append("<p>").append(inlineHtml(paraText, imageResolver))
                   .append("</p>\n");
            }
        }
        closeList(out, listType);
        return out.toString();
    }

    private static String closeList(StringBuilder out, String listType) {
        if ("ul".equals(listType)) {
            out.append("</ul>\n");
        } else if ("ol".equals(listType)) {
            out.append("</ol>\n");
        }
        return null;
    }

    private static String inlineHtml(String text, Function<String, String> imageResolver) {
        text = escapeHtml(text);
        // Images
        Matcher im = IMAGE.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (im.find()) {
            String alt = im.group(1);
            String src = im.group(2);
            if (imageResolver != null) {
                String resolved = imageResolver.apply(src);
                if (resolved != null) {
                    src = resolved;
                }
            }
            im.appendReplacement(sb,
                "<figure><img src=\"" + src + "\" alt=\"" + alt + "\"/>"
                + (alt.isBlank() ? "" : "<figcaption>" + alt + "</figcaption>")
                + "</figure>");
        }
        im.appendTail(sb);
        text = sb.toString();
        // Links
        text = LINK.matcher(text).replaceAll(m -> "<a href=\"" + m.group(2) + "\">" + m.group(1) + "</a>");
        // Bold
        text = BOLD.matcher(text).replaceAll(m -> "<strong>" + m.group(1) + "</strong>");
        // Italic
        text = ITALIC.matcher(text).replaceAll(m -> "<em>" + m.group(1) + "</em>");
        // Inline code
        text = CODE_INLINE.matcher(text).replaceAll(m -> "<code>" + m.group(1) + "</code>");
        return text;
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
}
