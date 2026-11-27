package com.javalabs.lab08;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Class OOP hoàn chỉnh đầu tiên: đóng gói private fields, constructor chaining,
 * validation trong cả constructor lẫn setter, và hợp đồng equals/hashCode/toString.
 */
public class Product {

    private final String sku;
    private String name;
    private BigDecimal unitPrice;
    private int stock;

    /**
     * Constructor 3 tham số: mặc định stock = 0 bằng constructor chaining `this(...)`.
     */
    public Product(String sku, String name, BigDecimal unitPrice) {
        // TODO: Gọi this(sku, name, unitPrice, 0)
        throw new UnsupportedOperationException("TODO: implement Product(sku, name, unitPrice)");
    }

    /**
     * Constructor đầy đủ:
     * - sku: không null/blank, chuẩn hóa strip().toUpperCase(Locale.ROOT)
     * - name: không null/blank, chuẩn hóa strip()
     * - unitPrice: không null và >= 0, chuẩn hóa setScale(2, RoundingMode.HALF_UP)
     * - stock: >= 0
     *
     * @throws IllegalArgumentException nếu có tham số không hợp lệ
     */
    public Product(String sku, String name, BigDecimal unitPrice, int stock) {
        // TODO: Validate toàn bộ tham số, chuẩn hóa và gán vào các private fields
        throw new UnsupportedOperationException("TODO: implement Product(sku, name, unitPrice, stock)");
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getStock() {
        return stock;
    }

    /**
     * Cập nhật tên sản phẩm (không null/blank, chuẩn hóa strip()).
     */
    public void setName(String name) {
        // TODO: Validate và cập nhật name
        throw new UnsupportedOperationException("TODO: implement setName");
    }

    /**
     * Cập nhật đơn giá (không null, >= 0, chuẩn hóa scale = 2, HALF_UP).
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        // TODO: Validate và cập nhật unitPrice
        throw new UnsupportedOperationException("TODO: implement setUnitPrice");
    }

    /**
     * Nhập thêm hàng vào kho.
     *
     * @throws IllegalArgumentException nếu quantity <= 0
     */
    public void restock(int quantity) {
        // TODO: Validate quantity > 0 và cộng vào stock (dùng Math.addExact hoặc kiểm tra tràn)
        throw new UnsupportedOperationException("TODO: implement restock");
    }

    /**
     * Xuất hàng khỏi kho.
     *
     * @throws IllegalArgumentException nếu quantity <= 0
     * @throws IllegalStateException    nếu quantity > stock hiện có
     */
    public void deductStock(int quantity) {
        // TODO: Phân biệt IllegalArgumentException (tham số sai) vs IllegalStateException (thiếu tồn kho)
        throw new UnsupportedOperationException("TODO: implement deductStock");
    }

    /**
     * Hai Product bằng nhau khi có cùng mã định danh `sku` (bất kể name/unitPrice/stock).
     */
    @Override
    public boolean equals(Object o) {
        // TODO: Cài đặt chuẩn `equals` dựa trên `sku` (không dùng == cho String!)
        throw new UnsupportedOperationException("TODO: implement equals");
    }

    /**
     * Hợp đồng bắt buộc đi kèm equals(): phải tính hashCode từ chính trường `sku`.
     */
    @Override
    public int hashCode() {
        // TODO: Trả về Objects.hash(sku)
        throw new UnsupportedOperationException("TODO: implement hashCode");
    }

    /**
     * Chuỗi biểu diễn dạng: "Product{sku='...', name='...', unitPrice=..., stock=...}".
     */
    @Override
    public String toString() {
        // TODO: Trả về chuỗi mô tả đầy đủ 4 thuộc tính của Product
        throw new UnsupportedOperationException("TODO: implement toString");
    }
}
