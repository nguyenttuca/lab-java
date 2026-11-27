# Lab 03 — String Immutability, String Pool & Xử lý chuỗi

- **Giai đoạn:** 1 — Nền tảng Java & Cú pháp
- **Hình thức:** Starter Code (`// TODO`)
- **Độ khó:** Cơ bản → Trung bình
- **Thời gian dự kiến:** 60–90 phút
- **Phiên bản chuẩn:** Java 21 LTS

---

## 1. Mục tiêu học tập (Learning Objectives)

Sau khi hoàn thành bài Lab này, bạn sẽ nắm vững:
- Bản chất **bất biến (`Immutable`)** của `String` trong Java và cơ chế **String Constant Pool** trên Heap.
- Sự khác biệt cốt lõi giữa `==`, `.equals()`, `.equalsIgnoreCase()` và `Objects.equals()` (tránh `NullPointerException`).
- Khi nào cần dùng **`StringBuilder`** thay cho toán tử `+` để tránh chi phí bộ nhớ và thời gian $O(n^2)$ trong vòng lặp.
- Cú pháp **Text Blocks (`"""`)** kết hợp `.formatted(...)` để viết template chuỗi nhiều dòng sạch đẹp (JSON, SQL, báo cáo).
- Các API chuẩn hiện đại của `String`: `strip()` vs `trim()`, `isBlank()` vs `isEmpty()`, `split()`, `String.join()`.

---

## 2. Góc nhìn cho dev C++ / JS

- **`String` Java không sửa tại chỗ như `std::string` C++:** Trong C++, bạn có thể viết `s[0] = 'A';` hay `s += "x";` làm biến đổi chuỗi gốc. Trong Java (giống JS), `String` là **bất biến tuyệt đối** — mọi phương thức như `.strip()`, `.toLowerCase()`, `.replace()` đều **trả về một `String` mới** và giữ nguyên chuỗi cũ. Lỗi hay gặp là gọi `s.strip();` mà quên gán lại `s = s.strip();`.
- **Cẩn thận `==` khác hẳn `===` của JavaScript:** Trong JS, `"abc" === new String("abc")` với literal vẫn hay khiến dev quen viết `==` cho chuỗi. Trong Java, `==` trên `String` chỉ so sánh **địa chỉ tham chiếu**! Hai chuỗi literal giống nhau dùng chung ô nhớ trong **String Pool** (`"java" == "java"` là `true`), nhưng chuỗi tạo lúc runtime (`new String("java")`, đọc từ file/Scanner, cắt ghép động) nằm ở vùng Heap riêng nên `==` trả về **`false`**. Luôn dùng `.equals()` hoặc `Objects.equals()`.
- **`strip()` thông minh hơn `trim()`:** `trim()` là hàm cũ từ Java 1.0, chỉ xóa các ký tự có mã Unicode `<= \u0020` (ASCII control & space). `strip()` (Java 11+) xóa mọi khoảng trắng theo chuẩn `Character.isWhitespace(...)`, bao gồm cả khoảng trắng Unicode như `\u2003` (EM SPACE). Tương tự, hãy ưu tiên `isBlank()` thay cho `trim().isEmpty()`.
- **Nối chuỗi trong vòng lặp:** Trong C++ dùng `res += part` có thể tối ưu amortized $O(1)$ vì buffer mutable; trong Java, `res = res + part` trong vòng lặp `for` tạo ra vô số `String` rác $O(n^2)$ ký tự bị copy lại! Hãy dùng `StringBuilder`.

---

## 3. Đề bài chi tiết

Hãy hoàn thiện các phương thức được đánh dấu `// TODO` trong 2 file thuộc package `com.javalabs.lab03`:

### Phần A: `StringPoolLab.java`

1. `areContentEqual(String a, String b)` → `boolean`
   - So sánh nội dung 2 chuỗi an toàn với `null` (không dùng `a == b` để kiểm tra nội dung khi cả hai khác `null`).
2. `areEqualIgnoreCase(String a, String b)` → `boolean`
   - So sánh không phân biệt hoa/thường an toàn với `null` (`(null, null) -> true`, một trong hai `null -> false`).
3. `compareLiteralPoolVsHeapNew()` → `boolean`
   - Khai báo bên trong hàm:
     - Hai biến literal cùng giá trị `"java-21"` (ví dụ `s1`, `s2`).
     - Một biến tạo tường minh trên Heap: `new String("java-21")` (`s3`).
   - Trả về `true` nếu thỏa đồng thời: `(s1 == s2)` là `true`, `(s1 == s3)` là `false`, và `s1.equals(s3)` là `true`.
4. `isHeapCopySameReferenceAsPool(String text)` → `boolean`
   - Nếu `text == null`, ném `IllegalArgumentException`.
   - Tạo `String heapCopy = new String(text);` và trả về kết quả so sánh tham chiếu `text.intern() == heapCopy` (kỳ vọng `false`).
5. `isInternedSameReferenceAsPool(String text)` → `boolean`
   - Nếu `text == null`, ném `IllegalArgumentException`.
   - Tạo `String heapCopy = new String(text);` và trả về kết quả `text.intern() == heapCopy.intern()` (kỳ vọng `true`).
6. `applyTransformIgnoringReturnLeavesOriginal(String original)` → `String`
   - Nếu `original == null`, ném `IllegalArgumentException`.
   - Gọi `original.toUpperCase(Locale.ROOT);` **không gán kết quả**, rồi trả về `original` — nhằm chứng minh trực tiếp rằng `String` bất biến không thể bị thay đổi tại chỗ.

### Phần B: `TextProcessor.java`

1. `normalizeFullName(String rawName)` → `String`
   - Nếu `rawName == null || rawName.isBlank()`, ném `IllegalArgumentException`.
   - Dùng `.strip()` (xử lý được cả khoảng trắng Unicode như `\u2003`), tách từ theo cụm khoảng trắng `\\s+`, chuẩn hóa mỗi từ theo Title Case (ký tự đầu viết hoa, các ký tự sau viết thường theo `Locale.ROOT`), nối lại bằng đúng 1 dấu cách `" "`.
2. `repeatWithDelimiter(String token, int count, String delimiter)` → `String`
   - Nếu `token == null || delimiter == null || count < 0`, ném `IllegalArgumentException`.
   - Nếu `count == 0`, trả về `""`.
   - Bắt buộc dùng **`StringBuilder`** để ghép `token` lặp lại `count` lần ngăn cách bởi `delimiter` (ví dụ `("OK", 3, "-")` → `"OK-OK-OK"`).
3. `sanitizeAndJoinCsvTags(String rawCsv)` → `String`
   - Nếu `rawCsv == null`, ném `IllegalArgumentException`.
   - Tách theo dấu phẩy `,`, `.strip()` từng phần tử, loại bỏ phần tử blank, chuyển chữ thường (`Locale.ROOT`) và nối bằng `" | "` (dùng `String.join` hoặc `StringBuilder`). Nếu không có tag nào hợp lệ, trả về `""`.
4. `isValidUsername(String username)` → `boolean`
   - Trả về `true` nếu và chỉ nếu: không `null`, độ dài trong `[4, 20]`, ký tự đầu tiên là chữ cái ASCII (`a-z` hoặc `A-Z`), các ký tự còn lại chỉ gồm chữ cái ASCII, chữ số (`0-9`) hoặc dấu gạch dưới `_`.
5. `renderJsonPayload(String username, String role, int level)` → `String`
   - Nếu `username == null || username.isBlank() || role == null || role.isBlank() || level < 1`, ném `IllegalArgumentException`.
   - Bắt buộc dùng **Text Block (`"""`)** kết hợp `.formatted(...)` trả về chuỗi JSON 5 dòng (không thừa newline ở cuối):
     ```json
     {
       "username": "<username.strip()>",
       "role": "<role.strip()>",
       "level": <level>
     }
     ```
6. `maskEmail(String email)` → `String`
   - Validate `email`: không `null`/blank; sau khi `.strip()` không chứa khoảng trắng bên trong; chứa duy nhất 1 dấu `@`; phần local trước `@` dài `>= 1`; phần domain sau `@` có ít nhất 1 dấu `.` không nằm ở đầu hoặc cuối domain (và không có `..`). Nếu sai → ném `IllegalArgumentException`.
   - Quy tắc che phần local:
     - Độ dài local `== 1`: `"*" + "@" + domain`
     - Độ dài local `== 2`: `local.charAt(0) + "*@" + domain`
     - Độ dài local `>= 3`: `local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + domain`

---

## 4. Ví dụ Input / Output & Edge Cases bắt buộc

### Ví dụ minh họa
- `TextProcessor.normalizeFullName("\u2003  nGuYễN   vĂn   aN  ")` → `"Nguyễn Văn An"`
- `TextProcessor.repeatWithDelimiter("OK", 3, "-")` → `"OK-OK-OK"`
- `TextProcessor.sanitizeAndJoinCsvTags(" java, ,  SPRING-boot ,   ,oop  ")` → `"java | spring-boot | oop"`
- `TextProcessor.maskEmail("alice@dev.io")` → `"a***e@dev.io"`
- `TextProcessor.maskEmail("ab@dev.io")` → `"a*@dev.io"`

### Danh sách Edge Cases bắt buộc kiểm tra
1. Tham số `null` ở các hàm so sánh (`areContentEqual`, `areEqualIgnoreCase`, `isValidUsername`).
2. Khoảng trắng Unicode (`\u2003`) làm `trim()` thất bại nhưng `strip()` xử lý đúng.
3. Chuỗi lớn trong `repeatWithDelimiter` (`count = 20_000`) kiểm chứng dùng `StringBuilder` thay cho `+` trong loop.
4. `rawCsv` chỉ toàn dấu phẩy và khoảng trắng (`" , ,   "` → `""`).
5. Email sai định dạng: thiếu `@`, nhiều `@`, `@` ở đầu/cuối, domain không có `.`, domain bắt đầu/kết thúc bằng `.`, có dấu cách bên trong.
6. Text Block canh lề chính xác (không bị thụt lề dư hoặc lệch khoảng trắng 2 space của JSON).

---

## 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/03-strings-pool-and-text
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab03.Lab03Test
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab03.Lab03Test
  ```
