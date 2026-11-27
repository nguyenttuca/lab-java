package com.javalabs.lab02;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Xử lý tính toán tiền tệ & số thập phân chính xác tuyệt đối với BigDecimal.
 */
public final class CurrencyCalculator {

    public static final int CURRENCY_SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private CurrencyCalculator() {
        // Utility class
    }

    /**
     * Cộng dồn danh sách số thập phân dạng chuỗi (ví dụ "0.1", "0.2") bằng BigDecimal.
     * Kết quả chuẩn hóa về scale = 2 với RoundingMode.HALF_UP.
     * Nếu không truyền phần tử nào (amounts.length == 0), trả về 0.00.
     *
     * @throws IllegalArgumentException nếu amounts null hoặc có phần tử null/blank/sai định dạng số
     */
    public static BigDecimal sumDecimalStrings(String... amounts) {
        // TODO: Khởi tạo bằng new BigDecimal(str.strip()), tuyệt đối không qua double
        throw new UnsupportedOperationException("TODO: implement sumDecimalStrings");
    }

    /**
     * Tính tổng tiền một dòng hàng sau thuế VAT:
     * total = unitPrice * quantity * (1 + vatRate), làm tròn scale = 2 (HALF_UP).
     *
     * @throws IllegalArgumentException nếu chuỗi không hợp lệ hoặc unitPrice < 0, quantity < 0, vatRate < 0
     */
    public static BigDecimal calculateLineTotalWithTax(String unitPrice, int quantity, String vatRate) {
        // TODO: Parse String -> BigDecimal, validate giá trị không âm và tính thuế chính xác
        throw new UnsupportedOperationException("TODO: implement calculateLineTotalWithTax");
    }

    /**
     * Áp dụng giảm giá theo phần trăm (0..100):
     * result = amount * (100 - discountPercent) / 100, làm tròn scale = 2 (HALF_UP).
     *
     * @throws IllegalArgumentException nếu amount/discountPercent null, amount < 0,
     *                                  hoặc discountPercent không thuộc [0, 100]
     */
    public static BigDecimal applyPercentageDiscount(BigDecimal amount, BigDecimal discountPercent) {
        // TODO: Nhân trước rồi dùng divide(..., CURRENCY_SCALE, ROUNDING) để tránh ArithmeticException
        throw new UnsupportedOperationException("TODO: implement applyPercentageDiscount");
    }

    /**
     * Chia đều hóa đơn totalAmount cho peopleCount người theo đơn vị xu (0.01):
     * - Chuẩn hóa totalAmount về scale = 2 (HALF_UP).
     * - Mỗi người nhận phần cơ bản (chia làm tròn xuống 2 chữ số thập phân).
     * - Phần cent dư được phân bổ lần lượt +0.01 cho những người đầu tiên (index 0, 1, ...).
     * - Tổng các phần trong mảng trả về phải bằng chính xác totalAmount sau chuẩn hóa.
     *
     * @throws IllegalArgumentException nếu totalAmount null, totalAmount < 0, hoặc peopleCount <= 0
     */
    public static BigDecimal[] splitBillEvenly(BigDecimal totalAmount, int peopleCount) {
        // TODO: Chia bằng RoundingMode.DOWN ở scale 2 rồi phân bổ từng 0.01 cho phần dư
        throw new UnsupportedOperationException("TODO: implement splitBillEvenly");
    }

    /**
     * So sánh hai số tiền BigDecimal theo giá trị số học (ví dụ 10.5 và 10.50 là bằng nhau).
     * Hỗ trợ an toàn trường hợp tham số null.
     */
    public static boolean hasSameMonetaryValue(BigDecimal a, BigDecimal b) {
        // TODO: Không dùng `==` hay `a.equals(b)`; dùng `a.compareTo(b) == 0`
        throw new UnsupportedOperationException("TODO: implement hasSameMonetaryValue");
    }
}
