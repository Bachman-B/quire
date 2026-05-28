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

import com.maiitsoh.quirebind.batch.parser.QuireFileParser;
import com.maiitsoh.quirebind.core.binding.BindingGroupMapper;
import com.maiitsoh.quirebind.core.imposition.ImpositionEngine;
import com.maiitsoh.quirebind.core.model.BindingTechnique;
import com.maiitsoh.quirebind.core.model.CreepConfig;
import com.maiitsoh.quirebind.core.model.ImpositionGroup;
import com.maiitsoh.quirebind.core.model.ImpositionLayout;
import com.maiitsoh.quirebind.core.model.MarkConfig;
import com.maiitsoh.quirebind.core.model.NumberingConfig;
import com.maiitsoh.quirebind.core.model.PaddingConfig;
import com.maiitsoh.quirebind.core.model.PageSequence;
import com.maiitsoh.quirebind.core.model.PageType;
import com.maiitsoh.quirebind.core.model.PaperSize;
import com.maiitsoh.quirebind.core.model.QuirePage;
import com.maiitsoh.quirebind.core.model.QuireProject;
import com.maiitsoh.quirebind.core.model.ReadingDirection;
import com.maiitsoh.quirebind.core.model.Signature;
import com.maiitsoh.quirebind.core.pdf.PdfImpositionWriter;
import com.maiitsoh.quirebind.core.pdf.PdfPageLoader;
import com.maiitsoh.quirebind.desktop.diagram.BindingTechniqueDiagram;
import com.maiitsoh.quirebind.desktop.state.WizardState;
import com.maiitsoh.quirebind.desktop.state.WizardState.WizardMode;
import com.maiitsoh.quirebind.desktop.template.QuireTemplateWriter;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

/** Controls the four-step imposition wizard in the main window. */
public final class MainController implements Initializable {

    private static final int TOTAL_STEPS = 4;

    // ── Wizard shell ─────────────────────────────────────────────────────────
    @FXML private StackPane wizardStack;
    @FXML private VBox stepLoad;
    @FXML private VBox stepOptions;
    @FXML private VBox stepPreview;
    @FXML private VBox stepExport;
    @FXML private Button backButton;
    @FXML private Button nextButton;
    @FXML private Label stepIndicatorLabel;
    @FXML private Label statusLabel;

    // ── Step 1: Load ─────────────────────────────────────────────────────────
    @FXML private ToggleButton singlePdfToggle;
    @FXML private ToggleButton batchToggle;
    @FXML private VBox singlePdfPane;
    @FXML private VBox batchPane;
    @FXML private TextField pdfPathField;
    @FXML private Label pageCountLabel;
    @FXML private TextField quirePathField;
    @FXML private Label jobCountLabel;

    // ── Step 2: Options ──────────────────────────────────────────────────────
    @FXML private ComboBox<BindingTechnique> techniqueCombo;
    @FXML private ComboBox<PaperSize> paperSizeCombo;
    @FXML private Spinner<Integer> sigSizeSpinner;
    @FXML private Label sigSizeLabel;
    @FXML private ComboBox<ReadingDirection> directionCombo;
    @FXML private TextField thicknessField;
    @FXML private StackPane diagramPane;

    // ── Step 3: Preview / edit ────────────────────────────────────────────────
    @FXML private ListView<PageItem> pageListView;
    @FXML private Label previewSummaryLabel;

    // ── Step 4: Export ───────────────────────────────────────────────────────
    @FXML private CheckBox foldLinesCheck;
    @FXML private CheckBox stitchMarksCheck;
    @FXML private CheckBox sewingHolesCheck;
    @FXML private CheckBox trimLinesCheck;
    @FXML private TableView<SignatureRow> signaturesTable;
    @FXML private TableColumn<SignatureRow, Number> colSigIndex;
    @FXML private TableColumn<SignatureRow, Number> colPageCount;
    @FXML private TableColumn<SignatureRow, Number> colSheetCount;
    @FXML private TableColumn<SignatureRow, String> colCreep;
    @FXML private TextField outputPathField;
    @FXML private Button exportButton;
    @FXML private Label exportResultLabel;

    // ── State ────────────────────────────────────────────────────────────────
    private final WizardState state = new WizardState();
    private int currentStep = 0;
    private ToggleGroup modeToggleGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mode toggle
        modeToggleGroup = new ToggleGroup();
        singlePdfToggle.setToggleGroup(modeToggleGroup);
        batchToggle.setToggleGroup(modeToggleGroup);
        singlePdfToggle.setSelected(true);
        modeToggleGroup.selectedToggleProperty().addListener(
            (obs, oldT, newT) -> onModeToggleChanged(newT));

        // Technique combo
        techniqueCombo.setItems(FXCollections.observableArrayList(BindingTechnique.values()));
        techniqueCombo.setValue(BindingTechnique.SADDLE_STITCH);
        techniqueCombo.setOnAction(e -> onTechniqueChanged());

        // Paper size
        paperSizeCombo.setItems(FXCollections.observableArrayList(PaperSize.values()));
        paperSizeCombo.setValue(PaperSize.A4);

        // Signature size spinner
        sigSizeSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 16, 4));
        sigSizeSpinner.setEditable(true);

        // Reading direction
        directionCombo.setItems(FXCollections.observableArrayList(ReadingDirection.values()));
        directionCombo.setValue(ReadingDirection.LTR);

        // Page list
        pageListView.setCellFactory(lv -> new PageListCell());
        pageListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, o, n) -> refreshPreviewSummary());

        // Imposition table columns
        colSigIndex.setCellValueFactory(r -> new SimpleIntegerProperty(r.getValue().index()));
        colPageCount.setCellValueFactory(r -> new SimpleIntegerProperty(r.getValue().pageCount()));
        colSheetCount.setCellValueFactory(
            r -> new SimpleIntegerProperty(r.getValue().sheetCount()));
        colCreep.setCellValueFactory(r -> new SimpleStringProperty(r.getValue().creepSummary()));

        // Initial diagram
        refreshDiagram();
        // Initial field visibility
        onTechniqueChanged();

        showStep(0);
    }

    // ── Menu actions ─────────────────────────────────────────────────────────

    @FXML
    private void handleMenuNewProject() {
        state.reset();
        pdfPathField.clear();
        pageCountLabel.setText("");
        quirePathField.clear();
        jobCountLabel.setText("");
        outputPathField.clear();
        exportResultLabel.setText("");
        pageListView.getItems().clear();
        showStep(0);
        setStatus("New project.");
    }

    @FXML
    private void handleMenuOpenPdf() {
        File f = pdfChooser("Open PDF").showOpenDialog(wizardStack.getScene().getWindow());
        if (f != null) {
            loadPdf(f.toPath());
            singlePdfToggle.setSelected(true);
            showStep(0);
        }
    }

    @FXML
    private void handleMenuExit() {
        Platform.exit();
    }

    @FXML
    private void handleMenuGuides() {
        openGuidesPanel();
    }

    @FXML
    private void handleAbout() {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("About QuireBind");
        a.setHeaderText("QuireBind 1.0.0-SNAPSHOT");
        a.setContentText(
            "FOSS desktop application for preparing PDF files for bookbinding.\n"
            + "Licensed under the GNU Affero General Public License v3.0.");
        a.showAndWait();
    }

    // ── Step 1 actions ────────────────────────────────────────────────────────

    private void onModeToggleChanged(Toggle selected) {
        if (selected == null) {
            // prevent deselection
            modeToggleGroup.selectToggle(singlePdfToggle);
            return;
        }
        boolean isSingle = selected == singlePdfToggle;
        state.setMode(isSingle ? WizardMode.SINGLE_PDF : WizardMode.BATCH);
        showNode(singlePdfPane, isSingle);
        showNode(batchPane, !isSingle);
    }

    @FXML
    private void handleBrowseInput() {
        File f = pdfChooser("Select Source PDF").showOpenDialog(
            wizardStack.getScene().getWindow());
        if (f != null) {
            loadPdf(f.toPath());
        }
    }

    @FXML
    private void handleBrowseQuire() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select .quire Batch File");
        fc.getExtensionFilters().add(new ExtensionFilter("Quire files", "*.quire"));
        File f = fc.showOpenDialog(wizardStack.getScene().getWindow());
        if (f != null) {
            loadQuireFile(f.toPath());
        }
    }

    private void loadPdf(Path path) {
        try {
            PageSequence seq = PdfPageLoader.load(path);
            state.setInputPdf(path);
            state.setPageCount(seq.pageCount());
            state.setPageSequence(seq);
            pdfPathField.setText(path.toString());
            pageCountLabel.setText("Pages loaded: " + seq.pageCount());
            setStatus("Loaded " + path.getFileName() + " — " + seq.pageCount() + " pages.");
        } catch (IOException e) {
            showError("Failed to load PDF", e.getMessage());
        }
    }

    private void loadQuireFile(Path path) {
        try {
            var config = QuireFileParser.parse(path);
            state.setBatchConfigPath(path);
            state.setBatchConfig(config);
            quirePathField.setText(path.toString());
            int jobCount = config.jobs() != null ? config.jobs().size() : 0;
            jobCountLabel.setText("Jobs loaded: " + jobCount);
            setStatus("Loaded batch config: " + path.getFileName()
                + " (" + jobCount + " job(s)).");
        } catch (IOException e) {
            showError("Failed to load .quire file", e.getMessage());
        }
    }

    // ── Step 2 actions ────────────────────────────────────────────────────────

    private void onTechniqueChanged() {
        BindingTechnique t = techniqueCombo.getValue();
        if (t == null) {
            return;
        }
        ImpositionGroup group = BindingGroupMapper.groupFor(t);
        boolean isGroupC = group == ImpositionGroup.C;
        boolean needsFoldMarks = group == ImpositionGroup.B || group == ImpositionGroup.C;

        showNode(sigSizeSpinner, isGroupC);
        showNode(sigSizeLabel, isGroupC);

        refreshDiagram();
    }

    private void refreshDiagram() {
        BindingTechnique t = techniqueCombo != null && techniqueCombo.getValue() != null
            ? techniqueCombo.getValue() : BindingTechnique.SADDLE_STITCH;
        Canvas canvas = BindingTechniqueDiagram.forTechnique(t);
        if (diagramPane != null) {
            diagramPane.getChildren().setAll(canvas);
        }
    }

    // ── Step 3 actions ────────────────────────────────────────────────────────

    @FXML
    private void handleAddBlankBefore() {
        int idx = pageListView.getSelectionModel().getSelectedIndex();
        insertBlank(idx < 0 ? 0 : idx);
    }

    @FXML
    private void handleAddBlankAfter() {
        int idx = pageListView.getSelectionModel().getSelectedIndex();
        insertBlank(idx < 0 ? pageListView.getItems().size() : idx + 1);
    }

    private void insertBlank(int position) {
        PageSequence seq = state.getPageSequence();
        if (seq == null) {
            return;
        }
        QuirePage blank = QuirePage.builder()
            .physicalPosition(position)
            .pageType(PageType.COMPLETION_BLANK)
            .build();
        seq.insertPage(position, blank);
        seq.reindex();
        rebuildPageList();
        pageListView.getSelectionModel().select(position);
        setStatus("Blank page inserted at position " + (position + 1) + ".");
    }

    @FXML
    private void handleRemovePage() {
        int idx = pageListView.getSelectionModel().getSelectedIndex();
        PageSequence seq = state.getPageSequence();
        if (idx < 0 || seq == null) {
            return;
        }
        PageItem item = pageListView.getItems().get(idx);
        if (item.isHeader()) {
            return;
        }
        seq.removePage(item.seqIndex());
        seq.reindex();
        rebuildPageList();
        setStatus("Page removed.");
    }

    @FXML
    private void handleMovePageUp() {
        int idx = pageListView.getSelectionModel().getSelectedIndex();
        PageSequence seq = state.getPageSequence();
        if (idx <= 0 || seq == null) {
            return;
        }
        PageItem item = pageListView.getItems().get(idx);
        if (item.isHeader()) {
            return;
        }
        int seqIdx = item.seqIndex();
        if (seqIdx > 0) {
            seq.movePage(seqIdx, seqIdx - 1);
            seq.reindex();
            rebuildPageList();
            // reselect the moved item
            for (int i = 0; i < pageListView.getItems().size(); i++) {
                PageItem pi = pageListView.getItems().get(i);
                if (!pi.isHeader() && pi.seqIndex() == seqIdx - 1) {
                    pageListView.getSelectionModel().select(i);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleMovePageDown() {
        int idx = pageListView.getSelectionModel().getSelectedIndex();
        PageSequence seq = state.getPageSequence();
        if (idx < 0 || seq == null) {
            return;
        }
        PageItem item = pageListView.getItems().get(idx);
        if (item.isHeader()) {
            return;
        }
        int seqIdx = item.seqIndex();
        if (seqIdx < seq.pageCount() - 1) {
            seq.movePage(seqIdx, seqIdx + 1);
            seq.reindex();
            rebuildPageList();
            for (int i = 0; i < pageListView.getItems().size(); i++) {
                PageItem pi = pageListView.getItems().get(i);
                if (!pi.isHeader() && pi.seqIndex() == seqIdx + 1) {
                    pageListView.getSelectionModel().select(i);
                    break;
                }
            }
        }
    }

    /**
     * Rebuilds the page list items, inserting signature-boundary header rows
     * based on the current pages-per-signature setting.
     */
    private void rebuildPageList() {
        PageSequence seq = state.getPageSequence();
        if (seq == null) {
            pageListView.setItems(FXCollections.emptyObservableList());
            return;
        }
        List<QuirePage> pages = seq.getPages();
        int pps = sigSizeSpinner.getValue() != null ? sigSizeSpinner.getValue() * 4 : 16;
        ImpositionGroup group = BindingGroupMapper.groupFor(
            techniqueCombo.getValue() != null
                ? techniqueCombo.getValue() : BindingTechnique.SADDLE_STITCH);

        ObservableList<PageItem> items = FXCollections.observableArrayList();
        int sigNum = 1;
        for (int i = 0; i < pages.size(); i++) {
            // Insert signature header at the start of each signature block (group C only)
            if (group == ImpositionGroup.C && i % pps == 0) {
                items.add(PageItem.header("── Signature " + sigNum + " ──"));
                sigNum++;
            } else if (group != ImpositionGroup.C && i == 0) {
                items.add(PageItem.header("── Signature 1 ──"));
            }
            QuirePage page = pages.get(i);
            String label = pageLabel(page, i);
            items.add(PageItem.page(i, label, page.getPageType()));
        }
        pageListView.setItems(items);
        refreshPreviewSummary();
    }

    private static String pageLabel(QuirePage page, int idx) {
        String type = switch (page.getPageType()) {
            case CONTENT -> "Content";
            case AESTHETIC -> "Aesthetic blank";
            case COMPLETION_BLANK -> "Blank";
            case FILLER_BLANK -> "Filler";
        };
        String logical = page.getLogicalPageNumber()
            .map(n -> "  (logical p." + n + ")")
            .orElse("");
        return "  " + (idx + 1) + "  " + type + logical;
    }

    private void refreshPreviewSummary() {
        PageSequence seq = state.getPageSequence();
        if (seq == null) {
            previewSummaryLabel.setText("");
            return;
        }
        long blanks = seq.getPages().stream()
            .filter(p -> p.getPageType() != PageType.CONTENT)
            .count();
        previewSummaryLabel.setText(seq.pageCount() + " pages total  ·  "
            + blanks + " blank  ·  "
            + (seq.pageCount() - blanks) + " content");
    }

    // ── Step 4 actions ────────────────────────────────────────────────────────

    private void runImpositionAndPopulateTable() {
        PageSequence seq = state.getPageSequence();
        if (seq == null) {
            return;
        }

        double thickness = parseThickness();
        CreepConfig creepConfig = thickness > 0
            ? CreepConfig.builder().paperThicknessMm(thickness).build()
            : CreepConfig.builder().build();

        int sigSize = sigSizeSpinner.getValue() != null ? sigSizeSpinner.getValue() : 4;

        QuireProject project = QuireProject.builder()
            .name(state.getInputPdf() != null
                ? state.getInputPdf().getFileName().toString() : "project")
            .bindingTechnique(state.getTechnique())
            .paperSize(state.getPaperSize())
            .readingDirection(state.getReadingDirection())
            .layout(ImpositionLayout.FOLIO)
            .pageSequence(seq)
            .paddingConfig(PaddingConfig.builder().signatureSize(sigSize).build())
            .numberingConfig(NumberingConfig.builder().build())
            .markConfig(MarkConfig.builder().build())
            .creepConfig(creepConfig)
            .build();

        List<Signature> sigs = ImpositionEngine.impose(project);
        state.setImpositionResult(sigs);

        signaturesTable.setItems(FXCollections.observableArrayList(
            sigs.stream().map(SignatureRow::from).toList()));

        setStatus("Imposition ready: " + sigs.size() + " signature(s).");
    }

    @FXML
    private void handleBrowseOutput() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Imposed PDF As");
        fc.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
        File f = fc.showSaveDialog(wizardStack.getScene().getWindow());
        if (f != null) {
            state.setOutputPdf(f.toPath());
            outputPathField.setText(f.toString());
        }
    }

    @FXML
    private void handleExport() {
        if (state.getOutputPdf() == null) {
            showError("No output path", "Please choose an output file first.");
            return;
        }
        if (!state.hasImpositionResult()) {
            showError("Not imposed", "Imposition has not been computed yet.");
            return;
        }
        collectMarksState();
        try {
            MarkConfig marks = MarkConfig.builder()
                .foldLines(state.isFoldLines())
                .signatureProofMarkers(state.isStitchMarks())
                .trimLines(state.isTrimLines())
                .sewingHoles(state.isSewingHoles())
                .build();

            PdfImpositionWriter.write(
                state.getImpositionResult(),
                state.getInputPdf(),
                state.getOutputPdf(),
                state.getPaperSize());

            exportResultLabel.setText("Exported: " + state.getOutputPdf().getFileName());
            exportButton.setDisable(true);
            setStatus("Export complete.");
        } catch (IOException e) {
            showError("Export failed", e.getMessage());
        }
    }

    @FXML
    private void handleSaveTemplate() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save as .quire Template");
        fc.getExtensionFilters().add(new ExtensionFilter("Quire batch files", "*.quire"));
        File f = fc.showSaveDialog(wizardStack.getScene().getWindow());
        if (f == null) {
            return;
        }
        collectOptionsState();
        collectMarksState();
        try {
            QuireTemplateWriter.write(state, f.toPath());
            setStatus("Template saved: " + f.getName());
        } catch (IOException e) {
            showError("Failed to save template", e.getMessage());
        }
    }

    // ── Wizard navigation ─────────────────────────────────────────────────────

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
                rebuildPageList();
            }
            if (next == 3) {
                collectOptionsState();
                exportButton.setDisable(false);
                exportResultLabel.setText("");
                runImpositionAndPopulateTable();
            }
            showStep(next);
        }
    }

    private boolean validateCurrentStep() {
        return switch (currentStep) {
            case 0 -> {
                if (state.getMode() == WizardMode.SINGLE_PDF && !state.hasInputPdf()) {
                    showError("No PDF selected", "Please select a source PDF.");
                    yield false;
                }
                if (state.getMode() == WizardMode.BATCH && !state.hasBatchConfig()) {
                    showError("No batch file", "Please select a .quire batch file.");
                    yield false;
                }
                yield true;
            }
            case 1 -> {
                collectOptionsState();
                yield true;
            }
            case 2 -> {
                if (state.getPageSequence() == null || state.getPageSequence().pageCount() == 0) {
                    showError("Empty sequence", "There are no pages to impose.");
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

    private void collectMarksState() {
        state.setFoldLines(foldLinesCheck.isSelected());
        state.setStitchMarks(stitchMarksCheck.isSelected());
        state.setSewingHoles(sewingHolesCheck.isSelected());
        state.setTrimLines(trimLinesCheck.isSelected());
    }

    private void showStep(int step) {
        currentStep = step;
        List<VBox> steps = List.of(stepLoad, stepOptions, stepPreview, stepExport);
        for (int i = 0; i < steps.size(); i++) {
            showNode(steps.get(i), i == step);
        }
        stepIndicatorLabel.setText("Step " + (step + 1) + " of " + TOTAL_STEPS);
        backButton.setDisable(step == 0);
        boolean isLast = step == TOTAL_STEPS - 1;
        nextButton.setText(isLast ? "Finish" : "Next →");
        nextButton.setDisable(isLast);
    }

    // ── Guides panel ──────────────────────────────────────────────────────────

    private void openGuidesPanel() {
        try {
            URL fxml = getClass().getResource(
                "/com/maiitsoh/quirebind/desktop/fxml/guides-panel.fxml");
            FXMLLoader loader = new FXMLLoader(fxml);
            Scene scene = new Scene(loader.load(), 760, 500);
            URL css = getClass().getResource(
                "/com/maiitsoh/quirebind/desktop/css/style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            Stage stage = new Stage();
            stage.setTitle("QuireBind — Binding Guides");
            stage.initModality(Modality.NONE);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Could not open guides", e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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

    private FileChooser pdfChooser(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
        return fc;
    }

    private void showError(String header, String body) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("QuireBind");
        a.setHeaderText(header);
        a.setContentText(body);
        a.showAndWait();
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    private static void showNode(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    // ── Inner types ───────────────────────────────────────────────────────────

    /**
     * Represents one row in the page list — either a content/blank page or a signature
     * boundary header.
     */
    public record PageItem(boolean isHeader, int seqIndex, String label, PageType pageType) {

        /** Creates a signature boundary header row. */
        static PageItem header(String label) {
            return new PageItem(true, -1, label, null);
        }

        /** Creates a page row. */
        static PageItem page(int seqIndex, String label, PageType pageType) {
            return new PageItem(false, seqIndex, label, pageType);
        }
    }

    /** Custom list cell that styles header rows differently from page rows. */
    private static final class PageListCell extends ListCell<PageItem> {

        @Override
        protected void updateItem(PageItem item, boolean empty) {
            super.updateItem(item, empty);
            getStyleClass().removeAll("sig-header-cell", "blank-page-cell", "content-page-cell");
            if (empty || item == null) {
                setText(null);
                setDisable(false);
            } else if (item.isHeader()) {
                setText(item.label());
                getStyleClass().add("sig-header-cell");
                setDisable(true);
            } else {
                setText(item.label());
                boolean isBlank = item.pageType() != PageType.CONTENT;
                getStyleClass().add(isBlank ? "blank-page-cell" : "content-page-cell");
                setDisable(false);
            }
        }
    }

    /**
     * Flat summary row for the imposition result table on the export step.
     */
    public record SignatureRow(int index, int pageCount, int sheetCount, String creepSummary) {

        static SignatureRow from(Signature sig) {
            double maxCreep = sig.getSheets().stream()
                .mapToDouble(s -> s.getCreepResult().map(r -> r.getCreepMm()).orElse(0.0))
                .max().orElse(0.0);
            String creep = maxCreep > 0
                ? String.format("%.3f mm", maxCreep) : "—";
            return new SignatureRow(
                sig.getSignatureIndex() + 1,
                sig.getLogicalPageNumbers().size(),
                sig.getSheets().size(),
                creep);
        }
    }
}
