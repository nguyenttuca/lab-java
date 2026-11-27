package com.javalabs.lab08;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Tra cứu danh mục sản phẩm an toàn bằng Optional<T> (tránh trả null hoặc gọi .get() mù quáng).
 */
public final class CatalogLookup {

    private CatalogLookup() {
        // Utility class
    }

    /**
     * Tìm Product theo SKU (chuẩn hóa strip + uppercase theo Locale.ROOT).
     * Bỏ qua các phần tử null nếu có trong catalog.
     *
     * @throws IllegalArgumentException nếu catalog là null
     */
    public static Optional<Product> findBySku(Collection<Product> catalog, String sku) {
        // TODO: Nếu sku null/blank -> Optional.empty(); tìm phần tử khớp và trả về Optional.of(p)
        throw new UnsupportedOperationException("TODO: implement findBySku");
    }

    /**
     * Tìm sản phẩm còn hàng (stock > 0) và chuyển đổi sang ProductDto bằng chuỗi Optional
     * (`findBySku(...).filter(...).map(...)`).
     *
     * @throws IllegalArgumentException nếu catalog là null
     */
    public static Optional<ProductDto> findInStockDtoBySku(Collection<Product> catalog, String sku) {
        // TODO: Kết hợp findBySku + .filter(...) + .map(ProductDto::from)
        throw new UnsupportedOperationException("TODO: implement findInStockDtoBySku");
    }

    /**
     * Lấy đơn giá của sản phẩm theo SKU; nếu không tìm thấy trả về BigDecimal "0.00".
     *
     * @throws IllegalArgumentException nếu catalog là null
     */
    public static BigDecimal getUnitPriceOrZero(Collection<Product> catalog, String sku) {
        // TODO: Dùng findBySku(...).map(Product::getUnitPrice).orElse(new BigDecimal("0.00"))
        throw new UnsupportedOperationException("TODO: implement getUnitPriceOrZero");
    }

    /**
     * Bắt buộc tìm thấy Product theo SKU; nếu không có thì ném NoSuchElementException.
     *
     * @throws IllegalArgumentException nếu catalog là null
     * @throws NoSuchElementException   nếu không tìm thấy sản phẩm khớp
     */
    public static Product requireBySku(Collection<Product> catalog, String sku) {
        // TODO: Dùng findBySku(...).orElseThrow(() -> new NoSuchElementException(...))
        throw new UnsupportedOperationException("TODO: implement requireBySku");
    }
}
