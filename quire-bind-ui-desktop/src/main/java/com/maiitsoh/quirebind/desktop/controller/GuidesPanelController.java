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
package com.maiitsoh.quirebind.desktop.controller;

import com.maiitsoh.quirebind.guides.loader.GuideLoader;
import com.maiitsoh.quirebind.guides.model.BindingGuide;
import com.maiitsoh.quirebind.guides.renderer.MarkdownToHtml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/** Controls the floating guides reference panel. */
public final class GuidesPanelController implements Initializable {

    private static final String CSS = """
            body { font-family: -apple-system, 'Segoe UI', sans-serif; font-size: 13px;
                   line-height: 1.6; color: #1a1a1a; margin: 0 16px 24px 16px; }
            h1 { font-size: 1.5em; margin-top: 1.2em; }
            h2 { font-size: 1.2em; border-bottom: 1px solid #ddd; padding-bottom: 4px; }
            h3 { font-size: 1.05em; }
            h4 { font-size: 1em; font-style: italic; }
            code { background: #f4f4f4; padding: 1px 4px; border-radius: 3px;
                   font-family: monospace; font-size: 0.92em; }
            hr { border: none; border-top: 1px solid #ddd; margin: 16px 0; }
            figure { margin: 16px 0; text-align: center; }
            figure img { max-width: 100%; height: auto; }
            figcaption { font-size: 0.85em; color: #666; margin-top: 4px; }
            .callout { background: #f0f4ff; border-left: 4px solid #4a7fb5;
                       padding: 8px 12px; margin: 12px 0; border-radius: 0 4px 4px 0; }
            .callout.tip { background: #f0fff4; border-color: #3a9e6a; }
            .callout.warn { background: #fff8f0; border-color: #c97a2a; }
            ol, ul { padding-left: 24px; }
            li { margin-bottom: 3px; }
            a { color: #4a7fb5; }
            """;

    @FXML private ListView<BindingGuide> guideListView;
    @FXML private WebView guideWebView;

    private String currentTechniqueId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        guideListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BindingGuide guide, boolean empty) {
                super.updateItem(guide, empty);
                if (empty || guide == null) {
                    setText(null);
                } else {
                    setText(guide.getMetadata().getTitle()
                        + "  [" + guide.getMetadata().getLocale() + "]");
                }
            }
        });

        guideListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showGuide(newVal));

        loadGuides();
    }

    private void loadGuides() {
        try {
            List<BindingGuide> guides = GuideLoader.loadAll();
            guideListView.getItems().setAll(guides);
            if (!guides.isEmpty()) {
                guideListView.getSelectionModel().selectFirst();
            }
        } catch (IOException e) {
            guideListView.getItems().setAll(Collections.emptyList());
            renderError("Could not load guides", e.getMessage());
        }
    }

    private void showGuide(BindingGuide guide) {
        if (guide == null) {
            guideWebView.getEngine().loadContent("<html><body></body></html>", "text/html");
            return;
        }
        currentTechniqueId = guide.getMetadata().getTechniqueId();
        String bodyHtml = MarkdownToHtml.toHtml(guide.getBody(), this::resolveImage);
        String full = buildPage(guide, bodyHtml);
        guideWebView.getEngine().loadContent(full, "text/html");
    }

    private String buildPage(BindingGuide guide, String bodyHtml) {
        var meta = guide.getMetadata();
        StringBuilder header = new StringBuilder();
        header.append("<h1>").append(escHtml(meta.getTitle())).append("</h1>\n");
        if (meta.getSubtitle() != null && !meta.getSubtitle().isBlank()) {
            header.append("<p class=\"subtitle\">").append(escHtml(meta.getSubtitle()))
                  .append("</p>\n");
        }
        String difficulty = meta.getDifficulty();
        String time = meta.getTimeEstimate();
        if ((difficulty != null && !difficulty.isBlank())
                || (time != null && !time.isBlank())) {
            header.append("<p class=\"meta\">");
            if (difficulty != null && !difficulty.isBlank()) {
                header.append("Difficulty: <strong>").append(escHtml(difficulty))
                      .append("</strong>");
            }
            if (time != null && !time.isBlank()) {
                if (difficulty != null && !difficulty.isBlank()) {
                    header.append("&nbsp;&nbsp;·&nbsp;&nbsp;");
                }
                header.append("Time: <strong>").append(escHtml(time)).append("</strong>");
            }
            header.append("</p>\n");
        }
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"/>"
            + "<style>" + CSS
            + ".subtitle { color: #555; margin-top:-8px; font-style:italic; }"
            + ".meta { color: #555; font-size: 0.9em; }"
            + "</style></head><body>"
            + header
            + bodyHtml
            + "</body></html>";
    }

    private String resolveImage(String src) {
        if (currentTechniqueId == null) {
            return null;
        }
        String dirId = currentTechniqueId.replace('_', '-');
        String resourcePath = "/guides/" + dirId + "/" + src;
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                return null;
            }
            byte[] bytes = in.readAllBytes();
            String mimeType = src.endsWith(".svg") ? "image/svg+xml" : "image/png";
            return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    private void renderError(String title, String message) {
        guideWebView.getEngine().loadContent(
            "<html><body><h2>" + escHtml(title) + "</h2><p>" + escHtml(message)
            + "</p></body></html>", "text/html");
    }

    private static String escHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
