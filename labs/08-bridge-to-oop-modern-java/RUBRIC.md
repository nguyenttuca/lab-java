# RUBRIC — Lab 08: Cầu nối OOP (`Class`, `enum`, `record`, `Optional`)

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Tính đúng đắn chức năng** | **40** | `OrderStatus`, `Product`, `ProductDto`, và `CatalogLookup` chạy đúng mọi yêu cầu; `Lab08Test` đạt 100% PASS; không đổi chữ ký public API. |
| **2. Xử lý Edge Cases & Validation** | **25** | Validation đầy đủ trong constructor, setter, `ProductDto` compact constructor; phân biệt đúng `IllegalArgumentException` vs `IllegalStateException` ở `deductStock`; xử lý `null`/blank ở `fromCode` và `CatalogLookup`; xử lý `terminal` states ở `OrderStatus`. |
| **3. Modern Java & Hợp đồng `equals`/`hashCode`** | **25** | Cài đặt chuẩn cả `equals` lẫn `hashCode` theo `sku` giúp `HashSet`/`HashMap` hoạt động đúng; dùng `this(...)` constructor chaining trong `Product`; dùng compact constructor trong `record`; dùng chuỗi `Optional` (`filter`/`map`/`orElse`/`orElseThrow`) không gọi `.get()` bừa bãi. |
| **4. Clean Code & Conventions** | **10** | Biên dịch sạch 0 warning với `-Xlint:all`, không còn `// TODO`, không để `Optional` làm field hay tham số, giữ `Locale.ROOT` khi chuẩn hóa `sku`/`code`. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Còn sót `// TODO` hoặc hardcode kết quả test.
- Override `equals()` mà **quên `hashCode()`** (hoặc dùng field khác nhau giữa `equals` và `hashCode`) khiến `HashSet`/`HashMap` tra cứu thất bại.
- Dùng `==` so sánh `sku` trong `Product.equals()` hoặc trong `CatalogLookup.findBySku()`.
- Dùng `Optional.get()` trần mà không kiểm tra, hoặc trả về `null` thay vì `Optional.empty()` ở hàm trả về `Optional`.
- Cho phép `stock` âm hoặc `unitPrice` âm đi lọt qua constructor/setter/compact constructor.

### `[Major]`
- Không dùng constructor chaining `this(...)` ở constructor 3 tham số của `Product`.
- Dùng `if (opt.isPresent()) { return Optional.of(...); }` dài dòng thay cho `.filter(...).map(...)`.
- Nhầm lẫn ném `IllegalArgumentException` thay cho `IllegalStateException` khi `deductStock` vượt tồn kho hiện tại.
- Quên `BigDecimal.setScale(2, RoundingMode.HALF_UP)` hoặc so sánh `unitPrice` bằng `equals` thay vì `compareTo(BigDecimal.ZERO) < 0`.
- Không dùng `Locale.ROOT` khi uppercase `sku` / `code`.

### `[Minor]`
- Format `toString()` lệch mẫu nhẹ nhưng vẫn đủ thông tin, hoặc đặt tên biến lặp chưa gọn.
- Import dư thừa hoặc comment nháp chưa dọn.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 08

1. **Override `equals()` mà thiếu `hashCode()`:** Phá vỡ hợp đồng của `HashSet`/`HashMap`.
2. **So sánh `String sku` bằng `==`:** Thất bại ngay với chuỗi được chuẩn hóa/cấp phát lúc runtime.
3. **Gọi `optional.get()` trực tiếp:** Bị coi là anti-pattern từ Java 10+ (thay bằng `orElseThrow(...)`, `orElse(...)`).
4. **Trả về `null` từ phương thức kiểu `Optional<T>`:** Phá vỡ hoàn toàn ý nghĩa của `Optional`.
5. **Dùng `Optional` làm field của class hoặc tham số của method:** `Optional` sinh ra để làm return type, không phải field/param.
6. **Không validation trong setter:** Constructor kiểm tra kỹ nhưng `setUnitPrice(-5)` lại lọt qua được!
7. **Nhầm `this` trong constructor chaining:** `this(...)` luôn phải là câu lệnh đầu tiên trong constructor.
