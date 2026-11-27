package com.javalabs.lab08;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * Driver test tự động cho Lab 08 (dùng custom assertion helpers, không phụ thuộc -ea).
 */
public class Lab08Test {

    private static int passed = 0;
    private static int failed = 0;

    @FunctionalInterface
    private interface TestCase {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        System.out.println("=== Lab 08: Class, Enum, Record & Optional Tests ===");

        runTest("OrderStatus: enum fields, transitions & Optional fromCode", () -> {
            checkTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.PAID), "PENDING -> PAID");
            checkTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED), "PENDING -> CANCELLED");
            checkFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.DELIVERED), "Cannot skip PAID/SHIPPED");
            checkTrue(OrderStatus.PAID.canTransitionTo(OrderStatus.SHIPPED), "PAID -> SHIPPED");
            checkTrue(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.DELIVERED), "SHIPPED -> DELIVERED");
            checkFalse(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CANCELLED), "Terminal cannot transition");
            checkFalse(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.DELIVERED), "Terminal cannot self-transition");
            checkFalse(OrderStatus.PENDING.canTransitionTo(null), "Null target -> false");

            checkEquals(Optional.of(OrderStatus.SHIPPED), OrderStatus.fromCode(" shp "), "Lookup by code");
            checkEquals(Optional.empty(), OrderStatus.fromCode("UNKNOWN"), "Unknown code -> empty");
            checkEquals(Optional.empty(), OrderStatus.fromCode(null), "Null code -> empty");
            checkEquals(Optional.empty(), OrderStatus.fromCode("   "), "Blank code -> empty");
        });

        runTest("Product: constructor chaining, validation, stock mutations & toString", () -> {
            Product p = new Product(" sku-01 ", " Mechanical Keyboard ", new BigDecimal("49.9"), 10);
            checkEquals("SKU-01", p.getSku(), "Normalized SKU");
            checkEquals("Mechanical Keyboard", p.getName(), "Trimmed name");
            checkBigDecimalEquals("49.90", p.getUnitPrice(), "Scale normalized to 2");
            checkEquals(10, p.getStock(), "Initial stock");

            Product chained = new Product("sku-02", "Mouse", new BigDecimal("15"));
            checkEquals(0, chained.getStock(), "3-arg constructor defaults stock to 0");

            p.restock(5);
            checkEquals(15, p.getStock(), "Stock after restock");
            p.deductStock(4);
            checkEquals(11, p.getStock(), "Stock after deduct");

            checkThrows(IllegalArgumentException.class, () -> p.deductStock(0), "Deduct <= 0 throws IllegalArgumentException");
            checkThrows(IllegalStateException.class, () -> p.deductStock(100), "Insufficient stock throws IllegalStateException");
            checkThrows(IllegalArgumentException.class, () -> p.setUnitPrice(new BigDecimal("-1")), "Negative price throws");
            checkThrows(IllegalArgumentException.class, () -> p.setName("  "), "Blank name throws");
            checkThrows(IllegalArgumentException.class, () -> new Product(" ", "X", BigDecimal.TEN), "Blank SKU throws");

            String text = p.toString();
            checkTrue(text.contains("SKU-01") && text.contains("49.90") && text.contains("11"),
                    "toString must contain sku, unitPrice and stock");
        });

        runTest("Product: equals & hashCode contract in HashSet & HashMap", () -> {
            Product p1 = new Product("sku-100", "Desk Lamp", new BigDecimal("25.00"), 3);
            Product p2 = new Product(" SKU-100 ", "Desk Lamp V2", new BigDecimal("30.00"), 9);
            Product p3 = new Product("SKU-200", "Chair", new BigDecimal("120.00"), 1);

            checkTrue(p1.equals(p2), "Same normalized SKU must be equal");
            checkEquals(p1.hashCode(), p2.hashCode(), "Equal Products MUST share identical hashCode");
            checkFalse(p1.equals(p3), "Different SKUs not equal");
            checkFalse(p1.equals(null), "Equals null -> false");

            Set<Product> set = new HashSet<>();
            set.add(p1);
            checkTrue(set.contains(p2), "HashSet lookup by equal Product must succeed");
            set.add(p2);
            checkEquals(1, set.size(), "HashSet must deduplicate equal SKUs");

            Map<Product, String> map = new HashMap<>();
            map.put(p1, "Warehouse-A");
            checkEquals("Warehouse-A", map.get(p2), "HashMap lookup by equal key must succeed");
        });

        runTest("ProductDto: immutable record, validation & factory", () -> {
            Product entity = new Product("kb-01", "Keycap Set", new BigDecimal("12.5"), 4);
            ProductDto dto = ProductDto.from(entity);
            checkEquals("KB-01", dto.sku(), "Record accessor sku()");
            checkEquals("Keycap Set", dto.name(), "Record accessor name()");
            checkBigDecimalEquals("12.50", dto.unitPrice(), "Record price scale");
            checkTrue(dto.isInStock(), "Stock 4 -> in stock");

            ProductDto zeroStock = new ProductDto(" kb-01 ", " Keycap Set ", new BigDecimal("12.50"), 0);
            checkFalse(zeroStock.isInStock(), "Stock 0 -> not in stock");

            checkThrows(IllegalArgumentException.class,
                    () -> new ProductDto("A", "B", new BigDecimal("-0.01"), 1),
                    "Negative price rejected by compact constructor");
            checkThrows(IllegalArgumentException.class,
                    () -> new ProductDto("A", "B", BigDecimal.ONE, -1),
                    "Negative stock rejected by compact constructor");
            checkThrows(IllegalArgumentException.class,
                    () -> ProductDto.from(null),
                    "Null product rejected");
        });

        runTest("CatalogLookup: Optional pipeline & safe fallback", () -> {
            Product inStock = new Product("SKU-A", "Monitor", new BigDecimal("199.99"), 5);
            Product outOfStock = new Product("SKU-B", "Webcam", new BigDecimal("49.50"), 0);
            List<Product> catalog = Arrays.asList(null, inStock, outOfStock);

            checkEquals(Optional.of(inStock), CatalogLookup.findBySku(catalog, " sku-a "),
                    "Find by normalized SKU ignoring null elements");
            checkEquals(Optional.empty(), CatalogLookup.findBySku(catalog, "  "),
                    "Blank SKU -> empty");
            checkEquals(Optional.empty(), CatalogLookup.findBySku(catalog, "MISSING"),
                    "Missing SKU -> empty");

            Optional<ProductDto> dtoOpt = CatalogLookup.findInStockDtoBySku(catalog, "sku-a");
            checkTrue(dtoOpt.isPresent(), "In-stock product returns DTO");
            checkEquals("SKU-A", dtoOpt.orElseThrow().sku(), "Mapped DTO SKU");

            checkEquals(Optional.empty(), CatalogLookup.findInStockDtoBySku(catalog, "SKU-B"),
                    "Out-of-stock product must be filtered out");

            checkBigDecimalEquals("49.50", CatalogLookup.getUnitPriceOrZero(catalog, "SKU-B"),
                    "Existing SKU price");
            checkBigDecimalEquals("0.00", CatalogLookup.getUnitPriceOrZero(catalog, "UNKNOWN"),
                    "Missing SKU falls back to 0.00");

            checkEquals(outOfStock, CatalogLookup.requireBySku(catalog, "sku-b"),
                    "requireBySku returns matching Product");
            checkThrows(NoSuchElementException.class,
                    () -> CatalogLookup.requireBySku(catalog, "NOPE"),
                    "requireBySku throws NoSuchElementException when missing");
            checkThrows(IllegalArgumentException.class,
                    () -> CatalogLookup.findBySku(null, "SKU-A"),
                    "Null catalog throws IllegalArgumentException");
        });

        System.out.printf("=== Kết quả Lab 08: %d PASS / %d FAIL ===%n", passed, failed);
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
