package com.javalabs.lab08;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Record bất biến biểu diễn dữ liệu tóm tắt của Product (DTO),
 * áp dụng Compact Constructor để chuẩn hóa và validate invariants.
 */
public record ProductDto(String sku, String name, BigDecimal unitPrice, int stock) {

    public ProductDto {
        // TODO:
        // 1. Kiểm tra sku/name không null/blank, chuẩn hóa sku (strip + uppercase Locale.ROOT) và name (strip)
        // 2. Kiểm tra unitPrice != null && >= 0, chuẩn hóa setScale(2, RoundingMode.HALF_UP)
        // 3. Kiểm tra stock >= 0
        // (Trong compact constructor, gán trực tiếp lên `sku = ...;` sẽ tự gán vào record component!)
        throw new UnsupportedOperationException("TODO: implement ProductDto compact constructor");
    }

    /**
     * Tạo DTO bất biến từ một thực thể Product.
     *
     * @throws IllegalArgumentException nếu product là null
     */
    public static ProductDto from(Product product) {
        // TODO: Validate product != null và trả về new ProductDto(...)
        throw new UnsupportedOperationException("TODO: implement ProductDto.from");
    }

    /**
     * Trả về true nếu còn hàng trong kho (stock > 0).
     */
    public boolean isInStock() {
        // TODO: Trả về stock > 0
        throw new UnsupportedOperationException("TODO: implement isInStock");
    }
}
