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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/** Controls the floating guides reference panel. */
public final class GuidesPanelController implements Initializable {

    @FXML private ListView<BindingGuide> guideListView;
    @FXML private Label guideTitleLabel;
    @FXML private Label guideMetaLabel;
    @FXML private TextArea guideBodyArea;

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
            guideTitleLabel.setText("Could not load guides");
            guideBodyArea.setText(e.getMessage());
        }
    }

    private void showGuide(BindingGuide guide) {
        if (guide == null) {
            guideTitleLabel.setText("");
            guideMetaLabel.setText("");
            guideBodyArea.setText("");
            return;
        }
        guideTitleLabel.setText(guide.getMetadata().getTitle());
        String subtitle = guide.getMetadata().getSubtitle();
        String difficulty = guide.getMetadata().getDifficulty();
        String time = guide.getMetadata().getTimeEstimate();
        StringBuilder meta = new StringBuilder();
        if (subtitle != null && !subtitle.isBlank()) {
            meta.append(subtitle);
        }
        if (difficulty != null && !difficulty.isBlank()) {
            if (meta.length() > 0) {
                meta.append("  ·  ");
            }
            meta.append("Difficulty: ").append(difficulty);
        }
        if (time != null && !time.isBlank()) {
            if (meta.length() > 0) {
                meta.append("  ·  ");
            }
            meta.append("Time: ").append(time);
        }
        guideMetaLabel.setText(meta.toString());
        guideBodyArea.setText(guide.getBody());
    }
}
