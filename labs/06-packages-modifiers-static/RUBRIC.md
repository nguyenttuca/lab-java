# RUBRIC — Lab 06: Package, Access Modifiers & `static` vs Instance

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Tính đúng đắn chức năng** | **40** | Toàn bộ method trong `SamePackageAuditor`, `ExternalSubAccount`, `AppConfig` chạy đúng; `Lab06Test` đạt 100% PASS; không sửa `AccountBase.java`. |
| **2. Xử lý Edge Cases & Validation** | **25** | Kiểm tra `null`/blank ở mọi tham số đầu vào; validate `serviceName`/`customEnv`/`newEnv`; bảo vệ `INIT_ORDER_LOG` không bị lộ mutable list ra ngoài (`List.copyOf`). |
| **3. Hiểu đúng Visibility & `static` Lifecycle** | **25** | Truy cập đúng phạm vi của `protected` và package-private trong cùng/khác package; giữ đúng thứ tự khởi tạo `static` block → instance block → constructor; phân tách đúng biến `static` vs instance field (`instanceId`, `environment`). |
| **4. Clean Code & Conventions** | **10** | Biên dịch sạch 0 warning với `-Xlint:all`, không còn `// TODO`, không sửa visibility trong `AccountBase` để lách luật, giữ nguyên `package` chuẩn theo thư mục. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...` (bao gồm lỗi sai `package` hoặc cố tình truy cập `otherBase.protectedRegionTag` / `packageInternalTier` từ package khác).
- Tự ý chỉnh sửa `AccountBase.java` (ví dụ đổi `private`/`package-private` thành `public`) để truy cập được từ bên ngoài.
- Còn sót `// TODO` hoặc hardcode kết quả test.
- Khai báo `environment` hoặc `instanceId` thành `static` làm các instance đè dữ liệu lên nhau, hoặc ngược lại để `totalInstancesCreated` thành non-static khiến mọi instance có ID giống nhau (`1`).
- Không tách đúng `static` block vs instance block làm sai thứ tự trong `INIT_ORDER_LOG`.

### `[Major]`
- Trả về trực tiếp reference `INIT_ORDER_LOG` trong `getInitOrderTrace()` thay vì `List.copyOf(INIT_ORDER_LOG)` (lộ internal mutable state).
- Dùng `String.format` / `Locale` không nhất quán hoặc quên `.strip()` / `.toUpperCase(Locale.ROOT)`.
- Thiếu validate `null` / blank cho `account`, `peer`, `otherBase`, `newTier`, `newRegion`, `serviceName`, `newEnv`.
- Xuất hiện compiler warning khi biên dịch với `-Xlint:all`.

### `[Minor]`
- `SamePackageAuditor` đi vòng qua getter thay vì truy cập trực tiếp field cùng package (khi mục đích bài là kiểm chứng truy cập package-private/protected cùng package).
- Tên biến cục bộ thiếu rõ ràng hoặc còn import thừa.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 06

1. **Nghĩ rằng không ghi access modifier là `private` (thói quen C++) hoặc `public` (thói quen JS):** Thực chất là *package-private*.
2. **Truy cập `otherBase.protectedField` qua tham chiếu lớp cha ở khác package:** Vi phạm JLS §6.6.2 (khác package chỉ truy cập `protected` qua tham chiếu kiểu lớp con `this` / `ExternalSubAccount`).
3. **Biến thuộc tính riêng của object (`instanceId`, `serviceName`) thành `static`:** Mọi object bị dùng chung 1 giá trị và ghi đè nhau.
4. **Cố dùng `this` trong phương thức `static`:** Gây lỗi biên dịch `non-static variable this cannot be referenced from a static context`.
5. **Để lộ `static` hoặc instance `List` nội bộ trực tiếp qua getter:** Caller có thể `.clear()` hoặc `.add()` làm hỏng state.
6. **Sửa tên thư mục mà quên sửa dòng `package ...;` đầu file.**
