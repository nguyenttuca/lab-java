# Lab 04 — Control Flow hiện đại, Methods & Cơ chế truyền tham số

- **Giai đoạn:** 1 — Nền tảng Java & Cú pháp
- **Hình thức:** Starter Code (`// TODO`)
- **Độ khó:** Trung bình
- **Thời gian dự kiến:** 60–90 phút
- **Phiên bản chuẩn:** Java 21 LTS

---

## 1. Mục tiêu học tập (Learning Objectives)

Sau khi hoàn thành bài Lab này, bạn sẽ nắm vững:
- **`switch` expression** hiện đại (Java 17+): cú pháp mũi tên `->`, đa nhãn (`"MON", "TUE"`), trả giá trị và từ khóa `yield` trong khối lệnh `{ ... }`.
- Vòng lặp **enhanced `for-each`** (cùng giới hạn không thay đổi được phần tử mảng primitive qua biến lặp) và **labeled `break` / `continue`** để điều khiển chính xác vòng lặp lồng nhau.
- **Method Overloading** (quy tắc phân giải overload theo độ ưu tiên: exact match → widening → autoboxing → `varargs`) và cú pháp **`varargs` (`T...`)**.
- Chứng minh bằng thực nghiệm nguyên lý nền tảng của JVM: **Java LUÔN truyền tham trị (Pass-by-Value)** — kể cả với Object và Array (truyền *bản sao của tham chiếu*, tức Pass-by-Value of Reference).

---

## 2. Góc nhìn cho dev C++ / JS

- **Không có `&` (Pass-by-Reference) như C++:** Trong C++, viết `void swap(int& a, int& b)` sẽ hoán đổi trực tiếp 2 biến của caller. Trong Java, **không thể** viết hàm `swap(a, b)` làm đổi giá trị 2 biến cục bộ của caller — kể cả khi truyền vào Object (`Box`) hay `int[]`! Tham số truyền vào luôn là **bản sao**: với primitive là bản sao giá trị, còn với Object là **bản sao địa chỉ tham chiếu**. Gán lại `target = new Box(...)` chỉ thay đổi biến cục bộ trong hàm, nhưng gọi `target.setValue(...)` sẽ thay đổi dữ liệu trên Heap mà biến gốc cũng đang trỏ tới (hành vi giống hệt truyền Object/Array trong JavaScript).
- **`switch` expression không bị fall-through:** Khác `switch` truyền thống của C++/JS rất dễ quên `break;` gây trôi xuống case dưới, `switch` expression dùng `->` chỉ thực thi đúng nhánh khớp và bắt buộc bao phủ hết trường hợp (`default`) khi trả về giá trị.
- **Bẫy `for-each` trên mảng primitive:** Trong C++, `for (int& x : arr) x *= 2;` sửa trực tiếp mảng. Trong Java, `for (int x : arr)` chỉ copy từng phần tử vào `x`; sửa `x *= 2` **không hề ảnh hưởng** tới mảng `arr`! Muốn sửa phần tử tại chỗ bắt buộc dùng vòng lặp `for (int i = 0; i < arr.length; i++)`.
- **Thứ tự phân giải Method Overloading & `varargs`:** Java luôn ưu tiên khớp chính xác kiểu (`int`) và widening trước, sau đó mới tới autoboxing, và `varargs` (`int...`) luôn là lựa chọn **cuối cùng** nếu đã có hàm cố định khớp. Một hàm chỉ được phép có tối đa **một** tham số `varargs` và phải đứng ở **cuối cùng**.

---

## 3. Đề bài chi tiết

Lab 04 gồm class mô phỏng có sẵn [Box.java](src/com/javalabs/lab04/Box.java) và 2 file starter code cần hoàn thiện `// TODO` trong package `com.javalabs.lab04`:

### Phần A: `PassByValueExperiment.java`

1. `attemptSwapPrimitives(int a, int b)` → `void`
   - Thực hiện logic hoán đổi `a` và `b` trong thân hàm (dùng biến `temp`) để kiểm chứng phía caller không đổi.
2. `attemptReassignBox(Box target, int newValue)` → `void`
   - Thực hiện gán lại `target = new Box(newValue, "reassigned");` trong thân hàm để kiểm chứng biến `Box` của caller vẫn trỏ vào object cũ.
3. `mutateBoxValue(Box target, int newValue)` → `void`
   - Nếu `target == null`, ném `IllegalArgumentException`. Gọi `target.setValue(newValue)` để thay đổi trạng thái object chung trên Heap.
4. `attemptReassignArray(int[] arr)` → `void`
   - Gán lại `arr = new int[]{999, 999};` trong thân hàm để kiểm chứng biến mảng ở caller không bị đổi tham chiếu.
5. `doubleArrayElementsInPlace(int[] arr)` → `void`
   - Nếu `arr == null`, ném `IllegalArgumentException`. Nhân đôi từng phần tử trực tiếp trong mảng `arr` (không dùng `for-each` làm hỏng logic cập nhật).
6. `createOrMutateDependingOnFlag(Box input, boolean mutateExisting, int value)` → `Box`
   - Nếu `input == null`, ném `IllegalArgumentException`.
   - Nếu `mutateExisting == true`: cập nhật `value` lên `input` và trả về chính tham chiếu `input`.
   - Nếu `mutateExisting == false`: giữ nguyên `input`, tạo và trả về `new Box(value, "detached")`.

### Phần B: `ControlFlowEngine.java`

1. `calculateShippingFee(String dayCode, int weightKg)` → `int`
   - Bắt buộc dùng **`switch` expression** (Java 17+) với `->` và `yield`.
   - Validate: `dayCode` không `null`/blank, `weightKg > 0` (sai ném `IllegalArgumentException`). Chuẩn hóa `dayCode.strip().toUpperCase(Locale.ROOT)`.
   - Quy tắc tính phí:
     - `"MON"`, `"TUE"`, `"WED"`, `"THU"`, `"FRI"`: đơn giá `10_000` / kg; nếu `weightKg > 10` giảm 20% tổng phí (dùng block `{ ... yield ...; }`).
     - `"SAT"`, `"SUN"`: đơn giá `15_000` / kg (không giảm).
     - Mã khác: ném `IllegalArgumentException` trong nhánh `default`.
2. `findFirstMatchCoordinates(int[][] matrix, int target)` → `int[]`
   - Nếu `matrix == null`, ném `IllegalArgumentException`.
   - Duyệt theo thứ tự hàng từ trên xuống, cột từ trái sang; hỗ trợ jagged array và hàng `null` (bỏ qua hàng `null`).
   - Bắt buộc dùng **labeled `break`** (ví dụ `outer:`) để thoát ngay cả 2 vòng lặp khi tìm thấy phần tử đầu tiên bằng `target`. Trả về `new int[]{row, col}` hoặc `new int[]{-1, -1}` nếu không thấy.
3. `sumPositiveRowsSkippingNegatives(int[][] matrix)` → `int`
   - Nếu `matrix == null`, ném `IllegalArgumentException`.
   - Duyệt từng hàng bằng **enhanced `for-each`** kết hợp **labeled `continue`**: bỏ qua hàng `null` hoặc bất kỳ hàng nào có chứa số âm (`< 0`), cộng dồn các hàng hợp lệ bằng `Math.addExact`.
4. Nhóm hàm Overload `formatMetric(...)`:
   - `formatMetric(String name, int value)` → `"<name>=<value>"`
   - `formatMetric(String name, double value)` → `"<name>=<value>"` với 2 chữ số thập phân theo `Locale.US` (ví dụ `"temp=36.50"`; ném `IllegalArgumentException` nếu `value` là `NaN` hoặc `Infinite`).
   - `formatMetric(String name, int... values)` (varargs) → `"<name>=[v1,v2,...]"` (nếu không truyền số nào → `"<name>=[]"`; nếu `values == null` → `IllegalArgumentException`).
   - Cả 3 overload đều phải `.strip()` `name` và ném `IllegalArgumentException` nếu `name == null || name.isBlank()`.
5. `sumAll(int... numbers)` → `int`
   - Nếu `numbers == null`, ném `IllegalArgumentException`.
   - Tính tổng các tham số `varargs` bằng `for-each` và `Math.addExact` (không truyền tham số nào trả về `0`).

---

## 4. Ví dụ Input / Output & Edge Cases bắt buộc

### Ví dụ minh họa
- `calculateShippingFee("fri", 12)` → `96_000` (`12 * 10_000 * 80%`)
- `calculateShippingFee("SAT", 3)` → `45_000`
- `sumPositiveRowsSkippingNegatives(new int[][]{{1, 2}, {3, -1, 5}, null, {4, 6}})` → `13`
- `formatMetric("cpu", 80)` → `"cpu=80"` (khớp overload `(String, int)`, không vào `varargs`)
- `formatMetric("scores", 10, 20, 30)` → `"scores=[10,20,30]"`
- `formatMetric("empty")` → `"empty=[]"`

### Danh sách Edge Cases bắt buộc kiểm tra
1. Gọi `attemptSwapPrimitives`, `attemptReassignBox`, `attemptReassignArray` không làm đổi biến gốc ở phía caller; gọi `mutateBoxValue` và `doubleArrayElementsInPlace` lại làm đổi nội dung đối tượng/mảng.
2. `matrix` có chứa hàng `null`, hàng rỗng `{}`, hoặc độ dài từng hàng khác nhau (jagged array).
3. `dayCode` có khoảng trắng hai đầu hoặc chữ thường (`" mon "`), mã không hợp lệ (`"ABC"`), `weightKg <= 0`.
4. Truyền mảng `null` tường minh vào tham số `varargs`: `sumAll((int[]) null)` và `formatMetric("x", (int[]) null)`.
5. Tràn `int` khi cộng trong `sumAll` hoặc `sumPositiveRowsSkippingNegatives`.

---

## 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/04-control-flow-methods-pass-by-value
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab04.Lab04Test
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab04.Lab04Test
  ```
