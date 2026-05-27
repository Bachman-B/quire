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
 * Creep section within a {@link BatchJob}.
 *
 * <p>At most one of {@code paperWeightGsm} or {@code paperThicknessMm} should be set.
 * If both are null, creep compensation is disabled.
 */
public record BatchCreepConfig(Double paperWeightGsm, Double paperThicknessMm) {

    /** No creep configuration. */
    public static final BatchCreepConfig NONE = new BatchCreepConfig(null, null);
}
