package com.javalabs.lab03;

import java.util.Locale;
import java.util.Objects;

/**
 * Thực nghiệm String Constant Pool, tính bất biến (Immutability) và so sánh chuỗi an toàn.
 */
public final class StringPoolLab {

    private StringPoolLab() {
        // Utility class
    }

    /**
     * So sánh nội dung hai chuỗi an toàn với null.
     */
    public static boolean areContentEqual(String a, String b) {
        // TODO: Không dùng `a == b`; gợi ý dùng Objects.equals(a, b)
        throw new UnsupportedOperationException("TODO: implement areContentEqual");
    }

    /**
     * So sánh nội dung hai chuỗi không phân biệt hoa/thường và an toàn với null.
     * (null, null) -> true; một trong hai null -> false.
     */
    public static boolean areEqualIgnoreCase(String a, String b) {
        // TODO: Kiểm tra null trước khi gọi equalsIgnoreCase
        throw new UnsupportedOperationException("TODO: implement areEqualIgnoreCase");
    }

    /**
     * Thực nghiệm cơ chế String Constant Pool vs Heap:
     * Tạo s1 = "java-21", s2 = "java-21" và s3 = new String("java-21").
     * Trả về true nếu (s1 == s2) && (s1 != s3) && s1.equals(s3).
     */
    public static boolean compareLiteralPoolVsHeapNew() {
        // TODO: Khai báo s1, s2 (literal) và s3 (new String) để kiểm chứng tham chiếu vs nội dung
        throw new UnsupportedOperationException("TODO: implement compareLiteralPoolVsHeapNew");
    }

    /**
     * Kiểm chứng chuỗi tạo mới bằng `new String(text)` có cùng tham chiếu với bản trong Pool không.
     *
     * @throws IllegalArgumentException nếu text là null
     */
    public static boolean isHeapCopySameReferenceAsPool(String text) {
        // TODO: Validate text != null, tạo new String(text) và so sánh `==` với text.intern()
        throw new UnsupportedOperationException("TODO: implement isHeapCopySameReferenceAsPool");
    }

    /**
     * Kiểm chứng `.intern()` đưa/truy xuất tham chiếu duy nhất trong String Pool.
     *
     * @throws IllegalArgumentException nếu text là null
     */
    public static boolean isInternedSameReferenceAsPool(String text) {
        // TODO: Validate text != null, tạo new String(text) và so sánh `text.intern() == heapCopy.intern()`
        throw new UnsupportedOperationException("TODO: implement isInternedSameReferenceAsPool");
    }

    /**
     * Minh họa tính bất biến: gọi original.toUpperCase(Locale.ROOT) nhưng KHÔNG gán lại,
     * sau đó trả về original để chứng minh chuỗi gốc không hề bị thay đổi tại chỗ.
     *
     * @throws IllegalArgumentException nếu original là null
     */
    public static String applyTransformIgnoringReturnLeavesOriginal(String original) {
        // TODO: Validate non-null, gọi original.toUpperCase(Locale.ROOT) đứng riêng, trả về original
        throw new UnsupportedOperationException("TODO: implement applyTransformIgnoringReturnLeavesOriginal");
    }
}
