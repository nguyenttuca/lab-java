# Lab 10 — Inheritance vs Composition (`[HR-204]`)

- **Giai đoạn:** 2 — OOP & Mô phỏng công việc thực tế
- **Hình thức:** **From Scratch** (Đọc Technical Spec tự thiết kế toàn bộ mã nguồn trong `src/`)
- **Độ khó:** Trung bình → Khá
- **Thời gian dự kiến:** 2.5–3.5 giờ
- **Phiên bản chuẩn:** Java 21 LTS

---

## Jira Ticket: `[HR-204]` Hệ thống Tính lương & Quản lý Nhân sự linh hoạt (`Payroll System`)

### 1. Bối cảnh nghiệp vụ (Business Context)
Hệ thống nhân sự cũ của công ty mắc phải thiết kế "bùng nổ cây kế thừa" (*Inheritance Explosion*): mọi tổ hợp chế độ lương, thưởng và thuế đều được đẻ thành một class con mới như `FullTimeFixedBonusTenPercentTaxEmployee`, `PartTimeNoBonusTaxExemptEmployee`... Mỗi khi phòng HR điều chỉnh chính sách thuế hay nâng gói thưởng cho một nhân viên, code buộc phải tạo lại object thuộc class khác.

Bạn được giao thiết kế lại hệ thống tính lương từ đầu sao cho:
- Áp dụng **Kế thừa (`extends`, `super`, `@Override`)** đúng cho bản chất hợp đồng lao động (*IS-A*).
- Áp dụng **Composition (*HAS-A*)** cho các chính sách thưởng và khấu trừ thuế để thay đổi linh hoạt tại runtime mà không làm phình cây kế thừa.

---

### 2. User Stories & Acceptance Criteria

#### Story 1 — Phân cấp Hợp đồng Nhân viên bằng Kế thừa (*IS-A*)
> *Là phòng Nhân sự, tôi muốn quản lý thống nhất mọi loại nhân viên dưới một mô hình chung nhưng mỗi loại hợp đồng tính thu nhập cơ bản theo quy tắc riêng.*
- **AC-1.1:** Lớp nền `Employee` lưu trữ thông tin chung (`employeeId`, `fullName`, `department`, `hireDate`) kèm validate chặt chẽ (`employeeId` không blank, `fullName` không blank, `hireDate != null`), chuẩn hóa `BigDecimal` về `scale = 2` (`RoundingMode.HALF_UP`).
- **AC-1.2:** Cài đặt ít nhất **3 lớp con** kế thừa `Employee` (dùng `extends`, gọi `super(...)`, ghi đè `@Override` đúng hợp đồng tính thu nhập cơ bản — Base Earnings):
  1. `FullTimeEmployee`: Lương tháng cố định (`monthlySalary > 0`) + tiền làm thêm giờ (`overtimeHours * overtimeRate`, với `overtimeHours >= 0`, `overtimeRate >= 0`).
  2. `PartTimeEmployee`: Tính theo số giờ làm thực tế (`hoursWorked * hourlyRate`, với `hoursWorked >= 0`, `hourlyRate > 0`).
  3. `Contractor`: Tính theo thù lao hợp đồng/dự án (`retainerFee >= 0`) cộng thưởng hoàn thành mốc (`milestoneCount * milestoneBonus`).
- **AC-1.3:** Tất cả các lớp con phải tuân thủ **Liskov Substitution Principle (LSP)**: mọi nơi nhận kiểu `Employee` đều làm việc chính xác với mọi lớp con mà không cần ép kiểu hay rẽ nhánh `instanceof`.

#### Story 2 — Tách Chính sách Thưởng & Thuế bằng Composition (*HAS-A*)
> *Là phòng Tài chính, tôi muốn gắn hoặc hoán đổi chính sách thưởng và chính sách thuế cho từng nhân viên tại runtime mà không cần tạo thêm lớp con kế thừa.*
- **AC-2.1:** Mỗi `Employee` sở hữu (*HAS-A*) một `BonusPolicy` và một `TaxPolicy` (được truyền vào lúc khởi tạo hoặc gán mặc định an toàn, và có thể thay đổi hợp lệ qua method như `updateBonusPolicy(...)` / `updateTaxPolicy(...)` với kiểm tra khác `null`).
- **AC-2.2:** Cài đặt tối thiểu:
  - **Nhóm `BonusPolicy`:**
    1. `NoBonusPolicy`: Thưởng `0.00`.
    2. `FixedBonusPolicy`: Thưởng một khoản cố định (`>= 0`).
    3. `PercentageBonusPolicy`: Thưởng theo `%` trên thu nhập cơ bản (`0 <= percent <= 100`).
  - **Nhóm `TaxPolicy`:**
    1. `NoTaxPolicy`: Thuế `0.00` (ví dụ áp dụng cho đối tượng miễn thuế hoặc contractor tự kê khai).
    2. `FlatTaxPolicy`: Thuế suất cố định (`0 <= rate <= 50%` trên Gross Pay).
    3. `ProgressiveTaxPolicy`: Thuế lũy tiến tối thiểu 2–3 bậc rõ ràng trên Gross Pay (ví dụ: phần `<= 1000.00` chịu `0%`, phần từ `1000.01` đến `3000.00` chịu `10%`, phần vượt trên `3000.00` chịu `20%`).

#### Story 3 — Quy trình Tính lương Thống nhất & `PayrollService`
> *Là kế toán trưởng, tôi muốn tính phiếu lương cho từng nhân viên và tổng hợp chi phí toàn công ty qua một luồng xử lý chung.*
- **AC-3.1:** Công thức chuẩn được điều phối thống nhất tại lớp cha `Employee` (hoặc helper template):
  - `basePay = calculateBasePay()` *(do từng subclass `@Override`)*
  - `bonus = bonusPolicy.calculateBonus(basePay)`
  - `grossPay = basePay + bonus`
  - `tax = taxPolicy.calculateTax(grossPay)` *(đảm bảo `0 <= tax <= grossPay`)*
  - `netPay = grossPay - tax`
- **AC-3.2:** `PayrollService` quản lý danh sách nhân viên (không cho trùng `employeeId`, từ chối `null`), tính tổng `Gross Pay` và tổng `Net Pay` toàn công ty hoặc theo từng `department`, và xuất phiếu lương (`Payslip` DTO/record) cho từng nhân viên.

---

### 3. Ràng buộc kỹ thuật & Yêu cầu Entry Point bắt buộc

1. **Tự thiết kế cấu trúc package trong `src/`:** Không dùng default package. Gợi ý:
   - `com.javalabs.lab10.model` (`Employee`, `FullTimeEmployee`, `PartTimeEmployee`, `Contractor`, `Payslip`)
   - `com.javalabs.lab10.policy` (`BonusPolicy`, `TaxPolicy` và các implementation)
   - `com.javalabs.lab10.service` (`PayrollService`)
   - `com.javalabs.lab10.Main`
2. **Phân biệt rõ Kế thừa vs Composition:**
   - KHÔNG tạo các class con kiểu `FullTimeWithBonusEmployee`.
   - `PayrollService` KHÔNG được dùng `if (emp instanceof FullTimeEmployee)` để tính lương!
   - Không lạm dụng `protected` mở field mutable ra ngoài; nếu `Employee` cho subclass dùng helper thì đặt hợp lý, giữ `private` fields và validate chặt.
3. **Bắt buộc có Entry Point tự kiểm chứng (`Main.java`):**
   - Bạn **bắt buộc phải tạo** file entry point (ví dụ `src/com/javalabs/lab10/Main.java` với `public static void main(String[] args)`) chạy kiểm chứng **đầy đủ Acceptance Criteria & Edge Cases**.
   - Chạy **tự động không treo chờ `Scanner(System.in)`**, không dùng từ khóa `assert` (tránh phụ thuộc `-ea`), tự viết helper kiểm tra in rõ `[PASS]` / `[FAIL]` từng kịch bản và tổng kết.
4. **Java 21 LTS & Không thư viện ngoài:** Chỉ dùng JDK 21 Standard Library và `BigDecimal` cho tiền tệ.

---

### 4. Kịch bản Input / Output mẫu & Edge Cases bắt buộc

#### Ví dụ kịch bản minh họa trong `Main`
```text
[1] FullTimeEmployee ("E01", salary=2500.00, OT=10h * 25.00) -> base = 2750.00
    + PercentageBonus(10%) -> bonus = 275.00 -> gross = 3025.00
    + ProgressiveTax (0% [0..1000], 10% [1000..3000], 20% [>3000])
      -> tax = 0 + 2000*10% + 25*20% = 205.00 -> net = 2820.00
[2] Đổi policy tại runtime trên chính instance E01 sang FixedBonus(500.00) & FlatTax(15%):
    -> gross = 2750.00 + 500.00 = 3250.00 -> tax = 487.50 -> net = 2762.50
[3] PartTimeEmployee ("E02", 80h * 15.00) + NoBonus + NoTax -> gross = 1200.00, net = 1200.00
[4] Contractor ("E03", retainer=1000.00, 2 milestones * 400.00) + NoBonus + FlatTax(10%) -> gross = 1800.00, net = 1620.00
[5] PayrollService tổng hợp công ty & lọc theo department -> tính tổng khớp hoàn toàn.
```

#### Danh sách Edge Cases bắt buộc kiểm chứng
1. Tham số âm hoặc `null` khi khởi tạo nhân viên (lương âm, giờ làm âm, `hireDate == null`, `employeeId` blank).
2. `PartTimeEmployee` làm `0` giờ (`basePay = 0.00`), hoặc `Contractor` có `0` milestone.
3. Gán `BonusPolicy` hoặc `TaxPolicy` là `null` (cả lúc khởi tạo lẫn khi `update...`) phải ném exception.
4. Chính sách thưởng/thuế ngoài ngưỡng hợp lệ (ví dụ phần trăm âm, thuế vượt `100%` hoặc `> 50%` theo quy định).
5. Kiểm tra điểm biên của thuế lũy tiến (tại đúng mức `1000.00`, `3000.00`, và `3000.01`).
6. `PayrollService` từ chối thêm nhân viên `null`, từ chối trùng `employeeId`, và xử lý an toàn danh sách/phòng ban rỗng (`totalNet = 0.00`).

---

### 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/10-inheritance-vs-composition
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab10.Main
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab10.Main
  ```
*(Nếu bạn đặt entry point ở tên class/package khác, hãy thay `com.javalabs.lab10.Main` tương ứng và ghi chú rõ khi gọi AI chấm bài.)*
