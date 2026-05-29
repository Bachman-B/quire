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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownToHtmlTest {

    private static String html(String markdown) {
        return MarkdownToHtml.toHtml(markdown, null);
    }

    // --- Headings ---

    @Test
    void h1IsRendered() {
        assertTrue(html("# Hello").contains("<h1>Hello</h1>"));
    }

    @Test
    void h2IsRendered() {
        assertTrue(html("## Hello").contains("<h2>Hello</h2>"));
    }

    @Test
    void h3IsRendered() {
        assertTrue(html("### Hello").contains("<h3>Hello</h3>"));
    }

    @Test
    void h4IsRendered() {
        assertTrue(html("#### Hello").contains("<h4>Hello</h4>"));
    }

    @Test
    void headingAnchorSuffixIsStripped() {
        String result = html("## Section Title {#my-anchor}");
        assertTrue(result.contains("<h2>Section Title</h2>"));
        assertFalse(result.contains("{#my-anchor}"));
    }

    // --- Paragraphs ---

    @Test
    void simpleParagraphIsWrappedInP() {
        assertTrue(html("Hello world").contains("<p>Hello world</p>"));
    }

    @Test
    void consecutiveLinesFormSingleParagraph() {
        String result = html("Line one\nLine two");
        assertTrue(result.contains("<p>Line one Line two</p>"));
    }

    @Test
    void blankLineSeparatesParagraphs() {
        String result = html("First\n\nSecond");
        assertTrue(result.contains("<p>First</p>"));
        assertTrue(result.contains("<p>Second</p>"));
    }

    // --- Horizontal rule ---

    @Test
    void hrIsRendered() {
        assertTrue(html("---").contains("<hr/>"));
    }

    @Test
    void hrWithExtraHyphensIsRendered() {
        assertTrue(html("-----").contains("<hr/>"));
    }

    // --- Unordered list ---

    @Test
    void dashItemRendersUlAndLi() {
        String result = html("- Item one");
        assertTrue(result.contains("<ul>"));
        assertTrue(result.contains("<li>Item one</li>"));
        assertTrue(result.contains("</ul>"));
    }

    @Test
    void asteriskItemRendersUl() {
        assertTrue(html("* Item").contains("<ul>"));
    }

    @Test
    void plusItemRendersUl() {
        assertTrue(html("+ Item").contains("<ul>"));
    }

    @Test
    void multipleUlItemsShareOneUlTag() {
        String result = html("- A\n- B\n- C");
        assertEquals(1, countOccurrences(result, "<ul>"));
        assertEquals(3, countOccurrences(result, "<li>"));
    }

    // --- Ordered list ---

    @Test
    void numberedItemRendersOlAndLi() {
        String result = html("1. First item");
        assertTrue(result.contains("<ol>"));
        assertTrue(result.contains("<li>First item</li>"));
        assertTrue(result.contains("</ol>"));
    }

    @Test
    void multipleOlItemsShareOneOlTag() {
        String result = html("1. A\n2. B\n3. C");
        assertEquals(1, countOccurrences(result, "<ol>"));
        assertEquals(3, countOccurrences(result, "<li>"));
    }

    // --- Blockquotes ---

    @Test
    void plainBlockquoteRendersCalloutDiv() {
        String result = html("> Some note");
        assertTrue(result.contains("<div class=\"callout\">"));
        assertTrue(result.contains("Some note"));
    }

    @Test
    void tipBlockquoteGetsTipClass() {
        String result = html("> **Tip:** Do this");
        assertTrue(result.contains("class=\"callout tip\""));
        assertTrue(result.contains("Do this"));
    }

    @Test
    void warningBlockquoteGetsWarnClass() {
        String result = html("> **Warning:** Watch out");
        assertTrue(result.contains("class=\"callout warn\""));
        assertTrue(result.contains("Watch out"));
    }

    @Test
    void multiLineBlockquoteIsMerged() {
        String result = html("> Line one\n> Line two");
        assertEquals(1, countOccurrences(result, "<div class=\"callout\">"));
        assertTrue(result.contains("Line one"));
        assertTrue(result.contains("Line two"));
    }

    // --- Inline formatting ---

    @Test
    void boldTextIsRendered() {
        assertTrue(html("Some **bold** text").contains("<strong>bold</strong>"));
    }

    @Test
    void italicTextIsRendered() {
        assertTrue(html("Some *italic* text").contains("<em>italic</em>"));
    }

    @Test
    void inlineCodeIsRendered() {
        assertTrue(html("Use `code` here").contains("<code>code</code>"));
    }

    @Test
    void linkIsRendered() {
        String result = html("[click here](https://example.com)");
        assertTrue(result.contains("<a href=\"https://example.com\">click here</a>"));
    }

    // --- HTML escaping ---

    @Test
    void ampersandIsEscaped() {
        assertTrue(html("A & B").contains("A &amp; B"));
    }

    @Test
    void lessThanIsEscaped() {
        assertTrue(html("a < b").contains("a &lt; b"));
    }

    @Test
    void greaterThanIsEscaped() {
        assertTrue(html("a > b").contains("a &gt; b"));
    }

    // --- Images ---

    @Test
    void imageRendersAsFigureWithResolvedSrc() {
        String result = MarkdownToHtml.toHtml(
            "![Alt text](images/schematic.svg)",
            src -> "data:image/svg+xml;base64,abc");
        assertTrue(result.contains("<figure>"));
        assertTrue(result.contains("src=\"data:image/svg+xml;base64,abc\""));
        assertTrue(result.contains("alt=\"Alt text\""));
        assertTrue(result.contains("<figcaption>Alt text</figcaption>"));
    }

    @Test
    void imageWithNullResolverKeepsOriginalSrc() {
        String result = html("![Alt](images/foo.svg)");
        assertTrue(result.contains("src=\"images/foo.svg\""));
    }

    @Test
    void imageResolverReturningNullKeepsOriginalSrc() {
        String result = MarkdownToHtml.toHtml("![Alt](images/foo.svg)", src -> null);
        assertTrue(result.contains("src=\"images/foo.svg\""));
    }

    @Test
    void imageWithBlankAltHasNoFigcaption() {
        String result = html("![](images/foo.svg)");
        assertFalse(result.contains("<figcaption>"));
    }

    // --- Empty and edge cases ---

    @Test
    void emptyStringReturnsEmpty() {
        assertEquals("", html(""));
    }

    @Test
    void blankLinesOnlyReturnEmpty() {
        assertEquals("", html("\n\n\n").trim());
    }

    @Test
    void listFollowedByParagraphClosesListCorrectly() {
        String result = html("- Item\n\nParagraph");
        assertTrue(result.contains("</ul>"));
        assertTrue(result.contains("<p>Paragraph</p>"));
        int ulClose = result.indexOf("</ul>");
        int pOpen = result.indexOf("<p>Paragraph");
        assertTrue(ulClose < pOpen);
    }

    @Test
    void orderedListFollowedByParagraphClosesListCorrectly() {
        String result = html("1. Item\n\nParagraph");
        assertTrue(result.contains("</ol>"));
        assertTrue(result.contains("<p>Paragraph</p>"));
    }

    private static int countOccurrences(String text, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
