package com.javalabs.lab04;

import java.util.Locale;

/**
 * Thực hành Control Flow hiện đại (switch expression, labeled break/continue, for-each),
 * Method Overloading và Varargs.
 */
public final class ControlFlowEngine {

    private ControlFlowEngine() {
        // Utility class
    }

    /**
     * Tính phí vận chuyển bằng switch expression (-> và yield):
     * - MON, TUE, WED, THU, FRI: 10_000 / kg; nếu weightKg > 10 thì giảm 20% tổng phí.
     * - SAT, SUN: 15_000 / kg (không giảm).
     * - Mã khác hoặc null/blank hoặc weightKg <= 0 -> ném IllegalArgumentException.
     */
    public static int calculateShippingFee(String dayCode, int weightKg) {
        // TODO: Validate dayCode & weightKg, chuẩn hóa chữ hoa và dùng switch expression (Java 17+)
        throw new UnsupportedOperationException("TODO: implement calculateShippingFee");
    }

    /**
     * Tìm tọa độ {row, col} ĐẦU TIÊN chứa target trong ma trận 2D (từ trên xuống, trái sang phải).
     * Bỏ qua an toàn nếu gặp hàng null; trả về {-1, -1} nếu không tìm thấy.
     * Bắt buộc dùng labeled `break` để thoát cả hai vòng lặp ngay khi tìm thấy.
     *
     * @throws IllegalArgumentException nếu matrix là null
     */
    public static int[] findFirstMatchCoordinates(int[][] matrix, int target) {
        // TODO: Dùng labeled break (ví dụ `outer:`) và bỏ qua hàng null
        throw new UnsupportedOperationException("TODO: implement findFirstMatchCoordinates");
    }

    /**
     * Tính tổng các phần tử của những hàng hợp lệ trong ma trận:
     * Dùng enhanced `for-each` kết hợp labeled `continue` để bỏ qua hàng null
     * hoặc bất kỳ hàng nào có chứa số âm (< 0). Cộng dồn bằng Math.addExact.
     *
     * @throws IllegalArgumentException nếu matrix là null
     * @throws ArithmeticException      nếu tổng vượt giới hạn int
     */
    public static int sumPositiveRowsSkippingNegatives(int[][] matrix) {
        // TODO: Dùng enhanced for-each + labeled continue để bỏ qua cả hàng nếu gặp số < 0
        throw new UnsupportedOperationException("TODO: implement sumPositiveRowsSkippingNegatives");
    }

    /**
     * Overload 1: Định dạng metric kiểu int thành "<name>=<value>".
     *
     * @throws IllegalArgumentException nếu name null hoặc blank
     */
    public static String formatMetric(String name, int value) {
        // TODO: Validate name và trả về "<name.strip()>=<value>"
        throw new UnsupportedOperationException("TODO: implement formatMetric(String, int)");
    }

    /**
     * Overload 2: Định dạng metric kiểu double thành "<name>=<value>" (2 chữ số thập phân, Locale.US).
     *
     * @throws IllegalArgumentException nếu name null/blank hoặc value là NaN/Infinite
     */
    public static String formatMetric(String name, double value) {
        // TODO: Validate name & value, trả về String.format(Locale.US, "%s=%.2f", name.strip(), value)
        throw new UnsupportedOperationException("TODO: implement formatMetric(String, double)");
    }

    /**
     * Overload 3 (varargs): Định dạng danh sách giá trị int thành "<name>=[v1,v2,...]".
     * Nếu không truyền giá trị nào -> "<name>=[]".
     *
     * @throws IllegalArgumentException nếu name null/blank hoặc values là null
     */
    public static String formatMetric(String name, int... values) {
        // TODO: Validate name & values, dùng StringBuilder ghép chuỗi kết quả
        throw new UnsupportedOperationException("TODO: implement formatMetric(String, int...)");
    }

    /**
     * Tính tổng toàn bộ các số truyền vào qua varargs (dùng for-each và Math.addExact).
     * Nếu không truyền số nào -> trả về 0.
     *
     * @throws IllegalArgumentException nếu numbers là null
     * @throws ArithmeticException      nếu tổng tràn giới hạn int
     */
    public static int sumAll(int... numbers) {
        // TODO: Kiểm tra numbers != null, lặp for-each và cộng bằng Math.addExact
        throw new UnsupportedOperationException("TODO: implement sumAll");
    }
}
