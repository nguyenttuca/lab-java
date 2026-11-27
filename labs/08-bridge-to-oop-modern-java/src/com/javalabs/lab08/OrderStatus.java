package com.javalabs.lab08;

import java.util.Locale;
import java.util.Optional;

/**
 * Enum nâng cao mô hình hóa vòng đời đơn hàng kèm mã code, mô tả và logic chuyển trạng thái.
 */
public enum OrderStatus {

    PENDING("PND", "Pending Payment", false),
    PAID("PAID", "Paid", false),
    SHIPPED("SHP", "Shipped", false),
    DELIVERED("DLV", "Delivered", true),
    CANCELLED("CXL", "Cancelled", true);

    private final String code;
    private final String displayName;
    private final boolean terminal;

    OrderStatus(String code, String displayName, boolean terminal) {
        this.code = code;
        this.displayName = displayName;
        this.terminal = terminal;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isTerminal() {
        return terminal;
    }

    /**
     * Kiểm tra có được phép chuyển từ trạng thái hiện tại sang `next` hay không:
     * - PENDING -> PAID, CANCELLED
     * - PAID -> SHIPPED, CANCELLED
     * - SHIPPED -> DELIVERED
     * - DELIVERED, CANCELLED (terminal) -> không chuyển sang bất kỳ trạng thái nào (kể cả chính nó)
     * - Nếu next == null -> false
     */
    public boolean canTransitionTo(OrderStatus next) {
        // TODO: Kiểm tra next != null và dùng switch expression theo `this`
        throw new UnsupportedOperationException("TODO: implement canTransitionTo");
    }

    /**
     * Tìm OrderStatus theo mã code (không phân biệt hoa/thường và khoảng trắng hai đầu).
     * Trả về Optional.empty() nếu code null, blank hoặc không khớp mã nào.
     */
    public static Optional<OrderStatus> fromCode(String code) {
        // TODO: Chuẩn hóa code với Locale.ROOT và trả về Optional<OrderStatus>
        throw new UnsupportedOperationException("TODO: implement fromCode");
    }
}
