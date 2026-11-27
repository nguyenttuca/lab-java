# Lab 05 — Mảng (Arrays) & Collections nhập môn

- **Giai đoạn:** 1 — Nền tảng Java & Cú pháp
- **Hình thức:** Starter Code (`// TODO`)
- **Độ khó:** Trung bình
- **Thời gian dự kiến:** 75–90 phút
- **Phiên bản chuẩn:** Java 21 LTS

---

## 1. Mục tiêu học tập (Learning Objectives)

Sau khi hoàn thành bài Lab này, bạn sẽ nắm vững:
- Thao tác chuẩn trên **Mảng 1D/2D** với `java.util.Arrays` (`copyOf`, `fill`, `sort`, `toString`, `deepToString`, `deepEquals`).
- Làm quen 3 cấu trúc dữ liệu nền tảng của Java Collections Framework: **`List` (`ArrayList`)**, **`Set` (`HashSet`)**, **`Map` (`HashMap`)**.
- Phân biệt rõ 3 dạng `List` phổ biến: **`List.of(...)` / `List.copyOf(...)`** (bất biến, cấm `null`), **`Arrays.asList(...)`** (kích thước cố định, cho phép `.set(...)`, ánh xạ trực tiếp mảng gốc), và **`new ArrayList<>(...)`** (linh hoạt, thêm/xóa tự do).
- Tránh các bẫy kinh điển: Generics không nhận primitive (`List<Integer>` thay cho `List<int>`), **`ConcurrentModificationException`** khi xóa trong `for-each`, và nhầm lẫn giữa **`List<Integer>.remove(int index)`** với **`remove(Object)`**.
- Sắp xếp tùy chỉnh đa tiêu chí bằng **`Comparator`** và **Lambda expression** cơ bản.

---

## 2. Góc nhìn cho dev C++ / JS

- **Mảng Java cố định kích thước như `std::array`, không tự phình như JS `Array` hay `std::vector`:** Tạo xong `new int[5]` thì không thể `.push()` thêm. Muốn danh sách co giãn động, hãy dùng `ArrayList<T>`. Ngoài ra, chú ý phân biệt `arr.length` (thuộc tính của mảng), `str.length()` (method của `String`), và `list.size()` (method của Collection).
- **Generics Java KHÔNG chấp nhận primitive:** Bạn không thể viết `List<int>` như `std::vector<int>` trong C++ (lỗi compile ngay!). Phải dùng Wrapper Class (`List<Integer>`, `Map<String, Integer>`).
- **3 bẫy List mà dev JS dễ ngã nhất:**
  1. `List.of("a", "b")` trả về danh sách **bất biến** và **cấm phần tử `null`**: gọi `.add()`, `.set()` hay truyền `null` đều ném exception ngay.
  2. `Arrays.asList("a", "b")` trả về view **cố định kích thước** gắn với mảng nền: gọi `.set(0, "x")` thì được (và đổi luôn mảng nền!), nhưng gọi `.add()` hoặc `.remove()` ném `UnsupportedOperationException`.
  3. Với `List<Integer>`, gọi `list.remove(1)` sẽ xóa **theo chỉ số (index 1)** chứ không xóa số `1`! Muốn xóa phần tử có giá trị `1`, bắt buộc boxing rõ ràng: `list.remove(Integer.valueOf(1))`.
- **Xóa khi đang duyệt (`ConcurrentModificationException`):** Khác với C++ phải cập nhật iterator `it = vec.erase(it)` hay JS tự lọc bằng `.filter()`, nếu bạn gọi `list.remove(...)` bên trong vòng `for (String s : list)` thì JVM sẽ ném `ConcurrentModificationException` ngay lập tức. Hãy dùng `Iterator.remove()` hoặc `list.removeIf(...)`.
- **`HashSet` và `HashMap` không đảm bảo thứ tự duyệt:** Đừng bao giờ viết logic hay test phụ thuộc vào thứ tự lặp của `HashSet`/`HashMap` (tương đương `std::unordered_set`/`std::unordered_map` trong C++).

---

## 3. Đề bài chi tiết

Hãy hoàn thiện các phương thức được đánh dấu `// TODO` trong 2 file thuộc package `com.javalabs.lab05`:

### Phần A: `ArrayToolkit.java`

1. `sortedCopyWithoutMutatingOriginal(int[] source)` → `int[]`
   - Nếu `source == null`, ném `IllegalArgumentException`.
   - Sao chép mảng bằng `Arrays.copyOf`, sắp xếp tăng dần bản sao bằng `Arrays.sort` và trả về bản sao (mảng `source` gốc phải giữ nguyên).
2. `formatArray2D(int[][] matrix)` → `String`
   - Nếu `matrix == null`, ném `IllegalArgumentException`.
   - Trả về biểu diễn chuỗi đầy đủ của mảng 2D bằng `Arrays.deepToString`.
3. `transposeRectangularMatrix(int[][] matrix)` → `int[][]`
   - Nếu `matrix == null`, `matrix.length == 0`, có hàng `null`, hàng rỗng, hoặc các hàng không cùng độ dài (không phải hình chữ nhật) → ném `IllegalArgumentException`.
   - Trả về ma trận chuyển vị `[cols][rows]`.
4. `resizeAndPad(int[] source, int newLength, int padValue)` → `int[]`
   - Nếu `source == null` hoặc `newLength < 0`, ném `IllegalArgumentException`.
   - Dùng `Arrays.copyOf(source, newLength)`; nếu `newLength > source.length`, điền `padValue` vào phần mở rộng bằng `Arrays.fill`.
5. `areMatricesDeeplyEqual(int[][] a, int[][] b)` → `boolean`
   - So sánh sâu hai mảng 2D bằng `Arrays.deepEquals(a, b)` (hỗ trợ cả `null`).

### Phần B: `CollectionAnalytics.java`

1. `createMutableUpperCaseCopy(List<String> source)` → `List<String>`
   - Nếu `source == null`, ném `IllegalArgumentException`.
   - Tạo một `ArrayList<String>` mới (thêm/xóa tự do kể cả khi `source` là `List.of(...)` hay `Arrays.asList(...)`), bỏ qua phần tử `null`/blank, các phần tử hợp lệ được `.strip().toUpperCase(Locale.ROOT)`.
2. `createFixedSizeBackedListDemo(String[] initialItems)` → `List<String>`
   - Nếu `initialItems == null || initialItems.length == 0`, ném `IllegalArgumentException`.
   - Tạo bản sao mảng `String[] copy = Arrays.copyOf(initialItems, initialItems.length)`, bọc bằng `Arrays.asList(copy)`, sau đó dùng `.set(0, ...)` đổi phần tử đầu sang chữ hoa (`Locale.ROOT`) nếu khác `null` và trả về view `List<String>` đó. (Kết quả trả về phải cho phép `.set(...)` nhưng ném `UnsupportedOperationException` khi `.add(...)`).
3. `createUnmodifiableSnapshot(List<String> source)` → `List<String>`
   - Nếu `source == null` hoặc chứa phần tử `null`, ném `IllegalArgumentException`.
   - Trả về bản sao bất biến bằng `List.copyOf(source)` (mọi thao tác `.add()`, `.set()`, `.remove()` đều ném `UnsupportedOperationException`).
4. `removeAllShortStringsInPlace(List<String> words, int minLength)` → `int`
   - Nếu `words == null || minLength < 0`, ném `IllegalArgumentException`.
   - Xóa tại chỗ mọi phần tử `null` hoặc có độ dài sau `.strip()` nhỏ hơn `minLength` bằng **`Iterator` hoặc `removeIf`** (tránh `ConcurrentModificationException`). Trả về **số phần tử đã xóa**.
5. `removeIntegerValueVsIndex(List<Integer> numbers, int valueToRemove, int indexToRemove)` → `void`
   - Nếu `numbers == null || indexToRemove < 0 || indexToRemove >= numbers.size()`, ném `IllegalArgumentException`.
   - Thực hiện tuần tự 2 bước trên `numbers`:
     1. Xóa phần tử tại **vị trí `indexToRemove`** (`numbers.remove(indexToRemove)`).
     2. Sau đó xóa **lần xuất hiện đầu tiên có giá trị `valueToRemove`** (bắt buộc boxing `Integer.valueOf(valueToRemove)` để gọi đúng `remove(Object)` thay vì `remove(int index)`).
6. `findUniqueCommonTags(List<String> groupA, List<String> groupB)` → `Set<String>`
   - Nếu `groupA == null || groupB == null`, ném `IllegalArgumentException`.
   - Chuẩn hóa các tag (bỏ `null`/blank, `.strip().toLowerCase(Locale.ROOT)`), dùng `HashSet<String>` tìm giao hai tập hợp và trả về `Set<String>`.
7. `countWordFrequencies(List<String> lines)` → `Map<String, Integer>`
   - Nếu `lines == null`, ném `IllegalArgumentException`.
   - Bỏ qua dòng `null`/blank, tách từ bằng `\\s+`, chuyển lowercase (`Locale.ROOT`), đếm tần suất bằng `HashMap<String, Integer>` (gợi ý `merge` hoặc `getOrDefault`).
8. `rankWordsByFrequencyDescThenAlpha(Map<String, Integer> frequencies, int limit)` → `List<String>`
   - Nếu `frequencies == null || limit < 0`, ném `IllegalArgumentException`.
   - Lọc các entry có key không `null`/blank và value `!= null && value > 0`.
   - Sắp xếp bằng **`Comparator` & Lambda** theo: tần suất **giảm dần**, nếu bằng nhau thì theo thứ tự **từ điển tăng dần (`compareTo`)**. Trả về tối đa `limit` từ đầu tiên.

---

## 4. Ví dụ Input / Output & Edge Cases bắt buộc

### Ví dụ minh họa
- `ArrayToolkit.transposeRectangularMatrix(new int[][]{{1, 2, 3}, {4, 5, 6}})` → `{{1, 4}, {2, 5}, {3, 6}}`
- `CollectionAnalytics.removeIntegerValueVsIndex(new ArrayList<>(List.of(10, 20, 30, 1)), 1, 1)` → danh sách còn `[10, 30]`
- `CollectionAnalytics.rankWordsByFrequencyDescThenAlpha(Map.of("java", 3, "code", 3, "oop", 1), 2)` → `["code", "java"]`

### Danh sách Edge Cases bắt buộc kiểm tra
1. Đảm bảo `sortedCopyWithoutMutatingOriginal` không làm đổi mảng `source` ban đầu.
2. Ma trận không hợp lệ trong `transposeRectangularMatrix`: `null`, `{}`, `{null}`, `{{}}`, ma trận jagged `{{1, 2}, {3}}`.
3. `List.of` truyền vào `createMutableUpperCaseCopy` vẫn trả về `ArrayList` sửa được bình thường.
4. `createUnmodifiableSnapshot` nhận list chứa `null` phải ném `IllegalArgumentException` (tránh NPE bất ngờ từ `List.copyOf`).
5. Xóa nhiều phần tử liên tiếp trong `removeAllShortStringsInPlace` không bị sót phần tử hoặc văng `ConcurrentModificationException`.
6. Phân biệt chuẩn xác `remove(index)` vs `remove(Integer.valueOf(value))` khi giá trị cần xóa nằm trong khoảng index hợp lệ (ví dụ xóa số `1` trong list 3 phần tử).
7. Kết quả `HashSet` và `HashMap` không phụ thuộc thứ tự duyệt.

---

## 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/05-arrays-and-collections
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab05.Lab05Test
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab05.Lab05Test
  ```
