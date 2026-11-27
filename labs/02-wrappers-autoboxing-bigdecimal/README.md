# Lab 02 — Wrapper Classes, Autoboxing & Độ chính xác số học

- **Giai đoạn:** 1 — Nền tảng Java & Cú pháp
- **Hình thức:** Starter Code (`// TODO`)
- **Độ khó:** Cơ bản → Trung bình
- **Thời gian dự kiến:** 60–90 phút
- **Phiên bản chuẩn:** Java 21 LTS

---

## 1. Mục tiêu học tập (Learning Objectives)

Sau khi hoàn thành bài Lab này, bạn sẽ nắm vững:
- Sự khác biệt giữa **Primitive Types** (`int`, `long`, `double`, `boolean`) và **Wrapper Classes** (`Integer`, `Long`, `Double`, `Boolean`).
- Cơ chế **Autoboxing / Unboxing** và các bẫy runtime kinh điển gây `NullPointerException` (NPE).
- Cơ chế **Integer Cache (`-128` đến `127`)** và lý do không bao giờ được dùng `==` để so sánh giá trị các đối tượng Wrapper.
- Lý do kiểu `double`/`float` (IEEE-754) gây sai số trong bài toán tài chính và cách dùng chuẩn **`BigDecimal`** (`String` constructor, `RoundingMode.HALF_UP`, `compareTo` vs `equals`).

---

## 2. Góc nhìn cho dev C++ / JS

- **Wrapper là Object nằm trên Heap, có thể `null`:** Khác với `int` hay `double` (luôn có giá trị, không thể `null`), `Integer` là một class tham chiếu. Khi bạn viết `int x = myInteger;` (Unboxing), JVM tự gọi `myInteger.intValue()`. Nếu `myInteger` là `null` → **nổ `NullPointerException` ngay lập tức**.
- **Bẫy Integer Cache (`-128..127`) với `==`:** Trong JS, `===` với số nguyên luôn so sánh giá trị vì `number` là primitive. Trong Java, toán tử `==` trên `Integer` lại **so sánh địa chỉ tham chiếu**. Nhờ bộ nhớ đệm `Integer.valueOf` cho đoạn `[-128, 127]`, `Integer a = 127; Integer b = 127; a == b` trả về `true`, nhưng với `128` thì `a == b` lại trả về `false`! Luôn dùng `.equals()` hoặc `Objects.equals()`.
- **Cẩn thận `Boolean.parseBoolean(...)`:** Trong JS `Boolean("yes")` là `true`, còn trong Java `Boolean.parseBoolean("yes")` âm thầm trả về `false` chứ **không** ném lỗi định dạng như `Integer.parseInt`.
- **Không dùng `double` cho tiền tệ:** Giống `number` của JS (`0.1 + 0.2 === 0.30000000000000004`), `double` trong Java dùng hệ nhị phân IEEE-754 nên không biểu diễn chính xác `0.1`. Trong Java, xử lý tiền tệ bắt buộc dùng `java.math.BigDecimal`.
- **2 bẫy kinh điển của `BigDecimal`:**
  1. `new BigDecimal(0.1)` vẫn mang theo sai số của `double` (`0.10000000000000000555...`). Phải dùng `new BigDecimal("0.1")` hoặc `BigDecimal.valueOf(0.1)`.
  2. `new BigDecimal("10.5").equals(new BigDecimal("10.50"))` trả về **`false`** vì `equals()` của `BigDecimal` so sánh cả `scale` (số chữ số sau dấu phẩy)! Để so sánh bằng giá trị số học, phải dùng `a.compareTo(b) == 0`.
  3. Phép chia `divide` không thể chia hết (ví dụ `10 / 3`) sẽ ném `ArithmeticException` nếu bạn không truyền `scale` và `RoundingMode`.

---

## 3. Đề bài chi tiết

Hãy hoàn thiện các phương thức được đánh dấu `// TODO` trong 2 file thuộc package `com.javalabs.lab02`:

### Phần A: `WrapperDiagnostics.java`

1. `safeUnboxOrDefault(Integer value, int defaultValue)` → `int`
   - Trả về giá trị `int` của `value`, hoặc `defaultValue` nếu `value == null` (không để xảy ra NPE).
2. `areWrapperValuesEqual(Integer a, Integer b)` → `boolean`
   - So sánh giá trị của 2 `Integer` an toàn với cả `null` và các số ngoài dải cache `-128..127` (ví dụ `1000` vs `1000` phải trả về `true`).
3. `isSameCachedReference(int value)` → `boolean`
   - Tạo 2 biến `Integer` qua `Integer.valueOf(value)` và kiểm tra bằng `==` để kiểm chứng trực tiếp dải Integer Cache của JVM (`-128..127` trả `true`, ngoài dải trả `false`). Không dùng `new Integer(...)` (đã deprecated từ Java 9).
4. `sumNonNullIntegers(Integer[] numbers)` → `int`
   - Cộng tất cả phần tử khác `null` trong mảng `numbers` (bỏ qua phần tử `null`, dùng `Math.addExact` để tránh tràn `int` âm thầm). Nếu `numbers == null`, ném `IllegalArgumentException`.
5. `safeAddLongs(Long a, Long b)` → `long`
   - Cộng 2 số `Long` an toàn: xem `null` như `0L`, dùng `Math.addExact` để phát hiện tràn `long` nếu có.
6. `safeMultiplyOrZero(Double a, Double b)` → `double`
   - Nếu `a == null` hoặc `b == null` → trả về `0.0`. Nếu một trong hai (khác `null`) là `NaN` hoặc `Infinite` → ném `IllegalArgumentException`. Ngược lại trả về `a * b`.
7. `parseIntOrNull(String raw)` → `Integer`
   - Nếu `raw == null` hoặc `raw.isBlank()` → trả về `null`. Trim khoảng trắng hai đầu rồi parse; nếu sai định dạng số nguyên (`NumberFormatException`) → trả về `null`.
8. `parseStrictBooleanOrNull(String raw)` → `Boolean`
   - Nếu `raw == null` → trả về `null`. Trim khoảng trắng và so sánh không phân biệt hoa thường: `"true"` → `Boolean.TRUE`, `"false"` → `Boolean.FALSE`, mọi chuỗi khác (`"yes"`, `"1"`, `""`) → `null`.

### Phần B: `CurrencyCalculator.java`

*(Mọi giá trị tiền tệ trả về chuẩn hóa về `scale = 2` và `RoundingMode.HALF_UP`.)*

1. `sumDecimalStrings(String... amounts)` → `BigDecimal`
   - Nếu `amounts == null` → ném `IllegalArgumentException`. Nếu `amounts.length == 0` → trả về `0.00`.
   - Khởi tạo từng phần tử bằng `new BigDecimal(s.strip())` (bắt `null`, blank hoặc `NumberFormatException` để ném `IllegalArgumentException`). Cộng dồn và chuẩn hóa kết quả về `scale = 2`, `RoundingMode.HALF_UP`.
2. `calculateLineTotalWithTax(String unitPrice, int quantity, String vatRate)` → `BigDecimal`
   - Validate `unitPrice`, `vatRate` là số thập phân hợp lệ và `>= 0`, `quantity >= 0` (sai ném `IllegalArgumentException`).
   - Công thức: `unitPrice * quantity * (1 + vatRate)`, làm tròn `scale = 2` theo `RoundingMode.HALF_UP`.
3. `applyPercentageDiscount(BigDecimal amount, BigDecimal discountPercent)` → `BigDecimal`
   - Validate không `null`, `amount >= 0`, `0 <= discountPercent <= 100` (sai ném `IllegalArgumentException`).
   - Công thức: `amount * (100 - discountPercent) / 100`, làm tròn `scale = 2` theo `RoundingMode.HALF_UP` (nhân trước rồi mới `divide` với `scale` và `RoundingMode` để tránh lỗi chia thập phân vô hạn).
4. `splitBillEvenly(BigDecimal totalAmount, int peopleCount)` → `BigDecimal[]`
   - Validate `totalAmount != null`, `totalAmount >= 0`, `peopleCount > 0`.
   - Chuẩn hóa `totalAmount` về `scale = 2` (`HALF_UP`), chia đều cho `peopleCount` sao cho:
     - Mỗi phần tử có `scale = 2`.
     - Chênh lệch giữa các phần tối đa `0.01`, phần dư cent lẻ phân bổ lần lượt cho những người đầu tiên (từ index `0`).
     - **Tổng tất cả phần tử trong mảng phải bằng chính xác `totalAmount` sau chuẩn hóa** (không mất xu lẻ).
5. `hasSameMonetaryValue(BigDecimal a, BigDecimal b)` → `boolean`
   - So sánh giá trị tiền tệ an toàn (ví dụ `10.5` và `10.50` trả `true`, hỗ trợ `null`). Tuyệt đối không dùng `==` hoặc `BigDecimal.equals()`.

---

## 4. Ví dụ Input / Output & Edge Cases bắt buộc

### Ví dụ minh họa
- `WrapperDiagnostics.isSameCachedReference(127)` → `true`, nhưng `isSameCachedReference(128)` → `false`
- `WrapperDiagnostics.areWrapperValuesEqual(Integer.valueOf(500), Integer.valueOf(500))` → `true`
- `CurrencyCalculator.sumDecimalStrings("0.1", "0.2")` → `0.30` (đúng `0.30`, không phải `0.30000000000000004`)
- `CurrencyCalculator.calculateLineTotalWithTax("19.99", 3, "0.10")` → `65.97`
- `CurrencyCalculator.splitBillEvenly(new BigDecimal("100.00"), 3)` → `[33.34, 33.33, 33.33]`
- `CurrencyCalculator.hasSameMonetaryValue(new BigDecimal("10.5"), new BigDecimal("10.50"))` → `true`

### Danh sách Edge Cases bắt buộc kiểm tra
1. Auto-unboxing phần tử `null` trong `Integer[]`, `Long`, `Double`, `Boolean` (kể cả trong phép tính và toán tử 3 ngôi).
2. Integer Cache tại đúng các biên `-129`, `-128`, `127`, `128`.
3. Chuỗi số có khoảng trắng `"  42  "`, chuỗi rỗng `""`, chuỗi rác `"12abc"`.
4. `parseStrictBooleanOrNull("yes")`, `"1"`, `"TRUE"`, `" false "` → phân biệt rõ với `Boolean.parseBoolean`.
5. `Double.NaN` / `Double.POSITIVE_INFINITY` kết hợp với `null` trong `safeMultiplyOrZero`.
6. Chia thập phân vô hạn trong `applyPercentageDiscount` và `splitBillEvenly` (ví dụ `100.00 / 3`, `10.00 / 6`, `0.02 / 5`).
7. So sánh `BigDecimal` khác `scale` (`"10.5"` vs `"10.50"`).
8. `discountPercent < 0` hoặc `> 100`, `peopleCount <= 0`, `totalAmount < 0`.

---

## 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/02-wrappers-autoboxing-bigdecimal
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab02.Lab02Test
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab02.Lab02Test
  ```
