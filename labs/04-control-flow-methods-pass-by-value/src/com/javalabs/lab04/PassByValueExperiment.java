package com.javalabs.lab04;

/**
 * Chuỗi thí nghiệm chứng minh Java LUÔN truyền tham trị (Pass-by-Value),
 * kể cả khi tham số là Object reference hoặc Array.
 */
public final class PassByValueExperiment {

    private PassByValueExperiment() {
        // Utility class
    }

    /**
     * Thực hiện hoán đổi hai biến a và b bằng biến tạm trong phạm vi hàm.
     * Lưu ý: Giá trị biến gốc ở caller vẫn sẽ không đổi.
     */
    public static void attemptSwapPrimitives(int a, int b) {
        // TODO: Viết logic hoán đổi a và b bằng biến temp trong thân hàm
        throw new UnsupportedOperationException("TODO: implement attemptSwapPrimitives");
    }

    /**
     * Gán lại tham số target sang một đối tượng Box mới: new Box(newValue, "reassigned").
     * Lưu ý: Tham chiếu phía caller vẫn giữ nguyên object cũ.
     */
    public static void attemptReassignBox(Box target, int newValue) {
        // TODO: Gán target = new Box(newValue, "reassigned");
        throw new UnsupportedOperationException("TODO: implement attemptReassignBox");
    }

    /**
     * Thay đổi trực tiếp trạng thái bên trong đối tượng Box mà target đang trỏ tới.
     *
     * @throws IllegalArgumentException nếu target là null
     */
    public static void mutateBoxValue(Box target, int newValue) {
        // TODO: Kiểm tra null và gọi target.setValue(newValue)
        throw new UnsupportedOperationException("TODO: implement mutateBoxValue");
    }

    /**
     * Gán lại tham số arr sang một mảng mới: new int[]{999, 999}.
     * Lưu ý: Mảng gốc ở caller vẫn không bị thay thế.
     */
    public static void attemptReassignArray(int[] arr) {
        // TODO: Gán arr = new int[]{999, 999};
        throw new UnsupportedOperationException("TODO: implement attemptReassignArray");
    }

    /**
     * Nhân đôi giá trị từng phần tử trực tiếp bên trong mảng arr.
     * (Cẩn thận: không thể dùng for-each để cập nhật phần tử primitive trong mảng!)
     *
     * @throws IllegalArgumentException nếu arr là null
     */
    public static void doubleArrayElementsInPlace(int[] arr) {
        // TODO: Kiểm tra arr != null và dùng vòng lặp chỉ số i để cập nhật arr[i] *= 2
        throw new UnsupportedOperationException("TODO: implement doubleArrayElementsInPlace");
    }

    /**
     * Nếu mutateExisting == true: thay đổi value của input và trả về chính input.
     * Nếu mutateExisting == false: giữ nguyên input, tạo và trả về new Box(value, "detached").
     *
     * @throws IllegalArgumentException nếu input là null
     */
    public static Box createOrMutateDependingOnFlag(Box input, boolean mutateExisting, int value) {
        // TODO: Phân biệt rõ sửa object dùng chung vs tạo instance mới độc lập
        throw new UnsupportedOperationException("TODO: implement createOrMutateDependingOnFlag");
    }
}
