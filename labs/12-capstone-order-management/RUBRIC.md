# RUBRIC — Lab 12: Mini-Project Tổng hợp OOP & SOLID (`[CORE-400]`)

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Đáp ứng Acceptance Criteria** | **25** | Đủ quản lý kho `Product`, vòng đời `Order` (`NEW→PAID→SHIPPED→DELIVERED`/`CANCELLED`), chiến lược `DiscountPolicy`, trừ/hoàn kho, xuất báo cáo file và CLI tách biệt. |
| **2. Xử lý Edge Cases & Tính toàn vẹn** | **25** | Đạt đủ 8 Edge Cases: đặt đơn nguyên tử khi món sau hết hàng, gộp/chặn SKU trùng trong cùng đơn, snapshot giá lúc mua, không hoàn kho kép khi hủy lại, giảm giá không làm âm `finalTotal`, Defensive Copying. |
| **3. Kiến trúc phân tầng & SOLID (SRP, OCP, DIP)** | **35** | Chia rõ 5 tầng (`model`, `exception`, `repository`, `service`, `app`); `OrderService` nhận `interface` qua Constructor Injection (DIP); `DiscountPolicy` mở rộng không cần sửa `OrderService` (OCP); Service/Repo không chứa `System.out`/`Scanner` (SRP); dùng `BigDecimal`, `Optional`, `try-with-resources`. |
| **4. Clean Code, Conventions & Entry Point** | **15** | Biên dịch sạch `--release 21` với `-Xlint:all`, naming conventions chuẩn, `Main.java` chạy tự động không treo `System.in`, không dùng `assert` phụ thuộc `-ea`, tự dọn file tạm sau test. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Thiếu entry point (`Main.java`), hoặc mặc định khi chạy `Main` bị treo chờ `Scanner(System.in)`.
- Vi phạm nghiêm trọng **SRP / Phân tầng**: viết toàn bộ logic nghiệp vụ lẫn I/O `Scanner`/`System.out` chung vào một class (God Class) hoặc đặt `System.out.println` bên trong `OrderService`/`Repository`.
- Vi phạm **DIP**: `OrderService` tự `new InMemoryOrderRepository()` cứng bên trong thay vì inject `interface` từ ngoài.
- Đặt đơn **không nguyên tử**: sản phẩm 1 đã bị trừ kho trước khi phát hiện sản phẩm 2 thiếu hàng.
- Hủy đơn không hoàn kho, hoặc cho phép hủy lại đơn đã `CANCELLED` làm hoàn kho 2 lần, hoặc cho phép hủy đơn đã `SHIPPED`/`DELIVERED`.
- Lộ internal mutable list (`order.getItems()`, `repo.findAll()`) hoặc dùng `double` cho tiền tệ.

### `[Major]`
- Không snapshot đơn giá trong `OrderItem` (đổi giá `Product` sau khi đặt làm đổi tổng tiền đơn cũ).
- Giảm giá cố định lớn hơn `subtotal` làm `finalTotal` bị âm.
- Không xử lý trường hợp 1 đơn hàng chứa 2 dòng cùng SKU dẫn tới trừ lố tồn kho.
- Báo cáo file tính cả doanh thu từ đơn `CANCELLED`, hoặc không dùng `try-with-resources`/`StandardCharsets.UTF_8`, hoặc để lại file rác trong thư mục Lab sau khi chạy test.
- Dùng `assert` trong `Main.java` hoặc `Optional.get()` trần không kiểm tra.

### `[Minor]`
- `Order` thiếu `equals`/`hashCode` hoặc `toString()` rõ ràng.
- Có một vài đoạn logic lặp trong validate/làm tròn `BigDecimal`.
- Xuất hiện warning khi biên dịch với `-Xlint:all`.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 12

1. **God Class & Lẫn lộn I/O với Business Logic:** Cho `OrderService` đọc `Scanner` hoặc in thẳng ra `System.out`.
2. **Tight Coupling (`new InMemoryRepository()` trong Service):** Không thể thay sang File/Database Repository nếu không sửa code Service (vi phạm DIP).
3. **Mutate Kho từng bước không kiểm tra trước (Partial Deduction):** Gây rò rỉ/sai lệch tồn kho khi món sau trong đơn bị lỗi.
4. **Tham chiếu sống tới `Product.getUnitPrice()` thay vì snapshot giá lúc mua:** Sửa giá catalog làm biến đổi hóa đơn lịch sử.
5. **Double-Refund / Double-Restock Bug:** Quên kiểm tra trạng thái trước khi hủy đơn hoàn kho.
6. **Nuốt `IOException` hoặc quên đóng file stream khi xuất báo cáo.**
7. **Để `finalTotal` âm do voucher/giảm giá lớn hơn giá trị đơn.**
