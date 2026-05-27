/*
 * Copyright 2025 Quire Contributors
 *
 * This file is part of Quire.
 *
 * Quire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Quire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Quire.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.quire.core.model;

import java.util.Objects;

/**
 * A warning produced during document format conversion.
 *
 * <p><strong>Phase 2 placeholder.</strong> In Phase 1, this class exists only to keep the
 * model shape stable. The conversion service (Phase 2) will produce and consume it.
 */
public final class ConversionWarning {

    private final WarningSeverity severity;
    private final String message;

    private ConversionWarning(Builder builder) {
        this.severity = builder.severity;
        this.message = builder.message;
    }

    /** Returns the severity of this warning. */
    public WarningSeverity getSeverity() {
        return severity;
    }

    /** Returns the human-readable warning message. */
    public String getMessage() {
        return message;
    }

    /** Returns a new {@link Builder}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link ConversionWarning}. */
    public static final class Builder {

        private WarningSeverity severity;
        private String message;

        private Builder() {
        }

        /**
         * Sets the severity.
         *
         * @param severity must not be null
         * @return this builder
         */
        public Builder severity(WarningSeverity severity) {
            this.severity = Objects.requireNonNull(severity, "severity");
            return this;
        }

        /**
         * Sets the warning message.
         *
         * @param message must not be null or blank
         * @return this builder
         */
        public Builder message(String message) {
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("message must not be null or blank");
            }
            this.message = message;
            return this;
        }

        /**
         * Builds the {@link ConversionWarning}.
         *
         * @return a new immutable instance
         * @throws NullPointerException     if severity is not set
         * @throws IllegalArgumentException if message is not set
         */
        public ConversionWarning build() {
            Objects.requireNonNull(severity, "severity must be set");
            Objects.requireNonNull(message, "message must be set");
            return new ConversionWarning(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConversionWarning other)) {
            return false;
        }
        return severity == other.severity
                && message.equals(other.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(severity, message);
    }

    @Override
    public String toString() {
        return "ConversionWarning{"
                + "severity=" + severity
                + ", message='" + message + '\''
                + '}';
    }
}
