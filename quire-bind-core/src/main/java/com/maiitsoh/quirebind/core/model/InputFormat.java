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
package com.maiitsoh.quirebind.core.model;

/**
 * Input document formats accepted by the Quire conversion pipeline.
 *
 * <p>{@link #PDF} is the only format supported in Phase 1. All other formats are
 * converted to PDF by the {@code quire-convert} module before entering the standard
 * Quire pipeline. Requesting any non-PDF format in Phase 1 is unsupported.
 */
public enum InputFormat {

    /** PDF — Phase 1. No conversion required. */
    PDF,

    /** HTML single file or zip with assets — Phase 2, converted via Playwright. */
    HTML,

    /** Microsoft Word DOCX — Phase 2, converted via Apache POI and LibreOffice. */
    DOCX,

    /** OpenDocument Text — Phase 2, converted via LibreOffice. */
    ODT,

    /** XML with XSL-FO stylesheet — Phase 3, converted via Apache FOP. */
    XML_FO,

    /** Markdown file or folder — Phase 3, converted via Flexmark-java. */
    MARKDOWN,

    /** LaTeX source — Phase 3, requires an external LaTeX installation. */
    LATEX
}
