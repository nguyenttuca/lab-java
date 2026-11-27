# RUBRIC — Lab 03: String Immutability, String Pool & Xử lý chuỗi

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Tính đúng đắn chức năng** | **40** | Toàn bộ 6 method trong `StringPoolLab` và 6 method trong `TextProcessor` chạy đúng; `Lab03Test` đạt 100% PASS; không sửa signature starter code. |
| **2. Xử lý Edge Cases & Validation** | **25** | An toàn `null` tuyệt đối; xử lý Unicode whitespace (`\u2003`) với `strip()`; `rawCsv` rỗng/toàn dấu phẩy; validation chặt chẽ cho `maskEmail` và `isValidUsername`; xử lý tốt `count = 0` và `count` lớn. |
| **3. Java Idioms & Hiệu năng chuỗi** | **25** | Dùng `.equals()`/`Objects.equals()` thay `==`; dùng `StringBuilder` trong `repeatWithDelimiter` (không nối `+` trong loop); dùng Text Block `"""` ở `renderJsonPayload`; hiểu rõ String Pool / `.intern()` và tính bất biến. |
| **4. Clean Code & Conventions** | **10** | Không còn `// TODO`, dùng `Locale.ROOT` khi đổi hoa/thường để tránh lỗi locale hệ điều hành (như Turkish `I`), code sạch, biến rõ nghĩa. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Còn sót `// TODO` hoặc hardcode kết quả test.
- Dùng `==` hoặc `!=` để so sánh nội dung chuỗi trong `areContentEqual` hay các logic validate.
- Nối chuỗi bằng toán tử `+` trong vòng lặp của `repeatWithDelimiter` thay vì `StringBuilder`.
- Crash `NullPointerException` khi truyền `null` vào `areContentEqual`, `areEqualIgnoreCase`, hoặc `isValidUsername`.
- Tự ý dùng `trim()` khiến chuỗi chứa Unicode whitespace `\u2003` bị sai kết quả.

### `[Major]`
- Không dùng Text Block (`"""`) trong `renderJsonPayload` (mà ghép chuỗi bằng `\n` thủ công).
- Thiếu kiểm tra các trường hợp email lỗi (`a@@b.com`, `a@b.`, `a@.b`, `a b@c.com`).
- Không dùng `Locale.ROOT` khi gọi `toLowerCase()` / `toUpperCase()` (tiềm ẩn lỗi phụ thuộc locale hệ điều hành).
- Quên `strip()` các giá trị `username`/`role` trong `renderJsonPayload`.

### `[Minor]`
- Tạo thừa nhiều `StringBuilder` hoặc biến trung gian không cần thiết.
- Regex quá rườm rà khi có thể viết gọn hoặc thiếu comment giải thích điều kiện kiểm tra.
- Format/import dư thừa.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 03

1. **`strA == strB` để kiểm tra bằng nhau về chữ:** So sánh địa chỉ tham chiếu thay vì nội dung ký tự.
2. **Gọi `s.toLowerCase();` mà không gán lại biến:** Quên tính bất biến của `String`.
3. **Nối `result += part;` trong vòng lặp lớn:** Tạo rác bộ nhớ $O(n^2)$ trên Heap thay vì dùng `StringBuilder`.
4. **Dùng `trim()` và `isEmpty()` theo thói quen cũ:** Bỏ sót ký tự trắng Unicode (`\u2003`) mà `strip()` và `isBlank()` xử lý chuẩn.
5. **Quên `Locale.ROOT` trong `toUpperCase()`/`toLowerCase()`:** Chạy tốt ở máy Mỹ nhưng có thể đổi sai chữ `'i'`/`'I'` trên hệ điều hành locale Thổ Nhĩ Kỳ.
6. **`a.equalsIgnoreCase(b)` không check `a == null`:** Ném NPE ngay khi `a` là `null`.
7. **Text Block canh lề sai cột:** Làm chuỗi JSON output dính khoảng trắng đầu dòng ngoài ý muốn.
