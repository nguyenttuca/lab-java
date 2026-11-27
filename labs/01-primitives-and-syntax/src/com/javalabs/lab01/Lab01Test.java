package com.javalabs.lab01;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

/**
 * Driver test tự động cho Lab 01 (dùng custom assertion helpers, không phụ thuộc -ea).
 */
public class Lab01Test {

    private static int passed = 0;
    private static int failed = 0;

    @FunctionalInterface
    private interface TestCase {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        System.out.println("=== Lab 01: Primitives, Casting & CLI Formatter Tests ===");

        runTest("sumWithoutOverflow: positive & negative boundary", () -> {
            checkEquals(2_147_483_657L, PrimitivesToolkit.sumWithoutOverflow(Integer.MAX_VALUE, 10),
                    "MAX_VALUE + 10 must not overflow int");
            checkEquals(-2_147_483_658L, PrimitivesToolkit.sumWithoutOverflow(Integer.MIN_VALUE, -10),
                    "MIN_VALUE - 10 must not overflow int");
        });

        runTest("addExactOrClamp: normal and overflow clamping", () -> {
            checkEquals(42, PrimitivesToolkit.addExactOrClamp(20, 22), "Normal addition");
            checkEquals(Integer.MAX_VALUE, PrimitivesToolkit.addExactOrClamp(Integer.MAX_VALUE, 1),
                    "Positive overflow should clamp to Integer.MAX_VALUE");
            checkEquals(Integer.MIN_VALUE, PrimitivesToolkit.addExactOrClamp(Integer.MIN_VALUE, -5),
                    "Negative overflow should clamp to Integer.MIN_VALUE");
        });

        runTest("toUnsigned32 & toUnsignedByte: bit pattern conversion", () -> {
            checkEquals(4_294_967_295L, PrimitivesToolkit.toUnsigned32(-1), "-1 in 32-bit unsigned");
            checkEquals(2_147_483_648L, PrimitivesToolkit.toUnsigned32(Integer.MIN_VALUE),
                    "Integer.MIN_VALUE unsigned value");
            checkEquals(200, PrimitivesToolkit.toUnsignedByte((byte) 200), "(byte) 200 unsigned value");
            checkEquals(255, PrimitivesToolkit.toUnsignedByte((byte) -1), "(byte) -1 unsigned value");
            checkEquals(0, PrimitivesToolkit.toUnsignedByte((byte) 0), "(byte) 0 unsigned value");
        });

        runTest("clampToShort: within and outside bounds", () -> {
            checkEquals((short) 1234, PrimitivesToolkit.clampToShort(1234), "In-range value");
            checkEquals(Short.MAX_VALUE, PrimitivesToolkit.clampToShort(40_000), "Clamp upper bound");
            checkEquals(Short.MIN_VALUE, PrimitivesToolkit.clampToShort(-50_000), "Clamp lower bound");
        });

        runTest("celsiusToFahrenheit: float precision & invalid temp", () -> {
            checkFloatEquals(32.0f, PrimitivesToolkit.celsiusToFahrenheit(0.0f), 0.0001f, "0C -> 32F");
            checkFloatEquals(212.0f, PrimitivesToolkit.celsiusToFahrenheit(100.0f), 0.0001f, "100C -> 212F");
            checkThrows(IllegalArgumentException.class,
                    () -> PrimitivesToolkit.celsiusToFahrenheit(-300.0f),
                    "Below absolute zero should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> PrimitivesToolkit.celsiusToFahrenheit(Float.NaN),
                    "NaN should throw");
        });

        runTest("calculateAverage: floating division & invalid count", () -> {
            checkDoubleEquals(2.5, PrimitivesToolkit.calculateAverage(5, 2), 1e-9, "5 / 2 must be 2.5");
            checkDoubleEquals(-3.5, PrimitivesToolkit.calculateAverage(-7, 2), 1e-9, "-7 / 2 must be -3.5");
            checkThrows(IllegalArgumentException.class,
                    () -> PrimitivesToolkit.calculateAverage(10, 0),
                    "Zero count should throw");
        });

        runTest("shiftUppercaseAscii: positive, negative, wrap-around", () -> {
            checkEquals('B', PrimitivesToolkit.shiftUppercaseAscii('Z', 2), "Z + 2 -> B");
            checkEquals('Z', PrimitivesToolkit.shiftUppercaseAscii('A', -1), "A - 1 -> Z");
            checkEquals('Z', PrimitivesToolkit.shiftUppercaseAscii('A', -53), "A - 53 -> Z");
            checkEquals('A', PrimitivesToolkit.shiftUppercaseAscii('A', 260), "Full cycles");
            checkThrows(IllegalArgumentException.class,
                    () -> PrimitivesToolkit.shiftUppercaseAscii('a', 1),
                    "Lowercase char should throw");
        });

        runTest("isWithinSafeRange: bounds & NaN validation", () -> {
            checkTrue(PrimitivesToolkit.isWithinSafeRange(10.0, 10.0, 20.0), "Inclusive lower bound");
            checkTrue(PrimitivesToolkit.isWithinSafeRange(20.0, 10.0, 20.0), "Inclusive upper bound");
            checkFalse(PrimitivesToolkit.isWithinSafeRange(20.1, 10.0, 20.0), "Out of range");
            checkThrows(IllegalArgumentException.class,
                    () -> PrimitivesToolkit.isWithinSafeRange(Double.NaN, 0.0, 10.0),
                    "NaN value should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> PrimitivesToolkit.isWithinSafeRange(5.0, 10.0, 1.0),
                    "min > max should throw");
        });

        runTest("classifyTemperatureBand: bands, Kelvin & absolute zero", () -> {
            checkEquals("FREEZING (263.15 K)", PrimitivesToolkit.classifyTemperatureBand(-10.0), "Freezing");
            checkEquals("NORMAL (298.15 K)", PrimitivesToolkit.classifyTemperatureBand(25.0), "Normal");
            checkEquals("HIGH (313.15 K)", PrimitivesToolkit.classifyTemperatureBand(40.0), "High boundary");
            checkEquals("CRITICAL (348.15 K)", PrimitivesToolkit.classifyTemperatureBand(75.0), "Critical boundary");
            checkEquals("FREEZING (0.00 K)", PrimitivesToolkit.classifyTemperatureBand(-273.15), "Absolute zero");
            checkThrows(IllegalArgumentException.class,
                    () -> PrimitivesToolkit.classifyTemperatureBand(-273.16),
                    "Below absolute zero should throw");
        });

        runTest("SensorCliFormatter.readAndFormatReading: spaces after nextInt & Locale.US", () -> {
            String input = "7\nServer Room A\n24.5\ntrue\n";
            Scanner scanner = new Scanner(input).useLocale(Locale.US);
            String actual = SensorCliFormatter.readAndFormatReading(scanner);
            checkEquals("[#0007] Server Room A      |  24.50 C | ONLINE", actual,
                    "Formatted sensor line mismatch");

            String offlineInput = "120\n  Lab Warehouse 2  \n-5.25\nfalse\n";
            String actualOffline = SensorCliFormatter.readAndFormatReading(
                    new Scanner(offlineInput).useLocale(Locale.US));
            checkEquals("[#0120] Lab Warehouse 2    |  -5.25 C | OFFLINE", actualOffline,
                    "Offline & trimmed location mismatch");

            checkThrows(IllegalArgumentException.class,
                    () -> SensorCliFormatter.readAndFormatReading(null),
                    "Null scanner should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> SensorCliFormatter.readAndFormatReading(new Scanner("0\nRoom\n20.0\ntrue\n").useLocale(Locale.US)),
                    "Non-positive sensorId should throw");
        });

        runTest("SensorCliFormatter.printSummaryTable: printf format", () -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
            SensorCliFormatter.printSummaryTable(ps, "Boiler-01", 12, 82.456, true);
            String output = baos.toString(StandardCharsets.UTF_8);
            String expected = "Sensor: Boiler-01       | Samples:   12 | Avg:   82.46 C | Alert: YES"
                    + System.lineSeparator();
            checkEquals(expected, output, "Summary printf mismatch");

            checkThrows(IllegalArgumentException.class,
                    () -> SensorCliFormatter.printSummaryTable(null, "A", 1, 10.0, false),
                    "Null PrintStream should throw");
        });

        System.out.printf("=== Kết quả Lab 01: %d PASS / %d FAIL ===%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
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

    private static void checkDoubleEquals(double expected, double actual, double delta, String message) {
        if (Double.isNaN(actual) || Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " | Expected: <" + expected + "> but got: <" + actual + ">");
        }
    }

    private static void checkFloatEquals(float expected, float actual, float delta, String message) {
        if (Float.isNaN(actual) || Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " | Expected: <" + expected + "> but got: <" + actual + ">");
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
