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
package com.quirebind.batch.model;

/**
 * A single imposition job from a {@code .quire} batch file.
 *
 * <p>Fields that are null or blank fall back to values in {@link BatchDefaults}
 * at run time. The only required fields are {@code name}, {@code source},
 * {@code output}, and {@code technique}.
 */
public record BatchJob(
        String name,
        String source,
        String output,
        String technique,
        String layout,
        int signatureSize,
        String readingDirection,
        String paperSize,
        BatchPaddingConfig padding,
        BatchNumberingConfig numbering,
        BatchCreepConfig creep,
        BatchMarksConfig marks) {
}
