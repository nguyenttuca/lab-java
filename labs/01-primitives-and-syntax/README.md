# Lab 01 — Kiểu nguyên thủy (Primitives), Ép kiểu & Toán tử

- **Giai đoạn:** 1 — Nền tảng Java & Cú pháp
- **Hình thức:** Starter Code (`// TODO`)
- **Độ khó:** Cơ bản
- **Thời gian dự kiến:** 60–90 phút
- **Phiên bản chuẩn:** Java 21 LTS

---

## 1. Mục tiêu học tập (Learning Objectives)

Sau khi hoàn thành bài Lab này, bạn sẽ nắm vững:
- Toàn bộ **8 kiểu dữ liệu nguyên thủy (Primitive Types)** của Java (`byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean`) cùng kích thước và miền giá trị cố định trên mọi kiến trúc CPU.
- Cách xử lý **tràn số (Integer Overflow)** bằng ép kiểu mở rộng (widening) và `Math.addExact`, cũng như cách mô phỏng số không dấu (`unsigned`) trong Java.
- Quy tắc **Widening vs Narrowing Casting**, hậu tố literal (`L`, `F`, dấu phân tách `_`), từ khóa `final` và local type inference `var`.
- Thao tác nhập xuất chuẩn với `Scanner` (tránh bẫy trôi dòng `nextLine()`) và định dạng chuỗi/bảng số liệu bằng `printf` / `String.format` với `Locale.US`.

---

## 2. Góc nhìn cho dev C++ / JS

- **Không có kiểu `unsigned`:** Khác với `unsigned int` hay `uint8_t` trong C/C++, mọi kiểu số nguyên của Java đều **có dấu** (trừ `char` 16-bit). Ví dụ `byte` chỉ chứa `-128..127`; nếu ép `(byte) 200`, giá trị nhận được là `-56`. Muốn đọc giá trị bit không dấu, phải dùng `Byte.toUnsignedInt(...)`, `Integer.toUnsignedLong(...)` hoặc phép bitmask (`& 0xFF`).
- **`boolean` tách biệt hoàn toàn với số:** Không thể viết `if (1)` hay `(int) true` như trong C++, và cũng không có cơ chế *truthy/falsy* như JavaScript (`if ("hello")` hay `if (0)` đều **lỗi compile**).
- **`char` là UTF-16 (2 byte), không phải ASCII (1 byte):** Trong C++, `char` thường là 1 byte; trong Java `char` là 2 byte không dấu (`0..65535`). Tuy nhiên bạn vẫn thực hiện phép toán số học trên `char` như `'A' + 1` (kết quả là `int`, cần ép `(char)` khi thu hẹp về `char`).
- **Phép chia số nguyên & literal mặc định:** Trong JS chỉ có kiểu `number` (float64) nên `5 / 2 == 2.5`. Trong Java (giống C++), `5 / 2 == 2` vì cả hai toán hạng đều là `int`. Ngoài ra, số thực viết trần như `9.0` luôn có kiểu `double`; gán cho `float` bắt buộc thêm hậu tố `f` (`9.0f`).
- **`final` khác `const` trong C++ và JS:** `final` trên biến cục bộ nghĩa là *chỉ gán được một lần* (single assignment — tương tự `const` trong JS với biến primitive), không cần biết giá trị lúc compile như `constexpr` trong C++.
- **Bẫy `Scanner.nextLine()` sau `nextInt()`:** Tương tự `cin >> x; getline(cin, s);` trong C++, `nextInt()` không tiêu thụ ký tự xuống dòng `\n`. Gọi ngay `nextLine()` sau đó sẽ đọc phải chuỗi rỗng `""` nếu không dọn phần còn lại của dòng trước.

---

## 3. Đề bài chi tiết

Hãy hoàn thiện các phương thức được đánh dấu `// TODO` trong 2 file thuộc package `com.javalabs.lab01`:

### Phần A: `PrimitivesToolkit.java`

1. `sumWithoutOverflow(int a, int b)` → `long`
   - Trả về tổng chính xác của 2 số `int` dưới kiểu `long`, không bị tràn số 32-bit (ví dụ `Integer.MAX_VALUE + 1` phải trả về `2147483648L`).
2. `addExactOrClamp(int a, int b)` → `int`
   - Cộng 2 số `int`; nếu tràn ngưỡng `int`, chặn (clamp) về `Integer.MAX_VALUE` (tràn dương) hoặc `Integer.MIN_VALUE` (tràn âm) thay vì wrap-around. Khuyến khích dùng `Math.addExact`.
3. `toUnsigned32(int signedValue)` → `long`
   - Diễn giải 32 bit của `signedValue` như số nguyên không dấu và trả về dạng `long` trong `[0, 4294967295]`.
4. `toUnsignedByte(byte signedByte)` → `int`
   - Diễn giải 8 bit của `signedByte` như `uint8` trong `[0, 255]`.
5. `clampToShort(int value)` → `short`
   - Chặn `value` vào khoảng `[Short.MIN_VALUE, Short.MAX_VALUE]` (`-32768..32767`) rồi ép kiểu an toàn về `short`.
6. `celsiusToFahrenheit(float celsius)` → `float`
   - Chuyển độ C sang độ F (`celsius * 9.0f / 5.0f + 32.0f`). Nếu `Float.isNaN(celsius)`, `Float.isInfinite(celsius)`, hoặc `celsius < (float) -KELVIN_OFFSET` (`-273.15f`), ném `IllegalArgumentException`.
7. `calculateAverage(int totalSum, int count)` → `double`
   - Tính trung bình cộng chính xác dạng `double` (không bị mất phần thập phân do chia nguyên). Nếu `count <= 0`, ném `IllegalArgumentException`.
8. `shiftUppercaseAscii(char letter, int offset)` → `char`
   - Kiểm tra `letter` phải nằm trong `'A'..'Z'` (ngược lại ném `IllegalArgumentException`). Dịch vòng tròn trong bảng chữ cái in hoa theo `offset` (hỗ trợ cả `offset` âm và `|offset| > 26`).
9. `isWithinSafeRange(double value, double minInclusive, double maxInclusive)` → `boolean`
   - Kiểm tra `value` có nằm trong đoạn `[minInclusive, maxInclusive]`. Nếu bất kỳ tham số nào là `NaN` hoặc `minInclusive > maxInclusive`, ném `IllegalArgumentException`.
10. `classifyTemperatureBand(double celsius)` → `String`
    - Sử dụng từ khóa `var` cho ít nhất một biến cục bộ (ví dụ `var kelvin = ...;`).
    - Nếu `Double.isNaN(celsius)`, `Double.isInfinite(celsius)` hoặc `celsius < -KELVIN_OFFSET`, ném `IllegalArgumentException`.
    - Phân loại ngưỡng theo độ C:
      - `< 0.0` → `"FREEZING"`
      - `[0.0, 40.0)` → `"NORMAL"`
      - `[40.0, 75.0)` → `"HIGH"`
      - `>= 75.0` → `"CRITICAL"`
    - Trả về chuỗi theo đúng format (dùng `Locale.US`): `"<BAND> (<kelvin> K)"` với `kelvin` làm tròn 2 chữ số thập phân (ví dụ `"NORMAL (298.15 K)"`).

### Phần B: `SensorCliFormatter.java`

1. `readAndFormatReading(Scanner scanner)` → `String`
   - Nhận `Scanner` từ tham số (tuyệt đối **không** hardcode `new Scanner(System.in)` bên trong hàm này để unit test truyền dữ liệu giả lập không bị treo).
   - Đọc 4 dòng theo đúng thứ tự:
     - Dòng 1: `sensorId` (`int`)
     - Dòng 2: `location` (`String` — đọc cả dòng, có thể chứa khoảng trắng như `"Server Room A"`)
     - Dòng 3: `temperature` (`double`)
     - Dòng 4: `online` (`boolean`)
   - Validate: nếu `scanner == null`, `sensorId <= 0`, `location.isBlank()`, `Double.isNaN(temperature)`, `Double.isInfinite(temperature)` hoặc `temperature < -273.15` → ném `IllegalArgumentException`.
   - Định dạng chuỗi kết quả theo đúng mẫu (`Locale.US`):
     `"[#%04d] %-18s | %6.2f C | %s"` (cột cuối là `"ONLINE"` hoặc `"OFFLINE"`).
2. `printSummaryTable(PrintStream out, String sensorName, int sampleCount, double avgTemp, boolean alert)` → `void`
   - Nếu `out == null`, `sensorName == null`, `sensorName.isBlank()`, `sampleCount < 0`, `Double.isNaN(avgTemp)` hoặc `Double.isInfinite(avgTemp)` → ném `IllegalArgumentException`.
   - Dùng `out.printf(Locale.US, ...)` in đúng 1 dòng theo mẫu:
     `"Sensor: %-15s | Samples: %4d | Avg: %7.2f C | Alert: %s%n"` (cột Alert là `"YES"` hoặc `"NO"`).

---

## 4. Ví dụ Input / Output & Edge Cases bắt buộc

### Ví dụ `PrimitivesToolkit`
- `sumWithoutOverflow(Integer.MAX_VALUE, 10)` → `2147483657L`
- `addExactOrClamp(Integer.MAX_VALUE, 50)` → `Integer.MAX_VALUE`
- `addExactOrClamp(Integer.MIN_VALUE, -1)` → `Integer.MIN_VALUE`
- `toUnsigned32(-1)` → `4294967295L`
- `toUnsignedByte((byte) 200)` → `200`
- `shiftUppercaseAscii('Z', 2)` → `'B'`
- `shiftUppercaseAscii('A', -1)` → `'Z'`
- `classifyTemperatureBand(25.0)` → `"NORMAL (298.15 K)"`

### Ví dụ `SensorCliFormatter`
Input qua `Scanner`:
```text
7
Server Room A
24.5
true
```
Output từ `readAndFormatReading(scanner)`:
```text
[#0007] Server Room A      |  24.50 C | ONLINE
```

### Danh sách Edge Cases bắt buộc kiểm tra
1. Tràn số `int` cả chiều dương (`Integer.MAX_VALUE + 1`) và chiều âm (`Integer.MIN_VALUE - 1`).
2. `byte` âm (`-1`, `-56` tức `(byte) 200`, `-128`).
3. `clampToShort` vượt quá `32767` và nhỏ hơn `-32768`.
4. `Double.NaN` / `Double.POSITIVE_INFINITY` và nhiệt độ dưới độ 0 tuyệt đối (`-273.16`).
5. `shiftUppercaseAscii` với `offset` âm lớn hơn 26 (ví dụ `-27`, `-53`) và ký tự ngoài `'A'..'Z'`.
6. `Scanner` đọc chuỗi có khoảng trắng ngay sau `nextInt()` mà không bị trôi dòng; khoảng trắng thừa hai đầu `location`.
7. Format số thập phân luôn dùng dấu chấm `.` (không phụ thuộc Locale hệ điều hành).

---

## 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/01-primitives-and-syntax
```

### Biên dịch và chạy bộ test tự động (`Lab01Test`)
- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab01.Lab01Test
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab01.Lab01Test
  ```

### Chạy thử CLI tương tác (tùy chọn sau khi làm xong)
```bash
java -cp out com.javalabs.lab01.SensorCliFormatter
```
