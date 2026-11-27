# RUBRIC — Lab 02: Wrapper Classes, Autoboxing & Độ chính xác số học

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Tính đúng đắn chức năng** | **40** | Toàn bộ 8 method trong `WrapperDiagnostics` và 5 method trong `CurrencyCalculator` hoạt động chính xác; `Lab02Test` đạt 100% PASS; giữ nguyên signature starter code. |
| **2. Xử lý Edge Cases & Validation** | **25** | Không nổ NPE khi unboxing `null`; xử lý chuẩn Integer Cache biên `-129/-128/127/128`; parse lỗi an toàn; validate số âm/`NaN`/`Infinite`/tỷ lệ giảm giá ngoài `[0, 100]`; chia hóa đơn không thất thoát xu lẻ. |
| **3. Java Idioms & Độ chính xác số học** | **25** | Dùng `Objects.equals` / `.equals` cho `Integer`; dùng `Integer.valueOf` (không dùng `new Integer` deprecated); khởi tạo `BigDecimal` từ `String` (tuyệt đối không qua `double`); dùng `RoundingMode.HALF_UP`; dùng `compareTo` thay cho `equals` ở `hasSameMonetaryValue`. |
| **4. Clean Code & Conventions** | **10** | Không còn `// TODO` chưa làm, tuân thủ naming convention, code sạch rõ, không lặp logic validate quá mức, biến/hằng rõ nghĩa. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Còn sót `// TODO` hoặc hardcode kết quả test.
- Dùng `a == b` để so sánh giá trị trong `areWrapperValuesEqual` hoặc `hasSameMonetaryValue`.
- Dùng `double` / `float` làm trung gian tính tiền hoặc khởi tạo `new BigDecimal(Double.parseDouble(...))` trong `CurrencyCalculator`.
- Dùng `BigDecimal.equals()` trong `hasSameMonetaryValue` khiến `"10.5"` và `"10.50"` trả về `false`.
- Crash `NullPointerException` khi truyền `null` vào bất kỳ method nào của `WrapperDiagnostics`, hoặc crash `ArithmeticException` ở `applyPercentageDiscount` / `splitBillEvenly` do không truyền `RoundingMode`.
- Chia hóa đơn bị mất hoặc dư xu lẻ (tổng các phần chia không bằng `totalAmount`).

### `[Major]`
- Dùng `new Integer(...)` hoặc `new Double(...)` (constructor đã deprecated từ Java 9 và bị đánh dấu cảnh báo ở Java 21).
- Không xử lý `NaN` / `Infinity` trong `safeMultiplyOrZero` khi có 1 tham số `null` và 1 tham số `NaN` (hoặc thiếu validate `NaN`).
- Dùng `Boolean.parseBoolean` trực tiếp trong `parseStrictBooleanOrNull` khiến chuỗi rác như `"yes"` trả về `Boolean.FALSE` thay vì `null`.
- Thiếu `Math.addExact` hoặc kiểm tra tràn số trong `sumNonNullIntegers` / `safeAddLongs`.
- Dùng `RoundingMode.HALF_EVEN` hoặc `FLOOR` sai yêu cầu `HALF_UP`.

### `[Minor]`
- Dùng `BigDecimal.valueOf(0)` thay vì hằng `BigDecimal.ZERO` ở các phép khởi tạo đơn giản.
- Code parse chuỗi lặp lại không tách helper private nhỏ (khi cần).
- Đặt tên biến chưa rõ ràng, còn import thừa hoặc comment nháp.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 02

1. **`Integer a == Integer b`:** So sánh tham chiếu object thay vì giá trị; chỉ "tình cờ chạy đúng" trong khoảng `[-128, 127]`.
2. **Bẫy unboxing trong toán tử 3 ngôi (`?:`):** Ví dụ `Integer x = flag ? a : 0;` khi `a` là `Integer` có thể ép unboxing `a` ngay cả khi mục tiêu là gán sang `Integer`, gây NPE nếu `a == null`.
3. **`new BigDecimal(double)`:** Mang theo sai số nhị phân (ví dụ `new BigDecimal(0.1)` tạo ra `0.100000000000000005551115...`).
4. **`bigDecimalA.equals(bigDecimalB)` để so sánh tiền:** Sai với `"10.5"` và `"10.50"`; phải dùng `compareTo(...) == 0`.
5. **`BigDecimal.divide(divisor)` không có scale & `RoundingMode`:** Gặp số thập phân vô hạn tuần hoàn như `10 / 3` sẽ nổ `ArithmeticException`.
6. **`Boolean.parseBoolean("abc")`:** Không ném exception mà âm thầm trả về `false`.
7. **`new Integer(x)`:** Deprecated từ Java 9; luôn dùng `Integer.valueOf(x)` hoặc autoboxing.
