package com.javalabs.lab01;

import java.util.Locale;

/**
 * Bộ công cụ luyện tập 8 kiểu nguyên thủy (Primitives), ép kiểu, tràn số,
 * mô phỏng unsigned, hằng số {@code final} và {@code var} trong Java 21.
 */
public final class PrimitivesToolkit {

    public static final double KELVIN_OFFSET = 273.15;
    public static final int MAX_UINT8 = 255;

    private PrimitivesToolkit() {
        // Utility class: không cho phép khởi tạo instance
    }

    /**
     * Cộng 2 số int sao cho không bị tràn số 32-bit, trả về giá trị đúng dạng long.
     */
    public static long sumWithoutOverflow(int a, int b) {
        // TODO: Ép kiểu sang long trước khi cộng để tránh tràn số 32-bit
        return (long)a + b;
        throw new UnsupportedOperationException("TODO: implement sumWithoutOverflow");
    }

    /**
     * Cộng 2 số int an toàn: nếu tràn giới hạn int thì clamp về
     * Integer.MAX_VALUE hoặc Integer.MIN_VALUE thay vì wrap-around.
     */
    public static int addExactOrClamp(int a, int b) {
        // TODO: Gợi ý dùng Math.addExact(a, b) và xử lý ArithmeticException,
        // hoặc kiểm tra trên kiểu long rồi clamp về [Integer.MIN_VALUE, Integer.MAX_VALUE]
        long sum = (long)a + b;
        if (Integer.MIN_VALUE > sum) {
            return Integer.MIN_VALUE;
        }
        if (Integer.MAX_VALUE < sum) {
            return Integer.MAX_VALUE;
        }

        return sum;
        throw new UnsupportedOperationException("TODO: implement addExactOrClamp");
    }

    /**
     * Chuyển bit pattern của một int có dấu sang giá trị không dấu dạng long [0, 4294967295].
     */
    public static long toUnsigned32(int signedValue) {
        // TODO: Dùng Integer.toUnsignedLong hoặc bitmask 0xFFFF_FFFFL
        throw new UnsupportedOperationException("TODO: implement toUnsigned32");
    }

    /**
     * Chuyển bit pattern của một byte có dấu (-128..127) sang giá trị không dấu [0, 255].
     */
    public static int toUnsignedByte(byte signedByte) {
        // TODO: Dùng Byte.toUnsignedInt hoặc bitmask 0xFF
        throw new UnsupportedOperationException("TODO: implement toUnsignedByte");
    }

    /**
     * Chặn value trong đoạn [Short.MIN_VALUE, Short.MAX_VALUE] rồi narrowing cast về short.
     */
    public static short clampToShort(int value) {
        // TODO: Clamp value trước khi ép kiểu (short) để tránh cắt bit sai lệch
        throw new UnsupportedOperationException("TODO: implement clampToShort");
    }

    /**
     * Đổi độ C sang độ F bằng kiểu float: F = C * 9.0f / 5.0f + 32.0f.
     *
     * @throws IllegalArgumentException nếu celsius là NaN, Infinite, hoặc < -273.15f
     */
    public static float celsiusToFahrenheit(float celsius) {
        // TODO: Validate nhiệt độ hợp lệ và tính bằng literal float (hậu tố f)
        throw new UnsupportedOperationException("TODO: implement celsiusToFahrenheit");
    }

    /**
     * Tính trung bình cộng chính xác dạng double.
     *
     * @throws IllegalArgumentException nếu count <= 0
     */
    public static double calculateAverage(int totalSum, int count) {
        // TODO: Validate count > 0 và tránh integer division trước khi trả về double
        throw new UnsupportedOperationException("TODO: implement calculateAverage");
    }

    /**
     * Dịch vòng tròn ký tự in hoa ('A'..'Z') theo offset (hỗ trợ cả offset âm và > 26).
     *
     * @throws IllegalArgumentException nếu letter không thuộc ['A', 'Z']
     */
    public static char shiftUppercaseAscii(char letter, int offset) {
        // TODO: Kiểm tra letter in hoa ASCII, chuẩn hóa phép modulo số âm và cast về char
        throw new UnsupportedOperationException("TODO: implement shiftUppercaseAscii");
    }

    /**
     * Kiểm tra value có nằm trong đoạn [minInclusive, maxInclusive] hay không.
     *
     * @throws IllegalArgumentException nếu có tham số NaN hoặc minInclusive > maxInclusive
     */
    public static boolean isWithinSafeRange(double value, double minInclusive, double maxInclusive) {
        // TODO: Kiểm tra NaN, kiểm tra min <= max và trả về kết quả boolean
        throw new UnsupportedOperationException("TODO: implement isWithinSafeRange");
    }

    /**
     * Phân loại dải nhiệt độ và trả về chuỗi dạng "<BAND> (<kelvin> K)" theo Locale.US.
     * Yêu cầu sử dụng từ khóa `var` cho biến cục bộ trong hàm này.
     *
     * @throws IllegalArgumentException nếu celsius là NaN, Infinite hoặc < -KELVIN_OFFSET
     */
    public static String classifyTemperatureBand(double celsius) {
        // TODO:
        // 1. Validate celsius hợp lệ (>= -KELVIN_OFFSET, không NaN/Infinite)
        // 2. Dùng `var` cho biến cục bộ (ví dụ: var kelvin = celsius + KELVIN_OFFSET;)
        // 3. Phân loại FREEZING (<0), NORMAL [0,40), HIGH [40,75), CRITICAL (>=75)
        // 4. Format: String.format(Locale.US, "%s (%.2f K)", band, kelvin)
        throw new UnsupportedOperationException("TODO: implement classifyTemperatureBand");
    }
}
