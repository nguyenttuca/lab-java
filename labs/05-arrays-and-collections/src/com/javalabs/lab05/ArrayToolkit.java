package com.javalabs.lab05;

import java.util.Arrays;

/**
 * Bộ công cụ thao tác Mảng 1D và 2D chuẩn Java với java.util.Arrays.
 */
public final class ArrayToolkit {

    private ArrayToolkit() {
        // Utility class
    }

    /**
     * Trả về một bản sao đã sắp xếp tăng dần của source mà KHÔNG làm thay đổi mảng source gốc.
     *
     * @throws IllegalArgumentException nếu source là null
     */
    public static int[] sortedCopyWithoutMutatingOriginal(int[] source) {
        // TODO: Dùng Arrays.copyOf để sao chép trước khi gọi Arrays.sort
        throw new UnsupportedOperationException("TODO: implement sortedCopyWithoutMutatingOriginal");
    }

    /**
     * Biểu diễn mảng 2D thành chuỗi đọc được (ví dụ "[[1, 2], [3, 4]]").
     *
     * @throws IllegalArgumentException nếu matrix là null
     */
    public static String formatArray2D(int[][] matrix) {
        // TODO: Dùng Arrays.deepToString (tránh Arrays.toString trên mảng 2D)
        throw new UnsupportedOperationException("TODO: implement formatArray2D");
    }

    /**
     * Chuyển vị ma trận hình chữ nhật [rows][cols] thành [cols][rows].
     *
     * @throws IllegalArgumentException nếu matrix null, rỗng, có hàng null/rỗng,
     *                                  hoặc các hàng có độ dài không bằng nhau
     */
    public static int[][] transposeRectangularMatrix(int[][] matrix) {
        // TODO: Validate hình chữ nhật chặt chẽ trước khi tạo mảng mới và chuyển vị
        throw new UnsupportedOperationException("TODO: implement transposeRectangularMatrix");
    }

    /**
     * Đổi kích thước mảng source sang newLength:
     * - Nếu newLength <= source.length: giữ newLength phần tử đầu.
     * - Nếu newLength > source.length: các ô mở rộng được điền giá trị padValue.
     *
     * @throws IllegalArgumentException nếu source null hoặc newLength < 0
     */
    public static int[] resizeAndPad(int[] source, int newLength, int padValue) {
        // TODO: Dùng Arrays.copyOf kết hợp Arrays.fill
        throw new UnsupportedOperationException("TODO: implement resizeAndPad");
    }

    /**
     * So sánh nội dung sâu (deep equality) của hai mảng 2D.
     */
    public static boolean areMatricesDeeplyEqual(int[][] a, int[][] b) {
        // TODO: Dùng Arrays.deepEquals(a, b)
        throw new UnsupportedOperationException("TODO: implement areMatricesDeeplyEqual");
    }
}
