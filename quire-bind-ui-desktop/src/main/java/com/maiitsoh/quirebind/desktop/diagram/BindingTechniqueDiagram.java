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
package com.maiitsoh.quirebind.desktop.diagram;

import com.maiitsoh.quirebind.core.model.BindingTechnique;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Draws a schematic diagram for each {@link BindingTechnique} onto a JavaFX {@link Canvas}.
 *
 * <p>All diagrams share a fixed 260×190 canvas. The style is intentionally minimal —
 * clean line drawings with a muted palette so they complement rather than distract
 * from the option form beside them.
 */
public final class BindingTechniqueDiagram {

    /** Width of every diagram canvas in pixels. */
    public static final double W = 260;

    /** Height of every diagram canvas in pixels. */
    public static final double H = 190;

    private static final Color BG         = Color.web("#fafafa");
    private static final Color PAGE_FILL  = Color.web("#e8eef6");
    private static final Color PAGE_EDGE  = Color.web("#7a9ac0");
    private static final Color SPINE_COL  = Color.web("#3c6ea6");
    private static final Color THREAD_COL = Color.web("#c04040");
    private static final Color GLUE_COL   = Color.web("#5a9e5a");
    private static final Color WIRE_COL   = Color.web("#888888");
    private static final Color LABEL_COL  = Color.web("#444444");
    private static final Color COVER_COL  = Color.web("#2a4a6a");

    private static final Font LABEL_FONT = Font.font("SansSerif", 10);

    private BindingTechniqueDiagram() { }

    /**
     * Creates and returns a fully drawn {@link Canvas} for the given technique.
     *
     * @param technique the binding technique to illustrate; must not be null
     * @return a new {@link Canvas} instance with the schematic already painted
     */
    public static Canvas forTechnique(BindingTechnique technique) {
        Canvas canvas = new Canvas(W, H);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(BG);
        g.fillRect(0, 0, W, H);
        switch (technique) {
            case SADDLE_STITCH -> drawSaddleStitch(g);
            case PAMPHLET      -> drawPamphlet(g);
            case BOOKLET       -> drawBooklet(g);
            case PERFECT_BINDING -> drawPerfectBinding(g);
            case SPIRAL        -> drawSpiral(g);
            case JAPANESE_STAB -> drawJapaneseStab(g);
            case SEWN_SIGNATURES -> drawSewnSignatures(g);
            case HARDCOVER     -> drawHardcover(g);
            case COPTIC        -> drawCoptic(g);
            default            -> drawGeneric(g);
        }
        return canvas;
    }

    // ── Saddle Stitch ────────────────────────────────────────────────────────

    private static void drawSaddleStitch(GraphicsContext g) {
        drawLabel(g, "Saddle Stitch — nested folded sheets, stitched at spine");
        int sheets = 4;
        double cx = W / 2;
        double topY = 30;
        double pageH = 110;
        double pageW = 75;
        double inset = 6;
        for (int i = sheets - 1; i >= 0; i--) {
            double off = i * inset;
            double x1 = cx - pageW - off;
            double x2 = cx + pageW + off;
            double y1 = topY + off;
            double y2 = topY + pageH - off;
            g.setFill(PAGE_FILL);
            g.fillRect(x1, y1, pageW + off, y2 - y1);
            g.fillRect(cx, y1, pageW + off, y2 - y1);
            g.setStroke(PAGE_EDGE);
            g.setLineWidth(1.0);
            g.strokeRect(x1, y1, pageW + off, y2 - y1);
            g.strokeRect(cx, y1, pageW + off, y2 - y1);
        }
        // spine fold line
        g.setStroke(SPINE_COL);
        g.setLineWidth(2.0);
        g.strokeLine(cx, topY - 4, cx, topY + pageH + 4);
        // thread marks at spine
        g.setStroke(THREAD_COL);
        g.setLineWidth(1.5);
        for (int i = 1; i <= 3; i++) {
            double ty = topY + i * (pageH / 4.0);
            g.strokeLine(cx - 4, ty, cx + 4, ty);
        }
        drawSmallLabel(g, "fold / spine", cx, topY + pageH + 16);
        drawSmallLabel(g, "thread", cx + 20, topY + pageH / 2.0);
    }

    // ── Pamphlet ─────────────────────────────────────────────────────────────

    private static void drawPamphlet(GraphicsContext g) {
        drawLabel(g, "Pamphlet — single folded sheet");
        double cx = W / 2;
        double topY = 35;
        double pageH = 110;
        double pageW = 80;
        g.setFill(PAGE_FILL);
        g.fillRect(cx - pageW, topY, pageW, pageH);
        g.fillRect(cx, topY, pageW, pageH);
        g.setStroke(PAGE_EDGE);
        g.setLineWidth(1.0);
        g.strokeRect(cx - pageW, topY, pageW, pageH);
        g.strokeRect(cx, topY, pageW, pageH);
        g.setStroke(SPINE_COL);
        g.setLineWidth(2.5);
        g.strokeLine(cx, topY - 4, cx, topY + pageH + 4);
        // single stitch
        g.setStroke(THREAD_COL);
        g.setLineWidth(1.5);
        g.strokeLine(cx - 5, topY + pageH / 2.0, cx + 5, topY + pageH / 2.0);
        drawSmallLabel(g, "fold", cx, topY + pageH + 16);
    }

    // ── Booklet ──────────────────────────────────────────────────────────────

    private static void drawBooklet(GraphicsContext g) {
        drawLabel(g, "Booklet — multiple nested signatures");
        int sigs = 5;
        double cx = W / 2;
        double topY = 28;
        double pageH = 120;
        double pageW = 70;
        double inset = 5;
        for (int i = sigs - 1; i >= 0; i--) {
            double off = i * inset;
            g.setFill(i == 0 ? PAGE_FILL : Color.web("#ccd8eb"));
            g.fillRect(cx - pageW - off, topY + off, pageW + off, pageH - off * 2);
            g.fillRect(cx, topY + off, pageW + off, pageH - off * 2);
            g.setStroke(PAGE_EDGE);
            g.setLineWidth(0.8);
            g.strokeRect(cx - pageW - off, topY + off, pageW + off, pageH - off * 2);
            g.strokeRect(cx, topY + off, pageW + off, pageH - off * 2);
        }
        g.setStroke(SPINE_COL);
        g.setLineWidth(2.5);
        g.strokeLine(cx, topY - 4, cx, topY + pageH + 4);
        g.setStroke(THREAD_COL);
        g.setLineWidth(1.5);
        for (int i = 1; i <= 4; i++) {
            double ty = topY + i * (pageH / 5.0);
            g.strokeLine(cx - 5, ty, cx + 5, ty);
        }
        drawSmallLabel(g, "spine", cx, topY + pageH + 16);
    }

    // ── Perfect Binding ──────────────────────────────────────────────────────

    private static void drawPerfectBinding(GraphicsContext g) {
        drawLabel(g, "Perfect Binding — pages glued at trimmed spine");
        double bx = 70;
        double by = 30;
        double bw = 120;
        double bh = 130;
        double spineW = 14;
        int lineCount = 14;
        // page block
        g.setFill(PAGE_FILL);
        g.fillRect(bx + spineW, by, bw - spineW, bh);
        g.setStroke(PAGE_EDGE);
        g.setLineWidth(0.7);
        for (int i = 1; i < lineCount; i++) {
            double ly = by + i * (bh / (double) lineCount);
            g.strokeLine(bx + spineW, ly, bx + bw, ly);
        }
        g.setStroke(PAGE_EDGE);
        g.setLineWidth(1.2);
        g.strokeRect(bx + spineW, by, bw - spineW, bh);
        // glue spine
        g.setFill(GLUE_COL);
        g.fillRect(bx, by, spineW, bh);
        g.setStroke(GLUE_COL.darker());
        g.setLineWidth(1.0);
        g.strokeRect(bx, by, spineW, bh);
        drawSmallLabel(g, "glue", bx + spineW / 2.0, by + bh + 14);
        drawSmallLabel(g, "pages", bx + spineW + (bw - spineW) / 2.0, by + bh + 14);
    }

    // ── Spiral ───────────────────────────────────────────────────────────────

    private static void drawSpiral(GraphicsContext g) {
        drawLabel(g, "Spiral — wire coil through punched holes");
        double bx = 80;
        double by = 28;
        double bw = 110;
        double bh = 130;
        double holeX = bx + 14;
        double holeR = 3.5;
        int holeCount = 8;
        // pages
        g.setFill(PAGE_FILL);
        g.fillRect(bx + 22, by, bw - 22, bh);
        g.setStroke(PAGE_EDGE);
        g.setLineWidth(0.7);
        int lineCount = 14;
        for (int i = 1; i < lineCount; i++) {
            double ly = by + i * (bh / (double) lineCount);
            g.strokeLine(bx + 22, ly, bx + bw, ly);
        }
        g.setStroke(PAGE_EDGE);
        g.setLineWidth(1.2);
        g.strokeRect(bx + 22, by, bw - 22, bh);
        // holes
        g.setFill(BG);
        g.setStroke(PAGE_EDGE);
        g.setLineWidth(1.0);
        double holeStep = bh / (holeCount + 1.0);
        for (int i = 1; i <= holeCount; i++) {
            double hy = by + i * holeStep;
            g.fillOval(holeX - holeR, hy - holeR, holeR * 2, holeR * 2);
            g.strokeOval(holeX - holeR, hy - holeR, holeR * 2, holeR * 2);
        }
        // spiral wire — series of arcs alternating sides
        g.setStroke(WIRE_COL);
        g.setLineWidth(2.0);
        for (int i = 0; i < holeCount + 1; i++) {
            double y0 = by + i * holeStep;
            double y1 = y0 + holeStep;
            double mx = holeX;
            // oval arc representing coil
            if (i % 2 == 0) {
                g.strokeArc(mx - 7, y0, 14, y1 - y0, 0, 180, javafx.scene.shape.ArcType.OPEN);
            } else {
                g.strokeArc(mx - 7, y0, 14, y1 - y0, 180, 180, javafx.scene.shape.ArcType.OPEN);
            }
        }
        drawSmallLabel(g, "spiral wire", holeX, by + bh + 14);
    }

    // ── Japanese Stab ────────────────────────────────────────────────────────

    private static void drawJapaneseStab(GraphicsContext g) {
        drawLabel(g, "Japanese Stab — stitched through cover near spine");
        double bx = 65;
        double by = 28;
        double bw = 130;
        double bh = 130;
        double stabX = bx + 18;
        // pages
        g.setFill(PAGE_FILL);
        g.fillRect(bx, by, bw, bh);
        g.setStroke(PAGE_EDGE);
        g.setLineWidth(0.7);
        for (int i = 1; i < 14; i++) {
            g.strokeLine(bx, by + i * (bh / 14.0), bx + bw, by + i * (bh / 14.0));
        }
        g.setStroke(PAGE_EDGE);
        g.setLineWidth(1.2);
        g.strokeRect(bx, by, bw, bh);
        // stab holes
        int holeCount = 4;
        double[] holeY = new double[holeCount];
        holeY[0] = by + bh * 0.15;
        holeY[1] = by + bh * 0.38;
        holeY[2] = by + bh * 0.62;
        holeY[3] = by + bh * 0.85;
        g.setFill(BG);
        g.setStroke(THREAD_COL);
        g.setLineWidth(1.0);
        for (double hy : holeY) {
            g.fillOval(stabX - 3, hy - 3, 6, 6);
            g.strokeOval(stabX - 3, hy - 3, 6, 6);
        }
        // stitching lines connecting holes (front side)
        g.setStroke(THREAD_COL);
        g.setLineWidth(1.5);
        g.strokeLine(stabX, holeY[0], stabX, holeY[3]);
        // decorative wrap at each hole
        for (double hy : holeY) {
            g.strokeLine(stabX - 8, hy, stabX + 8, hy);
        }
        drawSmallLabel(g, "stab stitch", stabX, by + bh + 14);
    }

    // ── Sewn Signatures ──────────────────────────────────────────────────────

    private static void drawSewnSignatures(GraphicsContext g) {
        drawLabel(g, "Sewn Signatures — multiple signatures sewn together");
        int sigCount = 4;
        double sigH = 22;
        double sigW = 110;
        double spineW = 10;
        double startX = 75;
        double startY = 28;
        double gap = 4;
        for (int i = 0; i < sigCount; i++) {
            double sy = startY + i * (sigH + gap);
            // left page
            g.setFill(PAGE_FILL);
            g.fillRect(startX + spineW, sy, sigW, sigH);
            g.setStroke(PAGE_EDGE);
            g.setLineWidth(1.0);
            g.strokeRect(startX + spineW, sy, sigW, sigH);
            // spine mark
            g.setFill(SPINE_COL);
            g.fillRect(startX, sy, spineW, sigH);
            // thread dot at spine
            g.setFill(THREAD_COL);
            g.fillOval(startX + spineW / 2.0 - 2, sy + sigH / 2.0 - 2, 4, 4);
        }
        // sewing thread line along spine
        g.setStroke(THREAD_COL);
        g.setLineWidth(1.2);
        for (int i = 0; i < sigCount; i++) {
            double sy = startY + i * (sigH + gap);
            g.strokeLine(startX + spineW / 2.0, sy, startX + spineW / 2.0, sy + sigH);
        }
        // connecting thread between signatures
        g.setStroke(THREAD_COL);
        g.setLineDashes(3, 2);
        for (int i = 0; i < sigCount - 1; i++) {
            double y0 = startY + i * (sigH + gap) + sigH;
            double y1 = y0 + gap;
            g.strokeLine(startX + spineW / 2.0, y0, startX + spineW / 2.0, y1);
        }
        g.setLineDashes(null);
        drawSmallLabel(g, "spine", startX + spineW / 2.0, startY + sigCount * (sigH + gap) + 10);
        drawSmallLabel(g, "each block = one signature", startX + spineW + sigW / 2.0,
            startY + sigCount * (sigH + gap) + 10);
    }

    // ── Hardcover ────────────────────────────────────────────────────────────

    private static void drawHardcover(GraphicsContext g) {
        drawLabel(g, "Hardcover — sewn signatures in hard boards");
        int sigCount = 3;
        double sigH = 18;
        double sigW = 100;
        double spineW = 10;
        double boardThick = 8;
        double startX = 80;
        double startY = 40;
        double gap = 3;
        double totalH = sigCount * (sigH + gap) - gap;
        // back board
        g.setFill(COVER_COL);
        g.fillRect(startX - boardThick, startY - boardThick,
            sigW + spineW + boardThick * 2, boardThick);
        g.fillRect(startX - boardThick, startY + totalH,
            sigW + spineW + boardThick * 2, boardThick);
        g.setStroke(COVER_COL.darker());
        g.setLineWidth(1.0);
        g.strokeRect(startX - boardThick, startY - boardThick,
            sigW + spineW + boardThick * 2, boardThick);
        g.strokeRect(startX - boardThick, startY + totalH,
            sigW + spineW + boardThick * 2, boardThick);
        // signatures
        for (int i = 0; i < sigCount; i++) {
            double sy = startY + i * (sigH + gap);
            g.setFill(PAGE_FILL);
            g.fillRect(startX + spineW, sy, sigW, sigH);
            g.setStroke(PAGE_EDGE);
            g.setLineWidth(0.8);
            g.strokeRect(startX + spineW, sy, sigW, sigH);
            g.setFill(SPINE_COL);
            g.fillRect(startX, sy, spineW, sigH);
        }
        // thread
        g.setStroke(THREAD_COL);
        g.setLineWidth(1.5);
        g.strokeLine(startX + spineW / 2.0, startY, startX + spineW / 2.0, startY + totalH);
        drawSmallLabel(g, "boards", startX + spineW + sigW / 2.0, startY + totalH + boardThick + 14);
        drawSmallLabel(g, "spine", startX + spineW / 2.0, startY + totalH + boardThick + 14);
    }

    // ── Coptic ───────────────────────────────────────────────────────────────

    private static void drawCoptic(GraphicsContext g) {
        drawLabel(g, "Coptic — exposed chain stitch, lays flat");
        int sigCount = 4;
        double sigH = 20;
        double sigW = 100;
        double spineW = 4;
        double startX = 80;
        double startY = 32;
        double gap = 6;
        for (int i = 0; i < sigCount; i++) {
            double sy = startY + i * (sigH + gap);
            g.setFill(PAGE_FILL);
            g.fillRect(startX + spineW, sy, sigW, sigH);
            g.setStroke(PAGE_EDGE);
            g.setLineWidth(1.0);
            g.strokeRect(startX + spineW, sy, sigW, sigH);
        }
        // exposed chain stitching between signatures
        int stitchPoints = 3;
        g.setStroke(THREAD_COL);
        g.setLineWidth(1.5);
        for (int s = 0; s < sigCount - 1; s++) {
            double y0 = startY + s * (sigH + gap) + sigH;
            double y1 = y0 + gap;
            for (int p = 0; p < stitchPoints; p++) {
                double px = startX + (p + 1) * (spineW + 2);
                g.strokeLine(px, y0, px, y1);
                // chain links
                if (s > 0) {
                    g.strokeArc(px - 3, y0 - 4, 6, 8, 0, 180, javafx.scene.shape.ArcType.OPEN);
                }
            }
        }
        // thread marks on each signature spine
        for (int i = 0; i < sigCount; i++) {
            double sy = startY + i * (sigH + gap);
            for (int p = 0; p < stitchPoints; p++) {
                double px = startX + (p + 1) * (spineW + 2);
                g.setFill(THREAD_COL);
                g.fillOval(px - 2, sy + sigH / 2.0 - 2, 4, 4);
            }
        }
        drawSmallLabel(g, "exposed chain stitch at spine", startX + spineW + sigW / 2.0,
            startY + sigCount * (sigH + gap) + 10);
    }

    // ── Generic fallback ─────────────────────────────────────────────────────

    private static void drawGeneric(GraphicsContext g) {
        drawLabel(g, "Binding technique");
        double bx = 80;
        double by = 50;
        double bw = 100;
        double bh = 100;
        g.setFill(PAGE_FILL);
        g.fillRect(bx, by, bw, bh);
        g.setStroke(PAGE_EDGE);
        g.setLineWidth(1.2);
        g.strokeRect(bx, by, bw, bh);
        g.setStroke(SPINE_COL);
        g.setLineWidth(2.5);
        g.strokeLine(bx, by, bx, by + bh);
    }

    // ── Shared helpers ───────────────────────────────────────────────────────

    private static void drawLabel(GraphicsContext g, String text) {
        g.setFont(Font.font("SansSerif", 10));
        g.setFill(LABEL_COL);
        g.setTextAlign(TextAlignment.CENTER);
        g.fillText(text, W / 2, H - 8, W - 8);
    }

    private static void drawSmallLabel(GraphicsContext g, String text, double cx, double y) {
        g.setFont(LABEL_FONT);
        g.setFill(LABEL_COL);
        g.setTextAlign(TextAlignment.CENTER);
        g.fillText(text, cx, y);
    }
}
