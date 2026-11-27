package com.javalabs.lab02;

import java.math.BigDecimal;

/**
 * Driver test tự động cho Lab 02 (dùng custom assertion helpers, không phụ thuộc -ea).
 */
public class Lab02Test {

    private static int passed = 0;
    private static int failed = 0;

    @FunctionalInterface
    private interface TestCase {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        System.out.println("=== Lab 02: Wrapper Classes, Autoboxing & BigDecimal Tests ===");

        runTest("safeUnboxOrDefault: non-null & null safety", () -> {
            checkEquals(42, WrapperDiagnostics.safeUnboxOrDefault(Integer.valueOf(42), -1), "Unbox non-null");
            checkEquals(-1, WrapperDiagnostics.safeUnboxOrDefault(null, -1), "Fallback on null");
        });

        runTest("areWrapperValuesEqual: null & outside Integer cache", () -> {
            checkTrue(WrapperDiagnostics.areWrapperValuesEqual(null, null), "Both null");
            checkFalse(WrapperDiagnostics.areWrapperValuesEqual(null, 10), "One null");
            checkTrue(WrapperDiagnostics.areWrapperValuesEqual(Integer.valueOf(127), Integer.valueOf(127)),
                    "Inside cache");
            checkTrue(WrapperDiagnostics.areWrapperValuesEqual(Integer.valueOf(1000), Integer.valueOf(1000)),
                    "Outside cache (-128..127) must still be equal by value");
            checkFalse(WrapperDiagnostics.areWrapperValuesEqual(Integer.valueOf(1000), Integer.valueOf(2000)),
                    "Different values");
        });

        runTest("isSameCachedReference: Integer cache boundary (-128..127)", () -> {
            checkTrue(WrapperDiagnostics.isSameCachedReference(-128), "-128 is cached");
            checkTrue(WrapperDiagnostics.isSameCachedReference(127), "127 is cached");
            checkFalse(WrapperDiagnostics.isSameCachedReference(-129), "-129 is NOT cached");
            checkFalse(WrapperDiagnostics.isSameCachedReference(128), "128 is NOT cached");
        });

        runTest("sumNonNullIntegers: skip nulls, empty & overflow", () -> {
            checkEquals(15, WrapperDiagnostics.sumNonNullIntegers(new Integer[]{5, null, 10, null}),
                    "Sum skipping nulls");
            checkEquals(0, WrapperDiagnostics.sumNonNullIntegers(new Integer[]{}), "Empty array");
            checkEquals(0, WrapperDiagnostics.sumNonNullIntegers(new Integer[]{null, null}), "All nulls");
            checkThrows(IllegalArgumentException.class,
                    () -> WrapperDiagnostics.sumNonNullIntegers(null),
                    "Null array should throw");
            checkThrows(ArithmeticException.class,
                    () -> WrapperDiagnostics.sumNonNullIntegers(new Integer[]{Integer.MAX_VALUE, 1}),
                    "Overflow should throw ArithmeticException");
        });

        runTest("safeAddLongs & safeMultiplyOrZero: null & NaN handling", () -> {
            checkEquals(100L, WrapperDiagnostics.safeAddLongs(null, 100L), "Null treated as 0L");
            checkEquals(0L, WrapperDiagnostics.safeAddLongs(null, null), "Both null -> 0L");
            checkEquals(10_000_000_000L,
                    WrapperDiagnostics.safeAddLongs(5_000_000_000L, 5_000_000_000L),
                    "Large longs sum");

            checkDoubleEquals(0.0, WrapperDiagnostics.safeMultiplyOrZero(null, 5.5), 1e-9, "Null factor -> 0.0");
            checkDoubleEquals(6.25, WrapperDiagnostics.safeMultiplyOrZero(2.5, 2.5), 1e-9, "Normal multiply");
            checkThrows(IllegalArgumentException.class,
                    () -> WrapperDiagnostics.safeMultiplyOrZero(null, Double.NaN),
                    "NaN factor must throw even if other is null");
            checkThrows(IllegalArgumentException.class,
                    () -> WrapperDiagnostics.safeMultiplyOrZero(Double.POSITIVE_INFINITY, 2.0),
                    "Infinity must throw");
        });

        runTest("parseIntOrNull & parseStrictBooleanOrNull", () -> {
            checkEquals(Integer.valueOf(42), WrapperDiagnostics.parseIntOrNull("  42 "), "Trimmed int");
            checkEquals(null, WrapperDiagnostics.parseIntOrNull("12abc"), "Invalid int -> null");
            checkEquals(null, WrapperDiagnostics.parseIntOrNull("   "), "Blank -> null");
            checkEquals(null, WrapperDiagnostics.parseIntOrNull(null), "Null -> null");

            checkEquals(Boolean.TRUE, WrapperDiagnostics.parseStrictBooleanOrNull(" TRUE "), "Strict true");
            checkEquals(Boolean.FALSE, WrapperDiagnostics.parseStrictBooleanOrNull("False"), "Strict false");
            checkEquals(null, WrapperDiagnostics.parseStrictBooleanOrNull("yes"), "Reject 'yes'");
            checkEquals(null, WrapperDiagnostics.parseStrictBooleanOrNull("1"), "Reject '1'");
            checkEquals(null, WrapperDiagnostics.parseStrictBooleanOrNull(null), "Null -> null");
        });

        runTest("sumDecimalStrings: 0.1 + 0.2 accuracy & validation", () -> {
            BigDecimal sum = CurrencyCalculator.sumDecimalStrings("0.1", "0.2");
            checkBigDecimalEquals("0.30", sum, "0.1 + 0.2 must equal 0.30");
            checkEquals(2, sum.scale(), "Scale must be 2");

            checkBigDecimalEquals("0.00", CurrencyCalculator.sumDecimalStrings(), "Empty varargs -> 0.00");
            checkBigDecimalEquals("10.01", CurrencyCalculator.sumDecimalStrings("10.005", "0.004"),
                    "Sum then HALF_UP round to 2 decimals");

            checkThrows(IllegalArgumentException.class,
                    () -> CurrencyCalculator.sumDecimalStrings((String[]) null),
                    "Null array should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> CurrencyCalculator.sumDecimalStrings("1.00", "bad"),
                    "Invalid number string should throw");
        });

        runTest("calculateLineTotalWithTax: VAT & HALF_UP rounding", () -> {
            checkBigDecimalEquals("65.97",
                    CurrencyCalculator.calculateLineTotalWithTax("19.99", 3, "0.10"),
                    "19.99 * 3 * 1.10 = 65.967 -> 65.97");
            checkBigDecimalEquals("1.01",
                    CurrencyCalculator.calculateLineTotalWithTax("1.005", 1, "0.00"),
                    "HALF_UP rounding at .005");
            checkThrows(IllegalArgumentException.class,
                    () -> CurrencyCalculator.calculateLineTotalWithTax("10.00", -1, "0.10"),
                    "Negative quantity should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> CurrencyCalculator.calculateLineTotalWithTax("-1.00", 1, "0.10"),
                    "Negative price should throw");
        });

        runTest("applyPercentageDiscount: valid & non-terminating division", () -> {
            checkBigDecimalEquals("89.99",
                    CurrencyCalculator.applyPercentageDiscount(new BigDecimal("99.99"), new BigDecimal("10")),
                    "10% off 99.99 -> 89.991 -> 89.99");
            checkBigDecimalEquals("66.67",
                    CurrencyCalculator.applyPercentageDiscount(new BigDecimal("100.00"), new BigDecimal("33.333")),
                    "100 * 66.667 / 100 -> 66.67");
            checkThrows(IllegalArgumentException.class,
                    () -> CurrencyCalculator.applyPercentageDiscount(new BigDecimal("50"), new BigDecimal("101")),
                    "Discount > 100% should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> CurrencyCalculator.applyPercentageDiscount(new BigDecimal("50"), new BigDecimal("-1")),
                    "Negative discount should throw");
        });

        runTest("splitBillEvenly: exact cent distribution without loss", () -> {
            BigDecimal[] split3 = CurrencyCalculator.splitBillEvenly(new BigDecimal("100.00"), 3);
            checkEquals(3, split3.length, "Array length must be 3");
            checkBigDecimalEquals("33.34", split3[0], "First person gets extra cent");
            checkBigDecimalEquals("33.33", split3[1], "Second person");
            checkBigDecimalEquals("33.33", split3[2], "Third person");

            BigDecimal[] split6 = CurrencyCalculator.splitBillEvenly(new BigDecimal("10.00"), 6);
            BigDecimal total6 = BigDecimal.ZERO;
            for (BigDecimal part : split6) {
                total6 = total6.add(part);
            }
            checkBigDecimalEquals("10.00", total6, "Sum of 6 splits must equal 10.00");
            checkBigDecimalEquals("1.67", split6[0], "Index 0 gets 1.67");
            checkBigDecimalEquals("1.67", split6[3], "Index 3 gets 1.67");
            checkBigDecimalEquals("1.66", split6[4], "Index 4 gets 1.66");

            BigDecimal[] splitTiny = CurrencyCalculator.splitBillEvenly(new BigDecimal("0.02"), 5);
            checkBigDecimalEquals("0.01", splitTiny[0], "Tiny split[0]");
            checkBigDecimalEquals("0.01", splitTiny[1], "Tiny split[1]");
            checkBigDecimalEquals("0.00", splitTiny[2], "Tiny split[2]");

            checkThrows(IllegalArgumentException.class,
                    () -> CurrencyCalculator.splitBillEvenly(new BigDecimal("10.00"), 0),
                    "Zero people should throw");
        });

        runTest("hasSameMonetaryValue: scale-independent compareTo", () -> {
            checkTrue(CurrencyCalculator.hasSameMonetaryValue(new BigDecimal("10.5"), new BigDecimal("10.50")),
                    "10.5 and 10.50 must have same monetary value");
            checkTrue(CurrencyCalculator.hasSameMonetaryValue(null, null), "Both null");
            checkFalse(CurrencyCalculator.hasSameMonetaryValue(BigDecimal.ZERO, null), "One null");
            checkFalse(CurrencyCalculator.hasSameMonetaryValue(new BigDecimal("10.50"), new BigDecimal("10.51")),
                    "Different values");
        });

        System.out.printf("=== Kết quả Lab 02: %d PASS / %d FAIL ===%n", passed, failed);
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

    private static void checkBigDecimalEquals(String expectedLiteral, BigDecimal actual, String message) {
        if (actual == null) {
            throw new AssertionError(message + " | Expected: <" + expectedLiteral + "> but got: <null>");
        }
        BigDecimal expected = new BigDecimal(expectedLiteral);
        if (expected.compareTo(actual) != 0 || expected.scale() != actual.scale()) {
            throw new AssertionError(message + " | Expected: <" + expected.toPlainString()
                    + " (scale=" + expected.scale() + ")> but got: <"
                    + actual.toPlainString() + " (scale=" + actual.scale() + ")>");
        }
    }

    private static void checkDoubleEquals(double expected, double actual, double delta, String message) {
        if (Double.isNaN(actual) || Math.abs(expected - actual) > delta) {
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
