package com.javalabs.lab03;

/**
 * Driver test tự động cho Lab 03 (dùng custom assertion helpers, không phụ thuộc -ea).
 */
public class Lab03Test {

    private static int passed = 0;
    private static int failed = 0;

    @FunctionalInterface
    private interface TestCase {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        System.out.println("=== Lab 03: String Immutability, Pool & Text Processing Tests ===");

        runTest("StringPoolLab: null-safe equality & ignore case", () -> {
            checkTrue(StringPoolLab.areContentEqual(new String("java"), new String("java")),
                    "Two distinct String objects with same text must be equal");
            checkTrue(StringPoolLab.areContentEqual(null, null), "Both null -> true");
            checkFalse(StringPoolLab.areContentEqual("java", null), "One null -> false");

            checkTrue(StringPoolLab.areEqualIgnoreCase("Java21", "jAvA21"), "Ignore case match");
            checkTrue(StringPoolLab.areEqualIgnoreCase(null, null), "Ignore case both null");
            checkFalse(StringPoolLab.areEqualIgnoreCase(null, "abc"), "Ignore case one null");
        });

        runTest("StringPoolLab: Pool vs Heap references & immutability", () -> {
            checkTrue(StringPoolLab.compareLiteralPoolVsHeapNew(),
                    "Literals share pool reference while new String allocates separate object");
            checkFalse(StringPoolLab.isHeapCopySameReferenceAsPool("hello"),
                    "new String(\"hello\") must not == pool reference");
            checkTrue(StringPoolLab.isInternedSameReferenceAsPool("hello"),
                    "intern() must return canonical pool reference");
            checkEquals("Java", StringPoolLab.applyTransformIgnoringReturnLeavesOriginal("Java"),
                    "Original String remains immutable");
            checkThrows(IllegalArgumentException.class,
                    () -> StringPoolLab.isHeapCopySameReferenceAsPool(null),
                    "Null input should throw");
        });

        runTest("TextProcessor.normalizeFullName: Unicode strip & Title Case", () -> {
            checkEquals("Nguyễn Văn An",
                    TextProcessor.normalizeFullName("\u2003  nGuYễN   vĂn   aN  "),
                    "Must strip Unicode whitespace and normalize to Title Case");
            checkEquals("Ada Lovelace",
                    TextProcessor.normalizeFullName("ADA   LOVELACE"),
                    "Uppercase input");
            checkThrows(IllegalArgumentException.class,
                    () -> TextProcessor.normalizeFullName("\u2003   \t "),
                    "Unicode blank string should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> TextProcessor.normalizeFullName(null),
                    "Null should throw");
        });

        runTest("TextProcessor.repeatWithDelimiter: StringBuilder & performance", () -> {
            checkEquals("OK-OK-OK", TextProcessor.repeatWithDelimiter("OK", 3, "-"), "Repeat 3 times");
            checkEquals("SOLO", TextProcessor.repeatWithDelimiter("SOLO", 1, ","), "Repeat 1 time");
            checkEquals("", TextProcessor.repeatWithDelimiter("OK", 0, "-"), "Repeat 0 times");

            long startNanos = System.nanoTime();
            String large = TextProcessor.repeatWithDelimiter("ab", 25_000, ",");
            long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
            checkEquals(25_000 * 2 + 24_999, large.length(), "Large StringBuilder output length");
            checkTrue(elapsedMs < 1_000L, "Must use StringBuilder (took " + elapsedMs + " ms)");

            checkThrows(IllegalArgumentException.class,
                    () -> TextProcessor.repeatWithDelimiter("OK", -1, "-"),
                    "Negative count should throw");
        });

        runTest("TextProcessor.sanitizeAndJoinCsvTags: split, strip & join", () -> {
            checkEquals("java | spring-boot | oop",
                    TextProcessor.sanitizeAndJoinCsvTags(" java, ,  SPRING-boot ,   ,oop  "),
                    "Normalize CSV tags");
            checkEquals("", TextProcessor.sanitizeAndJoinCsvTags(" , ,   "),
                    "Only commas/blanks -> empty string");
            checkThrows(IllegalArgumentException.class,
                    () -> TextProcessor.sanitizeAndJoinCsvTags(null),
                    "Null CSV should throw");
        });

        runTest("TextProcessor.isValidUsername: length & ASCII rules", () -> {
            checkTrue(TextProcessor.isValidUsername("dev_01"), "Valid username");
            checkTrue(TextProcessor.isValidUsername("A123"), "Min length 4");
            checkFalse(TextProcessor.isValidUsername("abc"), "Too short (3)");
            checkFalse(TextProcessor.isValidUsername("1dev_user"),
                    "Must start with ASCII letter");
            checkFalse(TextProcessor.isValidUsername("_dev01"), "Cannot start with underscore");
            checkFalse(TextProcessor.isValidUsername("dev-01"), "Hyphen not allowed");
            checkFalse(TextProcessor.isValidUsername("user_1234567890123456"), "Length 21 too long");
            checkFalse(TextProcessor.isValidUsername(null), "Null -> false");
        });

        runTest("TextProcessor.renderJsonPayload: Text Block formatting", () -> {
            String expected = "{\n"
                    + "  \"username\": \"alice\",\n"
                    + "  \"role\": \"ADMIN\",\n"
                    + "  \"level\": 5\n"
                    + "}";
            checkEquals(expected, TextProcessor.renderJsonPayload("  alice ", "ADMIN", 5),
                    "Text block JSON format mismatch");
            checkThrows(IllegalArgumentException.class,
                    () -> TextProcessor.renderJsonPayload(" ", "ADMIN", 1),
                    "Blank username should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> TextProcessor.renderJsonPayload("alice", "ADMIN", 0),
                    "Level < 1 should throw");
        });

        runTest("TextProcessor.maskEmail: local lengths & validation", () -> {
            checkEquals("a***e@dev.io", TextProcessor.maskEmail("  alice@dev.io "), "Local length >= 3");
            checkEquals("a*@dev.io", TextProcessor.maskEmail("ab@dev.io"), "Local length == 2");
            checkEquals("*@dev.io", TextProcessor.maskEmail("a@dev.io"), "Local length == 1");

            checkThrows(IllegalArgumentException.class, () -> TextProcessor.maskEmail(null), "Null email");
            checkThrows(IllegalArgumentException.class, () -> TextProcessor.maskEmail("no-at.com"), "Missing @");
            checkThrows(IllegalArgumentException.class, () -> TextProcessor.maskEmail("a@@b.com"), "Double @");
            checkThrows(IllegalArgumentException.class, () -> TextProcessor.maskEmail("@b.com"), "Empty local");
            checkThrows(IllegalArgumentException.class, () -> TextProcessor.maskEmail("a@localhost"), "No dot in domain");
            checkThrows(IllegalArgumentException.class, () -> TextProcessor.maskEmail("a@.b.com"), "Leading dot");
            checkThrows(IllegalArgumentException.class, () -> TextProcessor.maskEmail("a@b..com"), "Double dot");
            checkThrows(IllegalArgumentException.class, () -> TextProcessor.maskEmail("a b@c.com"), "Space inside");
        });

        System.out.printf("=== Kết quả Lab 03: %d PASS / %d FAIL ===%n", passed, failed);
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
