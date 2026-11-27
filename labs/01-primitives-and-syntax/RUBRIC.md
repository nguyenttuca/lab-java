# RUBRIC — Lab 01: Primitives, Ép kiểu & Toán tử

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Tính đúng đắn chức năng** | **40** | Toàn bộ 10 method trong `PrimitivesToolkit` và 2 method trong `SensorCliFormatter` chạy đúng logic; `Lab01Test` đạt 100% PASS; không đổi signature starter code. |
| **2. Xử lý Edge Cases & Validation** | **25** | Xử lý chuẩn xác tràn số dương/âm (`Integer.MAX_VALUE`/`MIN_VALUE`), `byte`/`int` âm khi chuyển unsigned, `offset` âm lớn ở `shiftUppercaseAscii`, `NaN`/`Infinity`, nhiệt độ `< -273.15`, `count <= 0`, bẫy newline `Scanner`, khoảng trắng ở `location`. |
| **3. Java Idioms & Tư duy ngôn ngữ** | **25** | Hiểu đúng widening trước khi cộng `long`, dùng `Math.addExact` hoặc kiểm tra ngưỡng chuẩn, dùng `Integer.toUnsignedLong`/`Byte.toUnsignedInt` (hoặc bitmask đúng), dùng hậu tố `f` cho `float`, dùng `var` đúng chỗ ở `classifyTemperatureBand`, dùng `Locale.US` khi format/parse số thập phân, giữ `Scanner` truyền qua tham số. |
| **4. Clean Code & Conventions** | **10** | Không còn `// TODO` chưa làm, tuân thủ `camelCase`/`PascalCase`/`UPPER_SNAKE_CASE`, code sạch gọn, early validation rõ ràng, không tạo biến thừa. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Còn sót `// TODO` ném `UnsupportedOperationException` hoặc hardcode kết quả test.
- Cộng `int` rồi mới gán vào `long` (`long sum = a + b;` trong `sumWithoutOverflow`) gây tràn số 32-bit âm thầm.
- Chia nguyên trong `calculateAverage` (`totalSum / count`) làm mất phần thập phân.
- Bị lỗi trôi dòng `nextLine()` sau `nextInt()` trong `SensorCliFormatter.readAndFormatReading`, hoặc hardcode `new Scanner(System.in)` bên trong hàm khiến `Lab01Test` bị treo/sai dữ liệu.
- Crash `NullPointerException` / sai định dạng khi gặp edge case bắt buộc (ví dụ `offset` âm ở `shiftUppercaseAscii` gây ra ký tự ngoài `'A'..'Z'` hoặc `IndexOutOfBounds`).

### `[Major]`
- Không dùng `Locale.US` khi gọi `String.format` / `printf` (gây ra dấu `,` thay vì `.` trên máy có locale châu Âu/Việt Nam).
- Thiếu validate `NaN` / `Infinity` hoặc nhiệt độ `< -273.15`.
- Không sử dụng `var` theo yêu cầu ở `classifyTemperatureBand`.
- Viết logic thủ công dài dòng nhưng bỏ qua API chuẩn như `Math.addExact`, `Byte.toUnsignedInt`, `Integer.toUnsignedLong`.
- Quên xử lý `scanner.useLocale(Locale.US)` hoặc lỗi đọc token `double` khi input dùng dấu chấm `.`.

### `[Minor]`
- Tên biến không theo `camelCase` hoặc thiếu rõ ràng.
- Import dư thừa hoặc comment `// TODO` cũ chưa dọn sau khi hoàn thành.
- Format thụt lề thiếu đồng nhất.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 01

1. **`long res = a + b;` (khi `a`, `b` là `int`):** Thực hiện phép cộng 32-bit bị tràn *trước* khi mở rộng sang 64-bit.
2. **`double avg = totalSum / count;` (khi cả hai là `int`):** Phép chia nguyên làm cụt phần thập phân trước khi gán sang `double`.
3. **Quên hậu tố `f` / `L`:** Viết `9.0 / 5.0` thay vì `9.0f / 5.0f` khi tính trên `float`, hoặc viết `4294967295` thiếu `L`.
4. **Dùng `(short) value` trực tiếp không clamp:** Narrowing cast âm thầm cắt bit cao (`(short) 40000` thành `-25536`).
5. **Quên phép `%` số âm trong Java:** `-1 % 26` trong Java ra `-1` (không phải `25`), cần cộng `26` rồi `% 26` lại khi dịch `char`.
6. **Bẫy `Scanner.nextInt()` + `Scanner.nextLine()`:** Quên tiêu thụ `\n` còn sót khiến dòng tiếp theo đọc chuỗi rỗng.
7. **Hardcode `System.in` trong helper method:** Làm mất khả năng unit test tự động bằng `Scanner(String)`.
