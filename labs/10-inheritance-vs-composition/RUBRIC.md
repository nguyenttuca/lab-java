# RUBRIC — Lab 10: Inheritance vs Composition (`[HR-204]`)

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Đáp ứng Acceptance Criteria** | **30** | Đủ 3 loại `Employee` (`FullTime`, `PartTime`, `Contractor`), các `BonusPolicy`/`TaxPolicy` (bao gồm thuế lũy tiến), đổi policy runtime, và `PayrollService` tổng hợp công ty/phòng ban. |
| **2. Xử lý Edge Cases & Validation** | **25** | Bao phủ đủ 6 nhóm Edge Cases (giá trị âm/`null`, `0` giờ/`0` milestone, `policy == null`, tỷ lệ ngoài ngưỡng, ranh giới thuế lũy tiến, trùng `employeeId`). |
| **3. Thiết kế Inheritance vs Composition** | **30** | Phân tách rạch ròi *IS-A* (`extends`, `super`, `@Override`) và *HAS-A* (Composition `BonusPolicy` + `TaxPolicy`); tuân thủ LSP; `PayrollService` xử lý đa hình không dùng `instanceof`; giữ `BigDecimal` chuẩn xác. |
| **4. Clean Code, Package & Entry Point** | **15** | Chia package sạch (`model`, `policy`, `service`), biên dịch sạch `--release 21`, có `Main.java` tự động kiểm chứng đủ kịch bản không treo stdin và không phụ thuộc `-ea`. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Thiếu entry point (`Main.java`), hoặc `Main` bị treo chờ `Scanner(System.in)`.
- Thiết kế sai bản chất bài toán: dùng kế thừa đa tầng cứng nhắc cho các tổ hợp thưởng/thuế (ví dụ `FullTimeFixedBonusEmployee`) thay vì Composition, hoặc không thể hoán đổi policy tại runtime.
- Dùng `instanceof` / `getClass()` / type-tag string trong `PayrollService` hoặc `Employee` để rẽ nhánh tính lương theo từng lớp con.
- Dùng `double` / `float` cho lương, thưởng, thuế.
- Phá vỡ LSP hoặc Encapsulation (lớp con sửa field cha tùy tiện vì để `protected` non-final bừa bãi, hoặc `netPay < 0`).

### `[Major]`
- Thiếu một trong 3 loại nhân viên hoặc thiếu thuế lũy tiến `ProgressiveTaxPolicy` (hoặc tính sai bậc lũy tiến — áp mức cao nhất lên toàn bộ thu nhập thay vì từng phần bậc!).
- Quên `@Override` khi ghi đè phương thức từ lớp cha `Employee`.
- Không kiểm tra trùng `employeeId` hoặc để lọt `null` trong `PayrollService`.
- Không bảo vệ danh sách nhân viên nội bộ của `PayrollService` (trả về mutable list trực tiếp).
- Dùng từ khóa `assert` trong `Main.java` để kiểm tra test.

### `[Minor]`
- Lặp logic tính toán `BigDecimal` / `setScale(2, RoundingMode.HALF_UP)` ở nhiều chỗ mà không gom chung.
- Cấu trúc package chưa rõ ràng hoặc còn compiler warning `-Xlint:all`.
- Thiếu `toString()` / `equals()` theo `employeeId` trên `Employee`.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 10

1. **Lạm dụng `extends` cho mọi biến thể hành vi:** Đẻ hàng loạt subclass cho từng combo thưởng + thuế thay vì dùng Composition.
2. **Dùng `if (e instanceof FullTimeEmployee)` trong `PayrollService`:** Đánh mất hoàn toàn giá trị của kế thừa và đa hình.
3. **Tính sai thuế lũy tiến:** Nhân cả `grossPay` với `20%` thay vì chỉ tính `20%` trên phần vượt ngưỡng bậc trên!
4. **Quên annotation `@Override`:** Nếu lỡ sai tên hoặc sai tham số method, Java âm thầm tạo method mới hoàn toàn mà không báo lỗi compile.
5. **Để field của lớp cha `Employee` là `protected` non-final:** Làm subclass tùy tiện thay đổi trực tiếp `grossPay`/`employeeId` phá vỡ invariants.
6. **Không kiểm tra `bonusPolicy == null` / `taxPolicy == null`:** Gây `NullPointerException` chậm (delayed NPE) khi gọi `calculateNetPay()`.
