# RUBRIC — Lab 11: Polymorphism, Abstract Class & Interface (`[PAY-305]`)

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Đáp ứng Acceptance Criteria** | **30** | Đầy đủ `PaymentMethod` (có `default`/`static`), `Refundable`, `ReceiptNotifier`, `AbstractPaymentProcessor`, ≥ 3 kênh thanh toán, ≥ 2 kênh thông báo, và `CheckoutService` hoàn tiền + mở rộng kênh mới. |
| **2. Xử lý Edge Cases & Validation** | **25** | Bao phủ đủ 6 Edge Cases: request sai/vượt hạn mức, kênh không tồn tại/trùng mã, hoàn tiền nhiều lần vượt tổng gốc, hoàn tiền kênh không `Refundable`, không notifier vẫn chạy, mở rộng kênh mới. |
| **3. Polymorphism, Interface & Abstract Design** | **30** | Phân tách hợp lý `interface` (hợp đồng & capability `Refundable`) vs `abstract class` (quy trình chung & trạng thái); `CheckoutService` không có `if-else` theo loại kênh; dùng `BigDecimal` và `record` chuẩn xác. |
| **4. Clean Code, Package & Entry Point** | **15** | Chia package sạch (`model`, `payment`, `notification`, `exception`, `service`), biên dịch sạch `--release 21`, có `Main.java` tự động kiểm chứng đủ kịch bản không treo stdin và không phụ thuộc `-ea`. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Thiếu entry point (`Main.java`), hoặc `Main` bị treo chờ `Scanner(System.in)`.
- Dùng chuỗi `if-else` hoặc `switch` kiểm tra loại kênh (`"CARD"`, `"EWALLET"`, `"BANK"`) trong `CheckoutService` thay vì gọi đa hình qua `PaymentMethod`.
- Nhét hàm `refund(...)` vào thẳng `PaymentMethod` khiến `BankTransferPayment` phải implement giả rồi ném `UnsupportedOperationException` (vi phạm Interface Segregation & LSP).
- Không có `abstract class` chia sẻ quy trình chung (copy-paste toàn bộ validate & tạo receipt ở cả 3 lớp con).
- Cho phép hoàn tiền cộng dồn vượt quá số tiền giao dịch gốc, hoặc dùng `double` cho tiền tệ/phí.

### `[Major]`
- `PaymentMethod` thiếu `default` method hoặc `static` method theo yêu cầu thiết kế interface Java 8+.
- Không có kịch bản chứng minh mở rộng kênh thanh toán mới ở `Main` mà không sửa `CheckoutService`.
- Không tách custom exceptions hoặc nuốt lỗi khi thanh toán/hoàn tiền.
- Đặt hết class vào một package phẳng hoặc dùng từ khóa `assert` trong `Main.java`.

### `[Minor]`
- `PaymentReceipt` hoặc `PaymentRequest` dùng class mutable nhiều boilerplate thay vì `record` bất biến.
- Lặp logic làm tròn `BigDecimal` thay vì gọi `static` helper trên `PaymentMethod`.
- Còn compiler warning với `-Xlint:all`.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 11

1. **`if (channel.equals("CARD")) ... else if (...)` trong Service:** Phá hủy hoàn toàn tính đa hình và nguyên lý Open/Closed.
2. **Fat Interface:** Bắt mọi `PaymentMethod` đều phải cài đặt `refund()` dù nghiệp vụ không hỗ trợ.
3. **Copy-paste luồng xử lý chung ở mọi subclass:** Quên vai trò Template/Base workflow của `abstract class`.
4. **Hoàn tiền không cộng dồn lịch sử đã hoàn:** Giao dịch `100.00`, gọi `refund(60)` 2 lần liên tiếp thành công thành hoàn `120.00`!
5. **Dùng `double` tính phí phần trăm:** Sai số làm tròn tài chính.
6. **Không phân biệt kiểm tra capability (`instanceof Refundable`) với kiểm tra concrete class (`instanceof CreditCardPayment`).**
