package com.javalabs.lab05;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Driver test tự động cho Lab 05 (dùng custom assertion helpers, không phụ thuộc -ea).
 */
public class Lab05Test {

    private static int passed = 0;
    private static int failed = 0;

    @FunctionalInterface
    private interface TestCase {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        System.out.println("=== Lab 05: Arrays & Collections Tests ===");

        runTest("ArrayToolkit: sortedCopy, format2D, deepEquals & resizeAndPad", () -> {
            int[] origin = {30, 10, 20};
            int[] sorted = ArrayToolkit.sortedCopyWithoutMutatingOriginal(origin);
            checkEquals("[10, 20, 30]", Arrays.toString(sorted), "Sorted copy mismatch");
            checkEquals("[30, 10, 20]", Arrays.toString(origin), "Original array must NOT be mutated");

            int[][] m1 = {{1, 2}, {3, 4}};
            int[][] m2 = {{1, 2}, {3, 4}};
            checkEquals("[[1, 2], [3, 4]]", ArrayToolkit.formatArray2D(m1), "2D format mismatch");
            checkTrue(ArrayToolkit.areMatricesDeeplyEqual(m1, m2), "Deep equality must be true");
            checkTrue(ArrayToolkit.areMatricesDeeplyEqual(null, null), "Both null deep equals");

            checkEquals("[1, 2, 9, 9]", Arrays.toString(ArrayToolkit.resizeAndPad(new int[]{1, 2}, 4, 9)),
                    "Expand and pad");
            checkEquals("[1]", Arrays.toString(ArrayToolkit.resizeAndPad(new int[]{1, 2, 3}, 1, 9)),
                    "Shrink array");
            checkThrows(IllegalArgumentException.class,
                    () -> ArrayToolkit.resizeAndPad(new int[]{1}, -1, 0),
                    "Negative length should throw");
        });

        runTest("ArrayToolkit.transposeRectangularMatrix: valid & jagged rejection", () -> {
            int[][] rect = {{1, 2, 3}, {4, 5, 6}};
            int[][] expected = {{1, 4}, {2, 5}, {3, 6}};
            checkTrue(Arrays.deepEquals(expected, ArrayToolkit.transposeRectangularMatrix(rect)),
                    "Transpose 2x3 -> 3x2");

            checkThrows(IllegalArgumentException.class,
                    () -> ArrayToolkit.transposeRectangularMatrix(new int[][]{{1, 2}, {3}}),
                    "Jagged array should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> ArrayToolkit.transposeRectangularMatrix(new int[][]{null, {1}}),
                    "Null row should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> ArrayToolkit.transposeRectangularMatrix(new int[][]{}),
                    "Empty matrix should throw");
        });

        runTest("CollectionAnalytics: ArrayList vs Arrays.asList vs List.copyOf", () -> {
            List<String> immutableInput = List.of("  java ", "", " oop");
            List<String> mutable = CollectionAnalytics.createMutableUpperCaseCopy(immutableInput);
            checkEquals(List.of("JAVA", "OOP"), mutable, "Mutable copy content");
            mutable.add("SOLID");
            checkEquals(3, mutable.size(), "Returned list must support add()");

            String[] raw = {"alpha", "beta"};
            List<String> fixed = CollectionAnalytics.createFixedSizeBackedListDemo(raw);
            checkEquals(List.of("ALPHA", "beta"), fixed, "First item uppercased via set(0, ...)");
            checkEquals("alpha", raw[0], "Input array should remain unchanged");
            fixed.set(1, "BETA");
            checkEquals("BETA", fixed.get(1), "Arrays.asList must allow set()");
            checkThrows(UnsupportedOperationException.class,
                    () -> fixed.add("gamma"),
                    "Arrays.asList must reject add()");

            List<String> snapshot = CollectionAnalytics.createUnmodifiableSnapshot(mutable);
            checkEquals(List.of("JAVA", "OOP", "SOLID"), snapshot, "Snapshot matches");
            checkThrows(UnsupportedOperationException.class,
                    () -> snapshot.set(0, "C++"),
                    "List.copyOf must reject set()");
            checkThrows(UnsupportedOperationException.class,
                    () -> snapshot.add("GO"),
                    "List.copyOf must reject add()");

            List<String> withNull = new ArrayList<>();
            withNull.add("ok");
            withNull.add(null);
            checkThrows(IllegalArgumentException.class,
                    () -> CollectionAnalytics.createUnmodifiableSnapshot(withNull),
                    "List containing null should throw IllegalArgumentException");
        });

        runTest("CollectionAnalytics.removeAllShortStringsInPlace: safe removal", () -> {
            List<String> words = new ArrayList<>(Arrays.asList("a", null, "java", "  hi ", "code", "x"));
            int removed = CollectionAnalytics.removeAllShortStringsInPlace(words, 4);
            checkEquals(4, removed, "Removed count (a, null, hi, x)");
            checkEquals(List.of("java", "code"), words, "Remaining words");
        });

        runTest("CollectionAnalytics.removeIntegerValueVsIndex: remove(int) vs remove(Object)", () -> {
            List<Integer> nums = new ArrayList<>(List.of(10, 20, 30, 1));
            CollectionAnalytics.removeIntegerValueVsIndex(nums, 1, 1);
            checkEquals(List.of(10, 30), nums,
                    "Step 1 removes index 1 (20), Step 2 removes VALUE 1 (not index 1!)");

            List<Integer> noTarget = new ArrayList<>(List.of(5, 6, 7));
            CollectionAnalytics.removeIntegerValueVsIndex(noTarget, 99, 0);
            checkEquals(List.of(6, 7), noTarget, "Value not found leaves rest intact");

            checkThrows(IllegalArgumentException.class,
                    () -> CollectionAnalytics.removeIntegerValueVsIndex(nums, 10, 5),
                    "Out-of-bounds index should throw");
        });

        runTest("CollectionAnalytics.findUniqueCommonTags: order-independent Set equality", () -> {
            Set<String> common = CollectionAnalytics.findUniqueCommonTags(
                    Arrays.asList(" Java ", null, "OOP", "collections", "java"),
                    Arrays.asList("collections", "  JAVA", "spring", "  ")
            );
            checkEquals(Set.of("java", "collections"), common, "Set equality independent of iteration order");
        });

        runTest("CollectionAnalytics.countWordFrequencies: order-independent Map equality", () -> {
            Map<String, Integer> freq = CollectionAnalytics.countWordFrequencies(
                    Arrays.asList("Java  oop", null, "   ", "java COLLECTIONS java")
            );
            checkEquals(Map.of("java", 3, "oop", 1, "collections", 1), freq,
                    "Map equality independent of HashMap order");
        });

        runTest("CollectionAnalytics.rankWordsByFrequencyDescThenAlpha: Comparator & tie-breaker", () -> {
            Map<String, Integer> map = new HashMap<>();
            map.put("java", 3);
            map.put("code", 3);
            map.put("oop", 1);
            map.put("ignoredZero", 0);
            map.put("ignoredNeg", -2);
            map.put(" ", 10);
            map.put(null, 5);

            checkEquals(List.of("code", "java", "oop"),
                    CollectionAnalytics.rankWordsByFrequencyDescThenAlpha(map, 5),
                    "Descending freq then alphabetical tie-breaker");
            checkEquals(List.of("code", "java"),
                    CollectionAnalytics.rankWordsByFrequencyDescThenAlpha(map, 2),
                    "Limit top 2");
            checkEquals(List.of(),
                    CollectionAnalytics.rankWordsByFrequencyDescThenAlpha(map, 0),
                    "Limit 0 -> empty");
        });

        System.out.printf("=== Kết quả Lab 05: %d PASS / %d FAIL ===%n", passed, failed);
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
