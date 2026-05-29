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
package com.maiitsoh.quirebind.web.controller;

import com.maiitsoh.quirebind.core.model.BindingTechnique;
import com.maiitsoh.quirebind.core.model.FolioPosition;
import com.maiitsoh.quirebind.core.model.FolioStyle;
import com.maiitsoh.quirebind.core.model.PaperSize;
import com.maiitsoh.quirebind.core.model.ReadingDirection;
import com.maiitsoh.quirebind.guides.loader.GuideLoader;
import com.maiitsoh.quirebind.guides.model.BindingGuide;
import com.maiitsoh.quirebind.guides.renderer.MarkdownToHtml;
import com.maiitsoh.quirebind.web.model.WebSession;
import com.maiitsoh.quirebind.web.service.WebImpositionService;
import com.maiitsoh.quirebind.web.service.WebImpositionService.SignatureSummary;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/** Handles all wizard steps and the guides panel for the web UI. */
@Controller
public class WizardController {

    private static final String STEP_UPLOAD = "fragments/upload :: step";
    private static final String STEP_BINDING = "fragments/binding :: step";
    private static final String STEP_PAGES = "fragments/pages :: step";
    private static final String STEP_EXPORT = "fragments/export :: step";

    private final WebImpositionService impositionService;

    /** Constructs the controller with the required imposition service. */
    public WizardController(WebImpositionService impositionService) {
        this.impositionService = impositionService;
    }

    /** Renders the main application shell with step 1 (upload) loaded. */
    @GetMapping("/")
    public String index(Model model, WebSession session) {
        addUploadModel(model, session);
        return "index";
    }

    /** Accepts a PDF upload and advances to the binding configuration step. */
    @PostMapping("/wizard/upload")
    public String upload(
            @RequestParam("pdf") MultipartFile file,
            @RequestHeader(value = "HX-Request", required = false) String htmx,
            WebSession session,
            Model model) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a PDF file.");
            addUploadModel(model, session);
            return htmx != null ? STEP_UPLOAD : "index";
        }
        Path tmp = Files.createTempFile("quire-upload-", ".pdf");
        file.transferTo(tmp);
        String name = file.getOriginalFilename();
        session.setSourcePdf(tmp);
        session.setOriginalFilename(name != null ? name : "document.pdf");
        session.setImpositionResult(null);
        addBindingModel(model, session);
        return htmx != null ? STEP_BINDING : "index";
    }

    /** Saves binding configuration and advances to the pages step. */
    @PostMapping("/wizard/binding")
    public String binding(
            @RequestParam("technique") String techniqueStr,
            @RequestParam("paperSize") String paperSizeStr,
            @RequestParam("readingDirection") String dirStr,
            @RequestParam(value = "signatureSize", defaultValue = "4") int signatureSize,
            @RequestHeader(value = "HX-Request", required = false) String htmx,
            WebSession session,
            Model model) {
        session.setTechnique(BindingTechnique.valueOf(techniqueStr));
        session.setPaperSize(PaperSize.valueOf(paperSizeStr));
        session.setReadingDirection(ReadingDirection.valueOf(dirStr));
        session.setSignatureSize(Math.max(1, signatureSize));
        session.setImpositionResult(null);
        addPagesModel(model, session);
        return htmx != null ? STEP_PAGES : "index";
    }

    /** Saves page-zone configuration and advances to the export step with imposition results. */
    @PostMapping("/wizard/pages")
    public String pages(
            @RequestParam(value = "frontMatterPages", defaultValue = "0") int frontPages,
            @RequestParam(value = "rearMatterPages", defaultValue = "0") int rearPages,
            @RequestHeader(value = "HX-Request", required = false) String htmx,
            WebSession session,
            Model model) throws IOException {
        session.setFrontMatterPageCount(Math.max(0, frontPages));
        session.setRearMatterPageCount(Math.max(0, rearPages));
        impositionService.impose(session);
        List<SignatureSummary> summary = impositionService.summarise(session);
        addExportModel(model, session, summary, null);
        return htmx != null ? STEP_EXPORT : "index";
    }

    /** Goes back from the binding step to the upload step. */
    @PostMapping("/wizard/back/upload")
    public String backToUpload(
            @RequestHeader(value = "HX-Request", required = false) String htmx,
            WebSession session,
            Model model) {
        addUploadModel(model, session);
        return htmx != null ? STEP_UPLOAD : "index";
    }

    /** Goes back from the pages step to the binding step. */
    @PostMapping("/wizard/back/binding")
    public String backToBinding(
            @RequestHeader(value = "HX-Request", required = false) String htmx,
            WebSession session,
            Model model) {
        addBindingModel(model, session);
        return htmx != null ? STEP_BINDING : "index";
    }

    /** Goes back from the export step to the pages step. */
    @PostMapping("/wizard/back/pages")
    public String backToPages(
            @RequestHeader(value = "HX-Request", required = false) String htmx,
            WebSession session,
            Model model) {
        addPagesModel(model, session);
        return htmx != null ? STEP_PAGES : "index";
    }

    /** Streams the imposed PDF to the browser as a file download. */
    @PostMapping("/wizard/download")
    public ResponseEntity<StreamingResponseBody> download(
            @RequestParam(value = "foldLines", defaultValue = "false") boolean foldLines,
            @RequestParam(value = "stitchMarks", defaultValue = "false") boolean stitchMarks,
            @RequestParam(value = "sewingHoles", defaultValue = "false") boolean sewingHoles,
            @RequestParam(value = "trimLines", defaultValue = "false") boolean trimLines,
            @RequestParam(value = "bodyFolioStyle", defaultValue = "ARABIC") String bodyStyleStr,
            @RequestParam(value = "frontFolioStyle", defaultValue = "NONE") String frontStyleStr,
            @RequestParam(value = "rearFolioStyle", defaultValue = "NONE") String rearStyleStr,
            @RequestParam(value = "folioPosition", defaultValue = "BOTTOM_OUTER") String posStr,
            @RequestParam(value = "suppressFirstFolio", defaultValue = "false")
                boolean suppressFirst,
            WebSession session) {
        session.setFoldLines(foldLines);
        session.setStitchMarks(stitchMarks);
        session.setSewingHoles(sewingHoles);
        session.setTrimLines(trimLines);
        session.setBodyFolioStyle(FolioStyle.valueOf(bodyStyleStr));
        session.setFrontMatterFolioStyle(FolioStyle.valueOf(frontStyleStr));
        session.setRearMatterFolioStyle(FolioStyle.valueOf(rearStyleStr));
        session.setFolioPosition(FolioPosition.valueOf(posStr));
        session.setSuppressFirstFolio(suppressFirst);

        String filename = deriveOutputFilename(session.getOriginalFilename());
        StreamingResponseBody body = out -> impositionService.export(session, out);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(filename).build().toString())
            .body(body);
    }

    /** Returns the guides panel page showing the requested technique's guide. */
    @GetMapping("/guides")
    public String guides(
            @RequestParam(value = "technique", defaultValue = "saddle_stitch") String techniqueId,
            Model model) throws IOException {
        List<BindingGuide> allGuides = GuideLoader.loadAll();
        BindingGuide selected = allGuides.stream()
            .filter(g -> g.getMetadata().getTechniqueId().equals(techniqueId))
            .findFirst()
            .orElse(allGuides.isEmpty() ? null : allGuides.get(0));
        model.addAttribute("guides", allGuides);
        model.addAttribute("selected", selected);
        if (selected != null) {
            String dirId = selected.getMetadata().getTechniqueId().replace('_', '-');
            String html = MarkdownToHtml.toHtml(selected.getBody(),
                src -> resolveGuideImage(dirId, src));
            model.addAttribute("guideHtml", html);
        }
        return "guides";
    }

    private static String resolveGuideImage(String dirId, String src) {
        String resourcePath = "/guides/" + dirId + "/" + src;
        try (var in = WizardController.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                return null;
            }
            byte[] bytes = in.readAllBytes();
            String mime = src.endsWith(".svg") ? "image/svg+xml" : "image/png";
            return "data:" + mime + ";base64,"
                + java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    private static String deriveOutputFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "imposed.pdf";
        }
        String base = originalFilename.endsWith(".pdf")
            ? originalFilename.substring(0, originalFilename.length() - 4)
            : originalFilename;
        return base + "-imposed.pdf";
    }

    private void addUploadModel(Model model, WebSession session) {
        model.addAttribute("step", 1);
        model.addAttribute("filename", session.getOriginalFilename());
    }

    private void addBindingModel(Model model, WebSession session) {
        model.addAttribute("step", 2);
        model.addAttribute("techniques", Arrays.asList(BindingTechnique.values()));
        model.addAttribute("paperSizes", Arrays.asList(PaperSize.values()));
        model.addAttribute("session", session);
    }

    private void addPagesModel(Model model, WebSession session) {
        model.addAttribute("step", 3);
        model.addAttribute("session", session);
    }

    private void addExportModel(Model model, WebSession session,
            List<SignatureSummary> summary, String error) {
        model.addAttribute("step", 4);
        model.addAttribute("session", session);
        model.addAttribute("summary", summary);
        model.addAttribute("folioStyles", Arrays.asList(FolioStyle.values()));
        model.addAttribute("folioPositions", Arrays.asList(FolioPosition.values()));
        if (error != null) {
            model.addAttribute("error", error);
        }
    }
}
