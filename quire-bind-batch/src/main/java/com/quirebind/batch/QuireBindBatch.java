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
package com.quirebind.batch;

import com.quirebind.batch.model.BatchConfig;
import com.quirebind.batch.parser.QuireFileParser;
import com.quirebind.batch.runner.BatchRunner;
import com.quirebind.batch.runner.BatchRunner.JobResult;

import java.nio.file.Path;
import java.util.List;

/**
 * CLI entry point for QuireBind batch processing.
 *
 * <p>Usage:
 * <pre>
 *   java -jar quire-bind-batch.jar --config &lt;path-to-.quire-file&gt; [--dry-run]
 * </pre>
 *
 * <p>Exit codes:
 * <ul>
 *   <li>0 — all jobs succeeded (or dry-run completed)</li>
 *   <li>1 — argument error or one or more jobs failed</li>
 * </ul>
 */
public final class QuireBindBatch {

    private QuireBindBatch() {
    }

    /**
     * Main entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.exit(run(args));
    }

    /**
     * Executes the batch run and returns the process exit code.
     * Extracted for testability.
     *
     * @param args command-line arguments
     * @return 0 on success, 1 on failure
     */
    static int run(String[] args) {
        String configPath = null;
        boolean dryRun = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--config" -> {
                    if (i + 1 >= args.length) {
                        err("--config requires a path argument");
                        return 1;
                    }
                    configPath = args[++i];
                }
                case "--dry-run" -> dryRun = true;
                default -> {
                    err("Unknown argument: " + args[i]);
                    printUsage();
                    return 1;
                }
            }
        }

        if (configPath == null) {
            err("--config is required");
            printUsage();
            return 1;
        }

        Path config = Path.of(configPath);
        Path baseDir = config.toAbsolutePath().getParent();

        BatchConfig batchConfig;
        try {
            batchConfig = QuireFileParser.parse(config);
        } catch (Exception e) {
            err("Failed to parse config: " + e.getMessage());
            return 1;
        }

        if (batchConfig.jobs().isEmpty()) {
            System.out.println("No jobs defined in " + configPath);
            return 0;
        }

        List<JobResult> results = BatchRunner.run(batchConfig, baseDir, dryRun);
        boolean anyFailed = false;
        for (JobResult r : results) {
            String prefix = r.success() ? "[OK]  " : "[FAIL]";
            System.out.println(prefix + " " + r.name() + ": " + r.message());
            if (!r.success()) {
                anyFailed = true;
            }
        }
        return anyFailed ? 1 : 0;
    }

    private static void printUsage() {
        System.out.println(
                "Usage: quire-bind-batch --config <file.quire> [--dry-run]");
    }

    private static void err(String msg) {
        System.err.println("Error: " + msg);
    }
}
