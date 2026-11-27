package com.javalabs.lab07;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Driver test tự động cho Lab 07 (dùng custom assertion helpers, không phụ thuộc -ea).
 */
public class Lab07Test {

    private static int passed = 0;
    private static int failed = 0;

    @FunctionalInterface
    private interface TestCase {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        System.out.println("=== Lab 07: Exception Handling & File I/O Tests ===");

        runTest("CsvParseException: Checked Exception with lineNumber & cause", () -> {
            checkTrue(Exception.class.isAssignableFrom(CsvParseException.class),
                    "Must extend Exception");
            checkFalse(RuntimeException.class.isAssignableFrom(CsvParseException.class),
                    "Must be a Checked Exception, NOT RuntimeException");

            NumberFormatException rootCause = new NumberFormatException("bad");
            CsvParseException ex = new CsvParseException("Invalid amount", 7, rootCause);
            checkEquals(7, ex.getLineNumber(), "Line number mismatch");
            checkTrue(ex.getCause() == rootCause, "Cause must be preserved");
        });

        runTest("parseSignedTransactionLine: valid rows, cause chaining & edge cases", () -> {
            checkBigDecimalEquals("150.50",
                    SafeFileProcessor.parseSignedTransactionLine("TX-1, credit , 150.50 , usd", 2),
                    "CREDIT positive");
            checkBigDecimalEquals("-40.25",
                    SafeFileProcessor.parseSignedTransactionLine("TX-2,DEBIT,40.25,USD", 3),
                    "DEBIT negative");

            try {
                SafeFileProcessor.parseSignedTransactionLine("TX-3,CREDIT,abc,USD", 5);
                throw new AssertionError("Expected CsvParseException for invalid number");
            } catch (CsvParseException e) {
                checkEquals(5, e.getLineNumber(), "Exception lineNumber");
                checkTrue(e.getCause() instanceof NumberFormatException,
                        "Must wrap NumberFormatException as cause");
            }

            checkThrows(CsvParseException.class,
                    () -> SafeFileProcessor.parseSignedTransactionLine("TX-4,CREDIT,10,", 6),
                    "Trailing empty column must be caught (split with -1)");
            checkThrows(CsvParseException.class,
                    () -> SafeFileProcessor.parseSignedTransactionLine("TX-5,CREDIT,0,USD", 7),
                    "Zero amount should throw CsvParseException");
            checkThrows(CsvParseException.class,
                    () -> SafeFileProcessor.parseSignedTransactionLine("TX-6,REFUND,10,USD", 8),
                    "Unknown type should throw CsvParseException");
            checkThrows(IllegalArgumentException.class,
                    () -> SafeFileProcessor.parseSignedTransactionLine("   ", 2),
                    "Blank line should throw IllegalArgumentException");
        });

        runTest("processCsvLedger: sample CSV file (data/transactions_sample.csv)", () -> {
            Path samplePath = Path.of("data/transactions_sample.csv");
            checkTrue(Files.exists(samplePath),
                    "Run Lab07Test from labs/07-exceptions-and-file-io/ so data/transactions_sample.csv resolves");

            SafeFileProcessor.ProcessingReport report = SafeFileProcessor.processCsvLedger(samplePath);
            checkEquals(3, report.getValidCount(), "3 valid transactions in sample CSV");
            checkEquals(4, report.getErrorCount(), "4 malformed rows in sample CSV (blank line ignored)");
            checkBigDecimalEquals("200.00", report.getNetTotal(), "150.50 - 40.25 + 89.75 = 200.00");
            checkEquals(4, report.getErrorMessages().size(), "Error messages count");
        });

        runTest("processCsvLedger: missing file throws IOException & empty file gives zero report", () -> {
            Path tempDir = Files.createTempDirectory("lab07-ledger-test");
            try {
                Path missing = tempDir.resolve("missing.csv");
                checkThrows(IOException.class,
                        () -> SafeFileProcessor.processCsvLedger(missing),
                        "Missing file must throw Checked IOException, not return empty report");

                Path emptyFile = tempDir.resolve("empty.csv");
                Files.writeString(emptyFile, "", StandardCharsets.UTF_8);
                SafeFileProcessor.ProcessingReport emptyReport = SafeFileProcessor.processCsvLedger(emptyFile);
                checkEquals(0, emptyReport.getValidCount(), "Empty file validCount");
                checkEquals(0, emptyReport.getErrorCount(), "Empty file errorCount");
                checkBigDecimalEquals("0.00", emptyReport.getNetTotal(), "Empty file netTotal");
            } finally {
                deleteRecursively(tempDir);
            }
        });

        runTest("exportReport: creates parent directories & writes expected format", () -> {
            Path tempDir = Files.createTempDirectory("lab07-export-test");
            try {
                Path nestedOut = tempDir.resolve("nested/reports/summary.txt");
                SafeFileProcessor.ProcessingReport withErrors = new SafeFileProcessor.ProcessingReport(
                        2, 1, new BigDecimal("55.50"), List.of("Line 3: bad amount"));
                SafeFileProcessor.exportReport(nestedOut, withErrors);
                String content = Files.readString(nestedOut, StandardCharsets.UTF_8);
                String expected = String.join("\n",
                        "VALID_ROWS=2",
                        "ERROR_ROWS=1",
                        "NET_TOTAL=55.50",
                        "ERRORS:",
                        "- Line 3: bad amount");
                checkEquals(expected, content.replace("\r\n", "\n").stripTrailing(),
                        "Exported report with errors mismatch");

                Path cleanOut = tempDir.resolve("clean.txt");
                SafeFileProcessor.ProcessingReport noErrors = new SafeFileProcessor.ProcessingReport(
                        1, 0, new BigDecimal("10.00"), List.of());
                SafeFileProcessor.exportReport(cleanOut, noErrors);
                String cleanContent = Files.readString(cleanOut, StandardCharsets.UTF_8);
                String expectedClean = String.join("\n",
                        "VALID_ROWS=1",
                        "ERROR_ROWS=0",
                        "NET_TOTAL=10.00",
                        "ERRORS: NONE");
                checkEquals(expectedClean, cleanContent.replace("\r\n", "\n").stripTrailing(),
                        "Exported report without errors mismatch");
            } finally {
                deleteRecursively(tempDir);
            }
        });

        runTest("useAutoCloseableSafely: closes resource even when task throws", () -> {
            final boolean[] closedFlag = {false};
            AutoCloseable mockResource = () -> closedFlag[0] = true;

            checkThrows(IllegalStateException.class,
                    () -> SafeFileProcessor.useAutoCloseableSafely(mockResource, () -> {
                        throw new IllegalStateException("Simulated failure");
                    }),
                    "Task exception must propagate");
            checkTrue(closedFlag[0], "try-with-resources MUST close resource even on exception");
        });

        System.out.printf("=== Kết quả Lab 07: %d PASS / %d FAIL ===%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void deleteRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(p);
            }
        }
    }

    private static void runTest(String name, TestCase testCase) {
        try {
            testCase.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static void checkEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " | Expected: <" + expected + "> but got: <" + actual + ">");
        }
    }

    private static void checkBigDecimalEquals(String expectedLiteral, BigDecimal actual, String message) {
        if (actual == null) {
            throw new AssertionError(message + " | Expected: <" + expectedLiteral + "> but got: <null>");
        }
        BigDecimal expected = new BigDecimal(expectedLiteral);
        if (expected.compareTo(actual) != 0) {
            throw new AssertionError(message + " | Expected: <" + expected.toPlainString()
                    + "> but got: <" + actual.toPlainString() + ">");
        }
    }

    private static void checkTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message + " | Expected true but got false");
        }
    }

    private static void checkFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message + " | Expected false but got true");
        }
    }

    private static void checkThrows(Class<? extends Throwable> expectedType, TestCase action, String message) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }
            throw new AssertionError(message + " | Expected " + expectedType.getSimpleName()
                    + " but got " + thrown.getClass().getSimpleName() + ": " + thrown.getMessage());
        }
        throw new AssertionError(message + " | Expected " + expectedType.getSimpleName() + " but nothing was thrown");
    }
}
