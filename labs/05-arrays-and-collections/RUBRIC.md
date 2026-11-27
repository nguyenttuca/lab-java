# RUBRIC — Lab 05: Mảng (Arrays) & Collections nhập môn

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Tính đúng đắn chức năng** | **40** | Toàn bộ 5 method trong `ArrayToolkit` và 8 method trong `CollectionAnalytics` chạy đúng; `Lab05Test` đạt 100% PASS; không sửa signature starter code. |
| **2. Xử lý Edge Cases & Validation** | **25** | Xử lý chuẩn mảng jagged / hàng `null` / ma trận rỗng; xử lý phần tử `null`/blank trong `List`/`Map`/`Set`; `limit = 0`, `newLength < source.length`; `source` chứa `null` ở `createUnmodifiableSnapshot`. |
| **3. Java Idioms & Collections Mastery** | **25** | Dùng chuẩn `Arrays.copyOf`, `Arrays.fill`, `Arrays.deepToString`, `Arrays.deepEquals`; phân biệt đúng `List.copyOf` vs `Arrays.asList` vs `ArrayList`; xóa an toàn bằng `Iterator`/`removeIf`; gọi đúng `remove(Integer.valueOf(...))`; dùng `Comparator` + Lambda rõ ràng. |
| **4. Clean Code & Conventions** | **10** | Không dùng raw types (`List`, `Map`, `Set` không có `<...>`), không còn `// TODO`, không giả định thứ tự `HashMap`/`HashSet`, dùng `Locale.ROOT`. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Còn sót `// TODO` hoặc hardcode kết quả test.
- Xóa phần tử bằng `list.remove(...)` trực tiếp bên trong vòng `for-each` gây `ConcurrentModificationException`.
- Nhầm lẫn `numbers.remove(valueToRemove)` (gọi overload `remove(int index)`) thay vì `numbers.remove(Integer.valueOf(valueToRemove))`.
- Làm biến đổi mảng gốc `source` trong `sortedCopyWithoutMutatingOriginal`.
- Trả về list có thể `.add()` được ở `createUnmodifiableSnapshot`, hoặc list không cho `.set()` ở `createFixedSizeBackedListDemo`, hoặc list không thể `.add()` ở `createMutableUpperCaseCopy`.
- Dùng `Arrays.toString(matrix)` / `Arrays.equals(a, b)` cho mảng 2D thay vì `deepToString` / `deepEquals`.

### `[Major]`
- Dùng Raw Types (`List list = new ArrayList();`) hoặc gây compiler warning `-Xlint:unchecked`.
- Thiếu kiểm tra ma trận jagged / hàng `null` trong `transposeRectangularMatrix`.
- Thiếu kiểm tra phần tử `null` hoặc value `<= 0` trong `rankWordsByFrequencyDescThenAlpha`.
- Sắp xếp thủ công bằng vòng lặp lồng nhau (bubble sort...) thay vì dùng `List.sort` / `Comparator` + Lambda theo yêu cầu.
- Quên `Locale.ROOT` khi chuẩn hóa chữ hoa/thường.

### `[Minor]`
- Tạo thừa `Set`/`List` trung gian không cần thiết hoặc viết `map.containsKey` + `map.get` dài dòng thay cho `getOrDefault` / `merge`.
- Import dư thừa hoặc format thiếu đồng nhất.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 05

1. **Xóa phần tử trong `for (T item : list)`:** Ném `ConcurrentModificationException` ngay lập tức.
2. **`list.remove(intVal)` trên `List<Integer>`:** Gọi nhầm `remove(int index)` thay vì `remove(Object)`!
3. **`Arrays.toString(matrix2D)` / `matrixA.equals(matrixB)`:** In ra địa chỉ bộ nhớ `[[I@...` hoặc so sánh tham chiếu thay vì dùng `deepToString` / `deepEquals`.
4. **Dùng Raw Types (`new ArrayList()`, `new HashMap()`):** Mất an toàn kiểu tĩnh và dính cảnh báo `unchecked`.
5. **Quên rằng `List.of(...)` và `List.copyOf(...)` cấm `null` và bất biến:** Gọi `.add()` hoặc truyền `null` sẽ văng exception.
6. **Quên `Arrays.copyOf` trước khi `Arrays.sort` hoặc `Arrays.asList`:** Làm thay đổi dữ liệu mảng gốc của người gọi ngoài ý muốn.
7. **Phụ thuộc vào thứ tự lặp của `HashMap` / `HashSet`:** Kết quả không tất định (non-deterministic).
