package com.javalabs.lab02;

import java.util.Objects;

/**
 * Thực hành Wrapper Classes (Integer, Long, Double, Boolean),
 * Autoboxing/Unboxing null-safe và Integer Cache (-128..127).
 */
public final class WrapperDiagnostics {

    private WrapperDiagnostics() {
        // Utility class
    }

    /**
     * Unbox Integer sang int an toàn; trả về defaultValue nếu value là null.
     */
    public static int safeUnboxOrDefault(Integer value, int defaultValue) {
        // TODO: Tránh NullPointerException khi value == null
        throw new UnsupportedOperationException("TODO: implement safeUnboxOrDefault");
    }

    /**
     * So sánh giá trị của 2 đối tượng Integer (hỗ trợ cả null và số ngoài dải cache -128..127).
     */
    public static boolean areWrapperValuesEqual(Integer a, Integer b) {
        // TODO: Không được dùng `a == b`. Gợi ý: Objects.equals(a, b)
        throw new UnsupportedOperationException("TODO: implement areWrapperValuesEqual");
    }

    /**
     * Thực nghiệm Integer Cache: tạo 2 biến Integer qua Integer.valueOf(value)
     * và trả về kết quả so sánh tham chiếu `first == second`.
     * Với [-128, 127] sẽ trả true; ngoài dải này trả false.
     */
    public static boolean isSameCachedReference(int value) {
        // TODO: Dùng Integer.valueOf(value) (KHÔNG dùng new Integer đã deprecated) và so sánh ==
        throw new UnsupportedOperationException("TODO: implement isSameCachedReference");
    }

    /**
     * Cộng toàn bộ phần tử khác null trong mảng numbers.
     *
     * @throws IllegalArgumentException nếu mảng numbers là null
     * @throws ArithmeticException      nếu tổng vượt quá giới hạn int (dùng Math.addExact)
     */
    public static int sumNonNullIntegers(Integer[] numbers) {
        // TODO: Bỏ qua phần tử null để tránh NPE khi auto-unboxing
        throw new UnsupportedOperationException("TODO: implement sumNonNullIntegers");
    }

    /**
     * Cộng 2 đối tượng Long an toàn: coi giá trị null là 0L.
     *
     * @throws ArithmeticException nếu tổng tràn giới hạn kiểu long
     */
    public static long safeAddLongs(Long a, Long b) {
        // TODO: Unbox an toàn (null -> 0L) và cộng bằng Math.addExact
        throw new UnsupportedOperationException("TODO: implement safeAddLongs");
    }

    /**
     * Nhân 2 đối tượng Double an toàn:
     * - Nếu một trong hai (hoặc cả hai) giá trị khác null là NaN hoặc Infinite -> ném IllegalArgumentException
     * - Nếu a == null hoặc b == null -> trả về 0.0
     * - Ngược lại trả về a * b
     */
    public static double safeMultiplyOrZero(Double a, Double b) {
        // TODO: Kiểm tra NaN/Infinite trước hoặc song song với null check
        throw new UnsupportedOperationException("TODO: implement safeMultiplyOrZero");
    }

    /**
     * Parse chuỗi sang Integer (trim khoảng trắng trước khi parse).
     * Trả về null nếu raw là null, blank, hoặc không đúng định dạng số nguyên.
     */
    public static Integer parseIntOrNull(String raw) {
        // TODO: Dùng Integer.valueOf(...) và xử lý NumberFormatException
        throw new UnsupportedOperationException("TODO: implement parseIntOrNull");
    }

    /**
     * Parse boolean nghiêm ngặt (không phân biệt hoa thường sau khi trim):
     * "true" -> Boolean.TRUE, "false" -> Boolean.FALSE, mọi trường hợp khác (hoặc null) -> null.
     */
    public static Boolean parseStrictBooleanOrNull(String raw) {
        // TODO: Không dùng Boolean.parseBoolean trực tiếp vì "yes" sẽ bị biến thành false
        throw new UnsupportedOperationException("TODO: implement parseStrictBooleanOrNull");
    }
}
