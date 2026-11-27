# RUBRIC — Lab 04: Control Flow hiện đại, Methods & Truyền tham số

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Tính đúng đắn chức năng** | **40** | Toàn bộ 6 method trong `PassByValueExperiment` và 7 method/overload trong `ControlFlowEngine` chạy đúng; `Lab04Test` đạt 100% PASS; không sửa signature starter code hay `Box.java`. |
| **2. Xử lý Edge Cases & Validation** | **25** | Xử lý tốt hàng `null`/rỗng/jagged array trong ma trận 2D; validate `dayCode`, `weightKg <= 0`, `NaN`/`Infinite`, mảng `varargs` là `null`, tràn số `int` với `Math.addExact`. |
| **3. Java Idioms & Control Flow hiện đại** | **25** | Dùng đúng **`switch` expression (`->` và `yield`)**; dùng **labeled `break`** và **labeled `continue`** kết hợp `for-each`; hiểu đúng `for-each` không sửa được mảng primitive; cài đặt đúng overload và `varargs`; thực hiện đúng các hàm thí nghiệm Pass-by-Value (không để thân hàm rỗng đối phó). |
| **4. Clean Code & Conventions** | **10** | Không còn `// TODO`, không sửa `Box.java` sai quy định, đặt tên label (`outer:`, `rowLoop:`) rõ nghĩa, dùng `Locale.US` cho số thập phân. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Còn sót `// TODO` hoặc tự ý sửa `Box.java` để làm thay đổi hành vi test.
- Dùng `for (int x : arr) { x *= 2; }` trong `doubleArrayElementsInPlace` khiến mảng gốc không thay đổi.
- Crash `NullPointerException` khi ma trận 2D chứa hàng `null`, khi `dayCode` hoặc mảng `varargs` là `null`, hoặc khi `Box` là `null`.
- Viết sai logic `labeled break` / `labeled continue` làm thoát sai vòng lặp (ví dụ trả về tọa độ sai hoặc cộng nhầm phần tử trước số âm trong hàng lỗi).

### `[Major]`
- Vẫn dùng `switch` truyền thống có `break;` kiểu C++/JS thay cho `switch` expression (`->` / `yield`) ở `calculateShippingFee`.
- Không sử dụng `labeled break` / `labeled continue` đúng chỗ (dùng biến cờ boolean cồng kềnh thay thế).
- Để thân hàm `attemptSwapPrimitives`, `attemptReassignBox`, hoặc `attemptReassignArray` rỗng hoàn toàn thay vì viết phép gán thực nghiệm.
- Thiếu kiểm tra `NaN` / `Infinite` trong `formatMetric(String, double)` hoặc quên `Locale.US`.
- Không dùng `Math.addExact` trong `sumAll` / `sumPositiveRowsSkippingNegatives`.

### `[Minor]`
- Tên nhãn vòng lặp chung chung (như `l1:`, `x:`) thay vì tên gợi nghĩa (`outer:`, `rowLoop:`).
- Dùng `String` `+` trong vòng lặp nhỏ của `formatMetric(String, int...)` thay vì `StringBuilder`.
- Import thừa hoặc formatting thiếu đồng nhất.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 04

1. **Tưởng `for (int x : arr)` sửa được phần tử trong `arr`:** `x` chỉ là biến cục bộ nhận bản sao giá trị.
2. **Dùng `switch` fall-through cũ:** Quên `break;` hoặc bỏ qua `switch` expression của Java 17+.
3. **Tin rằng gán `target = new Box(...)` bên trong method sẽ thay đổi biến ngoài caller:** Hiểu nhầm Pass-by-Value of Reference thành Pass-by-Reference (`&`).
4. **Cộng phần tử đầu hàng trước khi kiểm tra số âm trong hàng đó:** Nếu hàng `{3, -1, 5}` bị cộng `3` trước khi gặp `-1` rồi mới `continue`, tổng sẽ sai!
5. **Quên check `values == null` cho tham số `varargs` (`int...`):** Người gọi truyền `(int[]) null` sẽ gây `NullPointerException` khi lặp.
6. **Không kiểm tra hàng `null` trong `int[][]`:** Vì mỗi phần tử của mảng 2D trong Java là một tham chiếu mảng độc lập (jagged array), nên từng hàng có thể `null`.
