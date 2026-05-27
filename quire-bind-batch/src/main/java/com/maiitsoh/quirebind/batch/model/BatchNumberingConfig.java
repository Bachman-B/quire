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
package com.maiitsoh.quirebind.batch.model;

/**
 * Numbering section within a {@link BatchJob} or {@link BatchDefaults}.
 *
 * <p>Style values are lowercase strings matching {@code FolioStyle} names:
 * {@code none}, {@code arabic}, {@code roman}.
 */
public record BatchNumberingConfig(
        String frontMatterStyle,
        String bodyStyle,
        int bodyStart,
        boolean suppressFirstBodyFolio) {

    /** Default: arabic body numbering starting at 1, first folio suppressed. */
    public static final BatchNumberingConfig DEFAULT =
            new BatchNumberingConfig(null, "arabic", 1, true);
}
