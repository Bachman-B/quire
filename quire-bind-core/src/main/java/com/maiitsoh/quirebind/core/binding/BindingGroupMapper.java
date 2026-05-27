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
package com.maiitsoh.quirebind.core.binding;

import com.maiitsoh.quirebind.core.model.BindingTechnique;
import com.maiitsoh.quirebind.core.model.ImpositionGroup;

import java.util.Objects;

/**
 * Maps a {@link BindingTechnique} to its {@link ImpositionGroup}.
 *
 * <p>Delegates to {@link BindingTechnique#group()} so that service and UI layers
 * depend on a named collaborator rather than calling a domain-enum method directly.
 */
public final class BindingGroupMapper {

    private BindingGroupMapper() {
    }

    /**
     * Returns the imposition group for the given binding technique.
     *
     * @param technique must not be null
     * @return the corresponding imposition group
     * @throws IllegalArgumentException if technique is null
     */
    public static ImpositionGroup groupFor(BindingTechnique technique) {
        Objects.requireNonNull(technique, "technique must not be null");
        return technique.group();
    }
}
