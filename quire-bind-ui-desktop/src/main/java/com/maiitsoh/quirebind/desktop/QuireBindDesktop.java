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
package com.maiitsoh.quirebind.desktop;

/** CLI entry point — delegates to {@link QuireBindApp} to avoid JavaFX module restrictions. */
public final class QuireBindDesktop {

    private QuireBindDesktop() { }

    /**
     * Launches the QuireBind desktop application.
     *
     * @param args command-line arguments forwarded to JavaFX
     */
    public static void main(String[] args) {
        QuireBindApp.launch(QuireBindApp.class, args);
    }
}
