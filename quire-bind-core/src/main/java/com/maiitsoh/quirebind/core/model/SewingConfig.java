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
 * Configuration for sewing hole placement.
 *
 * <p><strong>Phase 2 placeholder.</strong> This class exists in Phase 1 only to keep the
 * model shape stable. It contains no fields or business logic. Phase 2 will add configurable
 * hole count, spacing, and tape width.
 *
 * <p>In Phase 1, {@link MarkConfig#getSewingConfig()} always returns
 * {@link java.util.Optional#empty()}.
 */
public final class SewingConfig {

    private SewingConfig() {
    }
}
