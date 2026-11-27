# Lab 07 — Exception Handling & File I/O cơ bản

- **Giai đoạn:** 1 — Nền tảng Java & Cú pháp
- **Hình thức:** Starter Code (`// TODO`)
- **Độ khó:** Trung bình
- **Thời gian dự kiến:** 75–90 phút
- **Phiên bản chuẩn:** Java 21 LTS

---

## 1. Mục tiêu học tập (Learning Objectives)

Sau khi hoàn thành bài Lab này, bạn sẽ nắm vững:
- Hệ thống phân cấp ngoại lệ của Java (`Throwable` → `Error` vs `Exception` → **`Checked Exception`** vs **`Unchecked / RuntimeException`**).
- Sử dụng thành thạo `try-catch-finally`, multi-catch (`catch (A | B e)`), `throw` / `throws` và **Exception Chaining** (giữ nguyên `cause` gốc).
- Cơ chế **`try-with-resources`** (`AutoCloseable`) đảm bảo giải phóng tài nguyên tự động kể cả khi có lỗi xảy ra.
- Đọc/ghi file văn bản hiện đại và an toàn bằng `java.nio.file.Path`, `java.nio.file.Files` (`newBufferedReader`, `writeString`, `createDirectories`, `StandardCharsets.UTF_8`).
- Tự định nghĩa một **Custom Checked Exception** (`CsvParseException`).

---

## 2. Góc nhìn cho dev C++ / JS

- **Checked Exception — khái niệm không có trong C++ và JS:** Trong C++ và JavaScript, mọi `throw` đều không bị compiler ép buộc phải khai báo ở chữ ký hàm. Trong Java, mọi lớp con của `Exception` (nhưng **không** kế thừa `RuntimeException`) như `IOException` hay `CsvParseException` đều là **Checked Exception**: hàm gọi nó bắt buộc phải `try-catch` xử lý hoặc khai báo `throws` lên tầng trên, nếu không sẽ **lỗi biên dịch ngay**.
- **Unchecked (`RuntimeException`) dùng cho lỗi lập trình / sai hợp đồng:** Ví dụ `IllegalArgumentException`, `NullPointerException`. Không nên lạm dụng `throws` với Unchecked Exception, và không dùng `catch (Exception e)` chung chung nuốt mất lỗi logic.
- **`try-with-resources` là "RAII" chuẩn của Java:** Trong C++, destructor của `std::ifstream` tự đóng file khi ra khỏi scope. Java dùng GC nên **không có destructor** chạy tức thời; nếu quên đóng `BufferedReader` sẽ rò rỉ file descriptor! Luôn viết `try (BufferedReader reader = Files.newBufferedReader(...)) { ... }` để JVM tự gọi `close()` (ngay cả khi khối `try` ném exception).
- **Tuyệt đối không "nuốt exception":** Thói quen viết `catch (Exception e) {}` (khối rỗng) hoặc chỉ `e.printStackTrace()` rồi cho chương trình chạy tiếp với dữ liệu sai là lỗi nghiêm trọng trong hệ thống thực tế.

---

## 3. Đề bài chi tiết

Lab 07 cung cấp sẵn file dữ liệu mẫu [data/transactions_sample.csv](data/transactions_sample.csv) chứa cả dòng hợp lệ lẫn các dòng lỗi định dạng (sai số cột, số không hợp lệ, loại giao dịch lạ, số tiền âm, dòng trống).

Hãy hoàn thiện các phần `// TODO` trong 2 file thuộc package `com.javalabs.lab07`:

### Phần A: `CsvParseException.java` (Custom Checked Exception)

- Kế thừa `Exception` (Checked Exception), giữ `private static final long serialVersionUID = 1L;`.
- Lưu thuộc tính `private final int lineNumber;`.
- Hoàn thiện 2 constructor:
  1. `CsvParseException(String message, int lineNumber)`
  2. `CsvParseException(String message, int lineNumber, Throwable cause)` (truyền `cause` lên `super(message, cause)` để không mất stack trace gốc).
- Hoàn thiện `getLineNumber()`.

### Phần B: `SafeFileProcessor.java`

*(Class lồng `ProcessingReport` đã được cài đặt hoàn chỉnh để đóng gói kết quả bất biến.)*

1. `parseSignedTransactionLine(String line, int lineNumber) throws CsvParseException` → `BigDecimal`
   - Nếu `line == null || line.isBlank()` hoặc `lineNumber < 1`, ném `IllegalArgumentException` (Unchecked).
   - Tách cột bằng `line.split(",", -1)` — kỳ vọng đúng **4 cột**: `tx_id,type,amount,currency`. Nếu khác 4 cột hoặc bất kỳ cột nào blank → ném `CsvParseException`.
   - `type` (`.strip().toUpperCase(Locale.ROOT)`) chỉ hợp lệ khi là `"CREDIT"` hoặc `"DEBIT"`. Nếu khác → ném `CsvParseException`.
   - Parse `amount` bằng `new BigDecimal(amountCol.strip())`; nếu sai định dạng (`NumberFormatException`), bắt và ném `new CsvParseException("...", lineNumber, e)` (**giữ cause `e`**). Số tiền phải `> 0` (`compareTo(BigDecimal.ZERO) > 0`), nếu `<= 0` → ném `CsvParseException`.
   - `currency` (`.strip().toUpperCase(Locale.ROOT)`) phải gồm đúng 3 chữ cái ASCII (`A-Z`, ví dụ `"USD"`, `"VND"`), nếu sai → ném `CsvParseException`.
   - Trả về `+amount` nếu `"CREDIT"`, `-amount` nếu `"DEBIT"`.
2. `processCsvLedger(Path csvPath) throws IOException` → `ProcessingReport`
   - Nếu `csvPath == null`, ném `IllegalArgumentException`.
   - Mở file bằng **`try-with-resources`**: `try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8))`.
   - Để `IOException` (ví dụ file không tồn tại) truyền ra ngoài qua `throws IOException`.
   - Đọc từng dòng từ `lineNumber = 1`:
     - Bỏ qua dòng đầu tiên (`lineNumber == 1`) nếu đó là dòng tiêu đề CSV (ví dụ bắt đầu bằng `"tx_id,"` không phân biệt hoa/thường).
     - Bỏ qua các dòng trắng (`line.isBlank()`).
     - Với từng dòng dữ liệu, bọc lời gọi `parseSignedTransactionLine` trong `try-catch (CsvParseException e)`:
       - Hợp lệ: cộng dồn vào `netTotal`, tăng `validCount`.
       - Lỗi `CsvParseException`: tăng `errorCount` và thêm `"Line " + e.getLineNumber() + ": " + e.getMessage()` vào danh sách lỗi.
   - Chuẩn hóa `netTotal` về `scale = 2, RoundingMode.HALF_UP` và trả về `new ProcessingReport(...)`.
3. `exportReport(Path outputPath, ProcessingReport report) throws IOException` → `void`
   - Nếu `outputPath == null || report == null`, ném `IllegalArgumentException`.
   - Nếu `outputPath.getParent() != null`, gọi `Files.createDirectories(outputPath.getParent())`.
   - Dùng `Files.writeString(outputPath, content, StandardCharsets.UTF_8)` (hoặc `BufferedWriter` trong `try-with-resources`) ghi báo cáo đúng định dạng:
     ```text
     VALID_ROWS=<validCount>
     ERROR_ROWS=<errorCount>
     NET_TOTAL=<netTotal.toPlainString()>
     ERRORS:
     - <errorMessage1>
     ```
     *(Nếu danh sách lỗi rỗng, ghi dòng `ERRORS: NONE` thay cho `ERRORS:` + danh sách).*
4. `useAutoCloseableSafely(AutoCloseable resource, Runnable task) throws Exception` → `void`
   - Nếu `resource == null || task == null`, ném `IllegalArgumentException`.
   - Bắt buộc dùng **`try-with-resources`** (`try (AutoCloseable managed = resource) { task.run(); }`) để chứng minh `resource.close()` luôn tự động được JVM gọi kể cả khi `task.run()` ném exception.

---

## 4. Ví dụ Input / Output & Edge Cases bắt buộc

### Dữ liệu mẫu (`data/transactions_sample.csv`)
```csv
tx_id,type,amount,currency
TX-1001,CREDIT,150.50,USD
TX-1002,DEBIT,40.25,USD

TX-1003,CREDIT,not_a_number,USD
TX-1004,REFUND,20.00,USD
TX-1005,CREDIT,-15.00,USD
TX-1006,DEBIT,10.00
TX-1007,CREDIT,89.75,USD
```
Kết quả xử lý trên file mẫu:
- `validCount = 3` (`TX-1001`, `TX-1002`, `TX-1007`)
- `errorCount = 4` (dòng `TX-1003`, `TX-1004`, `TX-1005`, `TX-1006`)
- `netTotal = 150.50 - 40.25 + 89.75 = 200.00`
- Dòng trống không tính là lỗi.

### Danh sách Edge Cases bắt buộc kiểm tra
1. File CSV không tồn tại → phải ném Checked `IOException` (`NoSuchFileException`), không nuốt thành report rỗng.
2. File CSV rỗng (0 dòng) hoặc chỉ có dòng tiêu đề → trả về `validCount=0, errorCount=0, netTotal=0.00`.
3. Dòng CSV có cột rỗng ở cuối (`"TX-1,CREDIT,10,"`) → `split(",", -1)` vẫn đếm đủ 4 phần tử để bắt cột `currency` bị blank!
4. `CsvParseException` khi `amount` không phải số phải giữ `getCause() instanceof NumberFormatException`.
5. `useAutoCloseableSafely` khi `task.run()` ném `RuntimeException` vẫn phải đóng `resource` và truyền nguyên `RuntimeException` ra ngoài.
6. `exportReport` ghi vào đường dẫn có thư mục cha chưa tồn tại phải tự tạo được thư mục.

---

## 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/07-exceptions-and-file-io
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab07.Lab07Test
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab07.Lab07Test
  ```
