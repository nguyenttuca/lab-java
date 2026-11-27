# RUBRIC — Lab 07: Exception Handling & File I/O cơ bản

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Tính đúng đắn chức năng** | **40** | `CsvParseException` và 4 method trong `SafeFileProcessor` hoạt động chuẩn xác trên cả `data/transactions_sample.csv` và file tạm; `Lab07Test` đạt 100% PASS. |
| **2. Xử lý Edge Cases & Validation** | **25** | Xử lý tốt file rỗng, dòng trắng, cột rỗng cuối dòng (`split(",", -1)`), file thiếu ném `IOException`, thư mục cha chưa tồn tại, `NumberFormatException` giữ `cause`, `AutoCloseable` vẫn đóng khi task lỗi. |
| **3. Java Idioms & Resource Safety** | **25** | `CsvParseException` kế thừa đúng Checked `Exception`; bắt buộc dùng `try-with-resources` (không rò rỉ file handle); dùng `java.nio.file.Path`/`Files` + `StandardCharsets.UTF_8`; không nuốt exception; dùng `BigDecimal` cho tiền tệ. |
| **4. Clean Code & Conventions** | **10** | Biên dịch sạch 0 warning với `-Xlint:all`, không còn `// TODO`, không sửa `ProcessingReport` sai quy định, không để lại file rác sau khi test. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Còn sót `// TODO` hoặc hardcode kết quả cho `data/transactions_sample.csv`.
- Cho `CsvParseException` kế thừa `RuntimeException` thay vì Checked `Exception`, hoặc tự ý bắt nuốt `IOException` trong `processCsvLedger` rồi trả về report rỗng khi file không tồn tại.
- Không dùng `try-with-resources` (hoặc không đóng `BufferedReader` / `AutoCloseable`) làm resource không được đóng khi có exception.
- Nuốt `CsvParseException` bằng khối `catch` rỗng (không tăng `errorCount`, không ghi log lỗi).
- Dùng `double` tính `netTotal` trong `SafeFileProcessor` gây sai số dấu phẩy động.

### `[Major]`
- Quên truyền `cause` vào `super(message, cause)` trong `CsvParseException` làm mất nguyên nhân `NumberFormatException` gốc.
- Dùng `line.split(",")` (thiếu `-1`) khiến dòng có cột rỗng ở cuối bị mất độ dài chính xác hoặc lỗi ngầm.
- Quên `Files.createDirectories(parent)` khi xuất report ra thư mục lồng nhau chưa tồn tại.
- Không chỉ định `StandardCharsets.UTF_8` rõ ràng khi đọc/ghi file (phụ thuộc encoding mặc định của OS).
- Gây warning `-Xlint:serial` hoặc warning khác khi biên dịch với `-Xlint:all`.

### `[Minor]`
- Dùng `java.io.File` cũ rườm rà thay cho `java.nio.file.Path` và `Files`.
- Ghép report bằng `+` trong vòng lặp lỗi thay vì `StringBuilder`.
- Import thừa hoặc code format thiếu nhất quán.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 07

1. **Nuốt Exception (`catch (Exception e) {}`):** Che giấu lỗi ngầm, cực kỳ nguy hiểm trong production.
2. **Mở stream/reader mà không dùng `try-with-resources`:** Khi gặp exception giữa chừng, lệnh `close()` phía dưới bị bỏ qua gây rò rỉ file descriptor.
3. **Bắt `Exception` chung chung rồi vứt `cause` (`throw new CustomException("fail")`):** Mất toàn bộ stack trace gốc để debug.
4. **Dùng `line.split(",")` không có `-1`:** Java mặc định cắt bỏ các chuỗi rỗng ở cuối (`"a,b,"` thành mảng 2 phần tử thay vì 3).
5. **Quên `StandardCharsets.UTF_8`:** Dễ lỗi font chữ khi chạy chéo giữa Windows và macOS/Linux.
6. **Không khai báo `serialVersionUID` trên class kế thừa `Exception`:** Kích hoạt cảnh báo `[serial]` dưới `-Xlint:all`.
