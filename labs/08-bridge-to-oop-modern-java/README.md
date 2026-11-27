# Lab 08 — Cầu nối sang OOP (Basic Class, `enum`, `record`, `Optional`)

- **Giai đoạn:** 1 — Nền tảng Java & Cú pháp (Bài tổng kết trước Giai đoạn 2)
- **Hình thức:** Starter Code (`// TODO`)
- **Độ khó:** Trung bình
- **Thời gian dự kiến:** 75–90 phút
- **Phiên bản chuẩn:** Java 21 LTS

---

## 1. Mục tiêu học tập (Learning Objectives)

Sau khi hoàn thành bài Lab này, bạn sẽ nắm vững các thành tố hiện đại để bước vào thiết kế OOP thực tế ở Giai đoạn 2:
- Xây dựng **một Class hoàn chỉnh đầu tiên** (`Product`): trường `private`, **constructor chaining (`this(...)`)**, validation ngay lúc khởi tạo & cập nhật, và kiểm soát thay đổi trạng thái (thay vì để setter thô).
- Hiểu và cài đặt đúng hợp đồng **`toString()`**, **`equals(Object)`**, và **`hashCode()`** — đảm bảo object hoạt động chính xác bên trong `HashSet` và `HashMap`.
- Sử dụng **`enum` nâng cao** (`OrderStatus`) có field riêng, constructor `private`, và phương thức xử lý nghiệp vụ (`canTransitionTo`, `fromCode`).
- Sử dụng **`record`** (`ProductDto`) của Java hiện đại cho DTO bất biến kèm **compact constructor** để validate dữ liệu.
- Sử dụng **`Optional<T>`** (`CatalogLookup`) để xử lý kết quả tìm kiếm rỗng một cách an toàn và biểu cảm (`map`, `filter`, `orElse`, `orElseThrow`).

---

## 2. Góc nhìn cho dev C++ / JS

- **Override `equals()` mà quên `hashCode()` là lỗi kinh điển:** Trong JS không có khái niệm hash-equality tùy chỉnh trên Object (chỉ so sánh tham chiếu trong `Set`/`Map`); trong C++ bạn tách riêng `operator==` và `std::hash`. Trong Java, **mọi `Object` đều thừa kế cả `equals()` lẫn `hashCode()`**. Nếu hai object `a.equals(b) == true` nhưng `a.hashCode() != b.hashCode()`, `HashSet` và `HashMap` sẽ đẩy chúng vào **2 bucket khác nhau** và tìm không ra nhau! Luật bất biến: *"Same equals → Must have same hashCode"*.
- **`enum` trong Java là một Class đầy đủ, không phải số nguyên:** Khác với `enum` chỉ là `int` trong C++ cũ hay object/union trong TS, mỗi hằng số `enum` trong Java là một **singleton instance** thực thụ, có thể chứa field (`code`, `displayName`), constructor `private`, và method riêng.
- **`record` không phải là thay thế toàn bộ `class`:** `record` sinh sẵn `private final` fields, accessor (`dto.sku()` không có chữ `get`), `equals()`, `hashCode()`, `toString()`. Nó lý tưởng cho **DTO bất biến / Value Object**, nhưng không phù hợp cho Entity có vòng đời thay đổi trạng thái (như `Product` có tồn kho thay đổi).
- **`Optional<T>` thay cho thói quen trả `null` hay `undefined`:** Trong JS bạn thường dùng `?.` và `??`. Trong Java, trả về `Optional<Product>` ở các hàm tra cứu giúp ép caller xử lý rõ ràng trường hợp không tìm thấy. **Cảnh báo:** Không gọi `.get()` thẳng trên `Optional` mà chưa kiểm tra (hãy dùng `.map()`, `.orElse()`, `.orElseThrow()`), và không lạm dụng `Optional` làm field của class hay tham số truyền vào method.

---

## 3. Đề bài chi tiết

Hãy hoàn thiện các phần `// TODO` trong 4 file thuộc package `com.javalabs.lab08`:

### Phần A: `OrderStatus.java` (`enum` nâng cao)

Các hằng số đã được khai báo sẵn: `PENDING`, `PAID`, `SHIPPED`, `DELIVERED`, `CANCELLED` với các thuộc tính `code`, `displayName`, `terminal`.
1. `canTransitionTo(OrderStatus next)` → `boolean`
   - Nếu `next == null`, trả về `false`.
   - Quy tắc chuyển trạng thái hợp lệ (khuyến khích dùng `switch` expression):
     - `PENDING` → `PAID` hoặc `CANCELLED`
     - `PAID` → `SHIPPED` hoặc `CANCELLED`
     - `SHIPPED` → `DELIVERED`
     - `DELIVERED`, `CANCELLED` (trạng thái kết thúc `terminal`) → không thể chuyển sang bất kỳ trạng thái nào (kể cả chính nó).
2. `fromCode(String code)` → `Optional<OrderStatus>`
   - Nếu `code == null || code.isBlank()` → trả về `Optional.empty()`.
   - Chuẩn hóa `code.strip().toUpperCase(Locale.ROOT)` và tìm hằng số khớp `status.getCode()`, trả về `Optional.of(...)` hoặc `Optional.empty()`.

### Phần B: `Product.java` (Class hoàn chỉnh & Hợp đồng `equals`/`hashCode`)

1. Constructor 3 tham số `Product(String sku, String name, BigDecimal unitPrice)`:
   - Dùng **constructor chaining** `this(sku, name, unitPrice, 0);`.
2. Constructor 4 tham số `Product(String sku, String name, BigDecimal unitPrice, int stock)`:
   - Validate:
     - `sku` không `null`/blank → chuẩn hóa `sku.strip().toUpperCase(Locale.ROOT)`.
     - `name` không `null`/blank → chuẩn hóa `name.strip()`.
     - `unitPrice` không `null` và `>= 0` → chuẩn hóa `setScale(2, RoundingMode.HALF_UP)`.
     - `stock >= 0`.
     - Nếu vi phạm → ném `IllegalArgumentException`.
3. `setName(String name)` & `setUnitPrice(BigDecimal unitPrice)`:
   - Validate và chuẩn hóa tương tự như trong constructor (không cho phép gán dữ liệu rác vào object đã tồn tại).
4. `restock(int quantity)` & `deductStock(int quantity)`:
   - `restock(quantity)`: nếu `quantity <= 0` → `IllegalArgumentException`; ngược lại cộng vào `stock`.
   - `deductStock(quantity)`: nếu `quantity <= 0` → `IllegalArgumentException`; nếu `quantity > stock` → `IllegalStateException`; ngược lại trừ `stock`.
5. `equals(Object o)` & `hashCode()`:
   - Hai `Product` được xem là bằng nhau về định danh nghiệp vụ khi có cùng `sku` (đã chuẩn hóa uppercase).
   - Bắt buộc cài đặt đồng bộ cả `equals` và `hashCode` dựa trên `sku`.
6. `toString()`:
   - Trả về chuỗi dễ đọc theo mẫu: `"Product{sku='%s', name='%s', unitPrice=%s, stock=%d}"` (với `unitPrice.toPlainString()`).

### Phần C: `ProductDto.java` (`record` bất biến)

1. Compact constructor `public ProductDto { ... }`:
   - Validate & chuẩn hóa tương tự `Product` (`sku` uppercase, `name` stripped, `unitPrice` không âm scale 2 `HALF_UP`, `stock >= 0`).
2. `ProductDto.from(Product product)` → `ProductDto`:
   - Nếu `product == null` → `IllegalArgumentException`. Chuyển đổi `Product` sang DTO bất biến.
3. `isInStock()` → `boolean`:
   - Trả về `stock > 0`.

### Phần D: `CatalogLookup.java` (`Optional<T>` Pipeline)

1. `findBySku(Collection<Product> catalog, String sku)` → `Optional<Product>`
   - Nếu `catalog == null` → `IllegalArgumentException`.
   - Nếu `sku == null || sku.isBlank()` → `Optional.empty()`.
   - Chuẩn hóa `sku.strip().toUpperCase(Locale.ROOT)`, tìm sản phẩm đầu tiên khớp (bỏ qua phần tử `null` nếu có trong `catalog`).
2. `findInStockDtoBySku(Collection<Product> catalog, String sku)` → `Optional<ProductDto>`
   - Dùng chuỗi `Optional`: `findBySku(catalog, sku).filter(p -> p.getStock() > 0).map(ProductDto::from)`.
3. `getUnitPriceOrZero(Collection<Product> catalog, String sku)` → `BigDecimal`
   - Dùng `Optional`: `findBySku(catalog, sku).map(Product::getUnitPrice).orElse(new BigDecimal("0.00"))`.
4. `requireBySku(Collection<Product> catalog, String sku)` → `Product`
   - Dùng `orElseThrow(() -> new NoSuchElementException(...))` (không gọi `.get()` trần!).

---

## 4. Ví dụ Input / Output & Edge Cases bắt buộc

### Ví dụ minh họa
- `OrderStatus.PENDING.canTransitionTo(OrderStatus.PAID)` → `true`; `OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CANCELLED)` → `false`
- `OrderStatus.fromCode(" shp ")` → `Optional.of(OrderStatus.SHIPPED)`
- Hai đối tượng `new Product(" sku-1 ", "Mouse", new BigDecimal("19.9"))` và `new Product("SKU-1", "Mouse Pro", new BigDecimal("25.00"))` phải có `p1.equals(p2) == true` và `p1.hashCode() == p2.hashCode()`, khi đưa vào `HashSet` chỉ còn 1 phần tử.
- `CatalogLookup.findInStockDtoBySku(catalog, "SKU-OUT-OF-STOCK")` → `Optional.empty()`

### Danh sách Edge Cases bắt buộc kiểm tra
1. Chuyển trạng thái ngược hoặc chuyển từ trạng thái `terminal` (`DELIVERED`, `CANCELLED`), hoặc `next == null`.
2. Đưa `Product` vào `HashSet` / dùng làm key trong `HashMap` rồi tra cứu bằng một instance `Product` mới cùng `sku` (khác `name`/`unitPrice`).
3. Truyền số lượng `<= 0` vào `restock`/`deductStock`, hoặc `deductStock` vượt tồn kho (phải phân biệt `IllegalArgumentException` vs `IllegalStateException`).
4. `ProductDto` từ chối `sku` blank, `unitPrice` âm, `stock < 0` ngay trong compact constructor.
5. `CatalogLookup` nhận `catalog` chứa phần tử `null`, hoặc `sku` là `null`/blank, hoặc sản phẩm hết hàng (`stock == 0`).

---

## 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/08-bridge-to-oop-modern-java
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab08.Lab08Test
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab08.Lab08Test
  ```
