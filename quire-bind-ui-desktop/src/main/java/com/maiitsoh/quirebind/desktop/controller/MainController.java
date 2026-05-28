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

import com.maiitsoh.quirebind.core.imposition.ImpositionEngine;
import com.maiitsoh.quirebind.core.model.BindingTechnique;
import com.maiitsoh.quirebind.core.model.CreepConfig;
import com.maiitsoh.quirebind.core.model.ImpositionLayout;
import com.maiitsoh.quirebind.core.model.MarkConfig;
import com.maiitsoh.quirebind.core.model.NumberingConfig;
import com.maiitsoh.quirebind.core.model.PaddingConfig;
import com.maiitsoh.quirebind.core.model.PaperSize;
import com.maiitsoh.quirebind.core.model.QuireProject;
import com.maiitsoh.quirebind.core.model.ReadingDirection;
import com.maiitsoh.quirebind.core.model.Signature;
import com.maiitsoh.quirebind.core.pdf.PdfImpositionWriter;
import com.maiitsoh.quirebind.core.pdf.PdfPageLoader;
import com.maiitsoh.quirebind.desktop.state.WizardState;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

/** Controls the four-step imposition wizard in the main window. */
public final class MainController implements Initializable {

    private static final int TOTAL_STEPS = 4;

    // ── Wizard shell ────────────────────────────────────────────────────────
    @FXML private StackPane wizardStack;
    @FXML private VBox stepLoad;
    @FXML private VBox stepOptions;
    @FXML private VBox stepPreview;
    @FXML private VBox stepExport;
    @FXML private Button backButton;
    @FXML private Button nextButton;
    @FXML private Label stepIndicatorLabel;
    @FXML private Label statusLabel;

    // ── Step 1: Load PDF ────────────────────────────────────────────────────
    @FXML private TextField pdfPathField;
    @FXML private Label pageCountLabel;

    // ── Step 2: Binding options ─────────────────────────────────────────────
    @FXML private ComboBox<BindingTechnique> techniqueCombo;
    @FXML private ComboBox<PaperSize> paperSizeCombo;
    @FXML private Spinner<Integer> sigSizeSpinner;
    @FXML private ComboBox<ReadingDirection> directionCombo;
    @FXML private TextField thicknessField;

    // ── Step 3: Preview ──────────────────────────────────────────────────────
    @FXML private TableView<SignatureRow> signaturesTable;
    @FXML private TableColumn<SignatureRow, Number> colSigIndex;
    @FXML private TableColumn<SignatureRow, Number> colPageCount;
    @FXML private TableColumn<SignatureRow, Number> colSheetCount;
    @FXML private TableColumn<SignatureRow, String> colCreep;
    @FXML private Label impositionSummaryLabel;

    // ── Step 4: Export ───────────────────────────────────────────────────────
    @FXML private TextField outputPathField;
    @FXML private Label exportResultLabel;
    @FXML private Button exportButton;

    // ── State ────────────────────────────────────────────────────────────────
    private final WizardState state = new WizardState();
    private int currentStep = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        techniqueCombo.setItems(FXCollections.observableArrayList(BindingTechnique.values()));
        techniqueCombo.setValue(BindingTechnique.SADDLE_STITCH);

        paperSizeCombo.setItems(FXCollections.observableArrayList(PaperSize.values()));
        paperSizeCombo.setValue(PaperSize.A4);

        sigSizeSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 16, 4));
        sigSizeSpinner.setEditable(true);

        directionCombo.setItems(FXCollections.observableArrayList(ReadingDirection.values()));
        directionCombo.setValue(ReadingDirection.LTR);

        colSigIndex.setCellValueFactory(r -> new SimpleIntegerProperty(r.getValue().index()));
        colPageCount.setCellValueFactory(r -> new SimpleIntegerProperty(r.getValue().pageCount()));
        colSheetCount.setCellValueFactory(r -> new SimpleIntegerProperty(r.getValue().sheetCount()));
        colCreep.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().creepSummary()));

        showStep(0);
    }

    // ── Menu actions ─────────────────────────────────────────────────────────

    @FXML
    private void handleMenuOpenPdf() {
        File chosen = pdfChooser("Open PDF").showOpenDialog(
            wizardStack.getScene().getWindow());
        if (chosen != null) {
            loadPdf(chosen.toPath());
        }
    }

    @FXML
    private void handleMenuNewProject() {
        state.setInputPdf(null);
        state.setImpositionResult(null);
        pdfPathField.clear();
        pageCountLabel.setText("");
        outputPathField.clear();
        exportResultLabel.setText("");
        showStep(0);
        setStatus("New project started.");
    }

    @FXML
    private void handleMenuExit() {
        Platform.exit();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About QuireBind");
        alert.setHeaderText("QuireBind 1.0.0-SNAPSHOT");
        alert.setContentText(
            "FOSS desktop application for preparing PDF files for bookbinding.\n"
            + "Licensed under the GNU Affero General Public License v3.0.");
        alert.showAndWait();
    }

    // ── Step 1 actions ───────────────────────────────────────────────────────

    @FXML
    private void handleBrowseInput() {
        File chosen = pdfChooser("Select Source PDF").showOpenDialog(
            wizardStack.getScene().getWindow());
        if (chosen != null) {
            loadPdf(chosen.toPath());
        }
    }

    private void loadPdf(Path path) {
        try {
            var sequence = PdfPageLoader.load(path);
            state.setInputPdf(path);
            state.setPageCount(sequence.pageCount());
            pdfPathField.setText(path.toString());
            pageCountLabel.setText("Pages loaded: " + sequence.pageCount());
            setStatus("Loaded " + path.getFileName() + " — " + sequence.pageCount() + " pages.");
        } catch (IOException e) {
            showError("Failed to load PDF", e.getMessage());
        }
    }

    // ── Step 3 actions ───────────────────────────────────────────────────────

    private void runImposition() {
        try {
            var sequence = PdfPageLoader.load(state.getInputPdf());

            double thickness = parseThickness();
            CreepConfig creepConfig = thickness > 0
                ? CreepConfig.builder().paperThicknessMm(thickness).build()
                : CreepConfig.builder().build();

            int sigSize = sigSizeSpinner.getValue();

            QuireProject project = QuireProject.builder()
                .name(state.getInputPdf().getFileName().toString())
                .bindingTechnique(state.getTechnique())
                .paperSize(state.getPaperSize())
                .readingDirection(state.getReadingDirection())
                .layout(ImpositionLayout.FOLIO)
                .pageSequence(sequence)
                .paddingConfig(PaddingConfig.builder().signatureSize(sigSize).build())
                .numberingConfig(NumberingConfig.builder().build())
                .markConfig(MarkConfig.builder().build())
                .creepConfig(creepConfig)
                .build();

            List<Signature> sigs = ImpositionEngine.impose(project);
            state.setImpositionResult(sigs);

            int totalSheets = sigs.stream().mapToInt(s -> s.getSheets().size()).sum();
            impositionSummaryLabel.setText(
                sigs.size() + " signature(s) · " + totalSheets + " sheet(s) total");

            signaturesTable.setItems(FXCollections.observableArrayList(
                sigs.stream().map(SignatureRow::from).toList()));

            setStatus("Imposition complete: " + sigs.size() + " signatures.");
        } catch (IOException e) {
            showError("Imposition failed", e.getMessage());
        }
    }

    private double parseThickness() {
        String text = thicknessField.getText().trim();
        if (text.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // ── Step 4 actions ───────────────────────────────────────────────────────

    @FXML
    private void handleBrowseOutput() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Imposed PDF As");
        chooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
        File chosen = chooser.showSaveDialog(wizardStack.getScene().getWindow());
        if (chosen != null) {
            state.setOutputPdf(chosen.toPath());
            outputPathField.setText(chosen.toString());
        }
    }

    @FXML
    private void handleExport() {
        if (state.getOutputPdf() == null) {
            showError("No output path", "Please choose an output file first.");
            return;
        }
        try {
            PdfImpositionWriter.write(
                state.getImpositionResult(),
                state.getInputPdf(),
                state.getOutputPdf(),
                state.getPaperSize());
            exportResultLabel.setText("Exported successfully to: " + state.getOutputPdf());
            setStatus("Export complete: " + state.getOutputPdf().getFileName());
            exportButton.setDisable(true);
        } catch (IOException e) {
            showError("Export failed", e.getMessage());
        }
    }

    // ── Wizard navigation ────────────────────────────────────────────────────

    @FXML
    private void handleBack() {
        if (currentStep > 0) {
            showStep(currentStep - 1);
        }
    }

    @FXML
    private void handleNext() {
        if (!validateCurrentStep()) {
            return;
        }
        if (currentStep < TOTAL_STEPS - 1) {
            int next = currentStep + 1;
            if (next == 2) {
                collectOptionsState();
                runImposition();
            }
            if (next == 3) {
                exportButton.setDisable(false);
                exportResultLabel.setText("");
            }
            showStep(next);
        }
    }

    private boolean validateCurrentStep() {
        return switch (currentStep) {
            case 0 -> {
                if (!state.hasInputPdf()) {
                    showError("No PDF selected", "Please select a source PDF before continuing.");
                    yield false;
                }
                yield true;
            }
            case 1 -> {
                collectOptionsState();
                yield true;
            }
            case 2 -> {
                if (!state.hasImpositionResult()) {
                    showError("Imposition not run", "Imposition must complete before exporting.");
                    yield false;
                }
                yield true;
            }
            default -> true;
        };
    }

    private void collectOptionsState() {
        if (techniqueCombo.getValue() != null) {
            state.setTechnique(techniqueCombo.getValue());
        }
        if (paperSizeCombo.getValue() != null) {
            state.setPaperSize(paperSizeCombo.getValue());
        }
        if (sigSizeSpinner.getValue() != null) {
            state.setPagesPerSignature(sigSizeSpinner.getValue());
        }
        if (directionCombo.getValue() != null) {
            state.setReadingDirection(directionCombo.getValue());
        }
        state.setPaperThicknessMm(parseThickness());
    }

    private void showStep(int step) {
        currentStep = step;
        List<VBox> steps = List.of(stepLoad, stepOptions, stepPreview, stepExport);
        for (int i = 0; i < steps.size(); i++) {
            boolean visible = i == step;
            steps.get(i).setVisible(visible);
            steps.get(i).setManaged(visible);
        }
        stepIndicatorLabel.setText("Step " + (step + 1) + " of " + TOTAL_STEPS);
        backButton.setDisable(step == 0);
        boolean isLast = step == TOTAL_STEPS - 1;
        nextButton.setText(isLast ? "Finish" : "Next →");
        nextButton.setDisable(isLast);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private FileChooser pdfChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
        return chooser;
    }

    private void showError(String header, String body) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("QuireBind");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.showAndWait();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    // ── Inner model for TableView ────────────────────────────────────────────

    /**
     * Flat row model derived from {@link Signature} for display in the preview table.
     */
    public record SignatureRow(int index, int pageCount, int sheetCount, String creepSummary) {

        static SignatureRow from(Signature sig) {
            double maxCreep = sig.getSheets().stream()
                .mapToDouble(s -> s.getCreepResult()
                    .map(r -> r.getCreepMm()).orElse(0.0))
                .max().orElse(0.0);
            String creep = maxCreep > 0
                ? String.format("%.3f mm max", maxCreep)
                : "—";
            return new SignatureRow(
                sig.getSignatureIndex() + 1,
                sig.getLogicalPageNumbers().size(),
                sig.getSheets().size(),
                creep);
        }
    }
}
