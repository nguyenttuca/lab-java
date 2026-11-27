package com.javalabs.lab04;

import java.util.Arrays;

/**
 * Driver test tự động cho Lab 04 (dùng custom assertion helpers, không phụ thuộc -ea).
 */
public class Lab04Test {

    private static int passed = 0;
    private static int failed = 0;

    @FunctionalInterface
    private interface TestCase {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        System.out.println("=== Lab 04: Control Flow, Methods & Pass-by-Value Tests ===");

        runTest("PassByValue: primitive swap does NOT change caller variables", () -> {
            int x = 10;
            int y = 20;
            PassByValueExperiment.attemptSwapPrimitives(x, y);
            checkEquals(10, x, "Caller x must stay unchanged");
            checkEquals(20, y, "Caller y must stay unchanged");
        });

        runTest("PassByValue: reassigning reference vs mutating Heap object", () -> {
            Box box = new Box(10, "origin");
            PassByValueExperiment.attemptReassignBox(box, 999);
            checkEquals(10, box.getValue(), "Reassigning parameter does not change caller reference");
            checkEquals("origin", box.getLabel(), "Label must stay 'origin'");

            PassByValueExperiment.mutateBoxValue(box, 99);
            checkEquals(99, box.getValue(), "Mutating object state is visible to caller");

            checkThrows(IllegalArgumentException.class,
                    () -> PassByValueExperiment.mutateBoxValue(null, 1),
                    "Null Box should throw");
        });

        runTest("PassByValue: reassigning array vs mutating elements in-place", () -> {
            int[] data = {1, 2, 3};
            PassByValueExperiment.attemptReassignArray(data);
            checkEquals("[1, 2, 3]", Arrays.toString(data), "Array reference in caller stays unchanged");

            PassByValueExperiment.doubleArrayElementsInPlace(data);
            checkEquals("[2, 4, 6]", Arrays.toString(data), "In-place mutation doubles each element");

            checkThrows(IllegalArgumentException.class,
                    () -> PassByValueExperiment.doubleArrayElementsInPlace(null),
                    "Null array should throw");
        });

        runTest("PassByValue: createOrMutateDependingOnFlag", () -> {
            Box origin = new Box(5, "base");
            Box detached = PassByValueExperiment.createOrMutateDependingOnFlag(origin, false, 50);
            checkEquals(5, origin.getValue(), "Original box must remain untouched when flag=false");
            checkEquals(50, detached.getValue(), "Detached box gets new value");
            checkEquals("detached", detached.getLabel(), "Detached label");
            checkTrue(origin != detached, "Must return a different Box instance");

            Box mutated = PassByValueExperiment.createOrMutateDependingOnFlag(origin, true, 77);
            checkTrue(origin == mutated, "Must return the same Box instance when flag=true");
            checkEquals(77, origin.getValue(), "Original box is updated when flag=true");
        });

        runTest("ControlFlowEngine.calculateShippingFee: switch expression", () -> {
            checkEquals(50_000, ControlFlowEngine.calculateShippingFee("mon", 5), "Weekday <= 10kg");
            checkEquals(100_000, ControlFlowEngine.calculateShippingFee("  wed ", 10), "Weekday boundary 10kg");
            checkEquals(96_000, ControlFlowEngine.calculateShippingFee("FRI", 12), "Weekday > 10kg gets 20% off");
            checkEquals(45_000, ControlFlowEngine.calculateShippingFee("sat", 3), "Weekend rate");
            checkEquals(225_000, ControlFlowEngine.calculateShippingFee("SUN", 15), "Weekend no discount");

            checkThrows(IllegalArgumentException.class,
                    () -> ControlFlowEngine.calculateShippingFee("INVALID", 2),
                    "Unknown dayCode should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> ControlFlowEngine.calculateShippingFee("MON", 0),
                    "Weight <= 0 should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> ControlFlowEngine.calculateShippingFee(null, 5),
                    "Null dayCode should throw");
        });

        runTest("ControlFlowEngine.findFirstMatchCoordinates: labeled break & jagged/null rows", () -> {
            int[][] matrix = {
                null,
                {1, 2, 3},
                {4, 5},
                {5, 6, 7}
            };
            checkEquals("[2, 1]", Arrays.toString(ControlFlowEngine.findFirstMatchCoordinates(matrix, 5)),
                    "Must return FIRST match (2,1), not (3,0)");
            checkEquals("[-1, -1]", Arrays.toString(ControlFlowEngine.findFirstMatchCoordinates(matrix, 99)),
                    "Not found -> [-1, -1]");
            checkThrows(IllegalArgumentException.class,
                    () -> ControlFlowEngine.findFirstMatchCoordinates(null, 1),
                    "Null matrix should throw");
        });

        runTest("ControlFlowEngine.sumPositiveRowsSkippingNegatives: labeled continue", () -> {
            int[][] matrix = {
                {1, 2},
                {10, -1, 20},
                null,
                {},
                {4, 6}
            };
            checkEquals(13, ControlFlowEngine.sumPositiveRowsSkippingNegatives(matrix),
                    "Row with -1 must be completely skipped (even elements before -1)");
            checkThrows(ArithmeticException.class,
                    () -> ControlFlowEngine.sumPositiveRowsSkippingNegatives(new int[][]{{Integer.MAX_VALUE, 1}}),
                    "Overflow should throw ArithmeticException");
        });

        runTest("ControlFlowEngine: method overloading & varargs", () -> {
            checkEquals("cpu=80", ControlFlowEngine.formatMetric(" cpu ", 80),
                    "Must resolve to exact (String, int) overload");
            checkEquals("temp=36.50", ControlFlowEngine.formatMetric("temp", 36.5),
                    "Must resolve to (String, double) overload");
            checkEquals("scores=[10,20,30]", ControlFlowEngine.formatMetric("scores", 10, 20, 30),
                    "Varargs with multiple values");
            checkEquals("empty=[]", ControlFlowEngine.formatMetric("empty"),
                    "Varargs with zero values");

            checkEquals(15, ControlFlowEngine.sumAll(1, 2, 3, 4, 5), "sumAll 1..5");
            checkEquals(0, ControlFlowEngine.sumAll(), "sumAll empty");

            checkThrows(IllegalArgumentException.class,
                    () -> ControlFlowEngine.formatMetric("x", (int[]) null),
                    "Null varargs array should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> ControlFlowEngine.formatMetric("val", Double.NaN),
                    "NaN double metric should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> ControlFlowEngine.sumAll((int[]) null),
                    "Null varargs in sumAll should throw");
        });

        System.out.printf("=== Kết quả Lab 04: %d PASS / %d FAIL ===%n", passed, failed);
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

    private static void checkTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message + " | Expected true but got false");
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
