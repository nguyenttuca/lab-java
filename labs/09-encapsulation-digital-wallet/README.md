# Lab 09 — Encapsulation & Domain Validation (`[FIN-101]`)

- **Giai đoạn:** 2 — OOP & Mô phỏng công việc thực tế
- **Hình thức:** **From Scratch** (Đọc Technical Spec tự thiết kế toàn bộ mã nguồn trong `src/`)
- **Độ khó:** Trung bình → Khá
- **Thời gian dự kiến:** 2–3 giờ
- **Phiên bản chuẩn:** Java 21 LTS

---

## Jira Ticket: `[FIN-101]` Xây dựng module lõi Ví điện tử cá nhân (`DigitalWallet`)

### 1. Bối cảnh nghiệp vụ (Business Context)
Module ví điện tử phiên bản cũ của công ty từng xảy ra nhiều sự cố dữ liệu nghiêm trọng trên production:
- Code ở tầng UI gọi nhầm setter sửa trực tiếp số dư mà không ghi lịch sử giao dịch.
- Một service khác gọi `wallet.getTransactions().clear()` làm mất sạch nhật ký giao dịch lưu trong bộ nhớ.
- Việc dùng kiểu `double` khiến các giao dịch nhỏ cộng dồn bị lệch xu.

Bạn được giao thiết kế lại **module lõi Ví điện tử (`DigitalWallet`)** từ đầu với nguyên tắc đóng gói chặt chẽ (**Encapsulation**), bảo vệ tính bất biến dữ liệu (**Immutability & Defensive Copying**) và hệ thống **Custom Domain Exceptions** rõ ràng.

---

### 2. User Stories & Acceptance Criteria

#### Story 1 — Khởi tạo Ví & Quản lý Invariants
> *Là hệ thống tài chính, tôi muốn mỗi ví khởi tạo ra luôn ở trạng thái hợp lệ để không bao giờ tồn tại ví có số dư âm hoặc định danh rỗng.*
- **AC-1.1:** Mỗi ví có `walletId` (không `null`/blank, chuẩn hóa trim), `ownerName` (không `null`/blank), `balance` (`>= 0`, mặc định `0.00`), `dailyLimit` (`> 0`), và `status` (`ACTIVE`, `FROZEN`, `CLOSED`, mặc định khi tạo là `ACTIVE`).
- **AC-1.2:** Toàn bộ giá trị tiền tệ dùng `BigDecimal`, chuẩn hóa `scale = 2` với `RoundingMode.HALF_UP`. Tuyệt đối không có các setter trần như `setBalance(...)` hay `setTransactions(...)`.

#### Story 2 — Nạp tiền (`deposit`)
> *Là chủ ví, tôi muốn nạp tiền vào ví đang hoạt động và lưu vết lịch sử giao dịch.*
- **AC-2.1:** Chỉ cho phép nạp khi ví ở trạng thái `ACTIVE` và `amount > 0`.
- **AC-2.2:** Khi thành công, số dư tăng thêm `amount` và ghi nhận 1 bản ghi giao dịch (`DEPOSIT`) bất biến chứa mã giao dịch, loại giao dịch, số tiền, số dư sau giao dịch và thời điểm/ngày giao dịch.

#### Story 3 — Rút tiền & Kiểm soát hạn mức ngày (`withdraw`)
> *Là bộ phận quản trị rủi ro, tôi muốn tổng tiền rút/chi trong cùng một ngày không vượt quá `dailyLimit` của ví.*
- **AC-3.1:** Hỗ trợ truyền ngày giao dịch (`LocalDate`) hoặc dùng mặc định ngày hiện tại (`LocalDate.now()`) để có thể kiểm thử kịch bản sang ngày mới dễ dàng.
- **AC-3.2:** Từ chối nếu ví không `ACTIVE`, `amount <= 0`, số dư không đủ (`balance < amount`), hoặc tổng số tiền đã rút/chuyển đi trong đúng ngày đó cộng thêm `amount` vượt quá `dailyLimit`.
- **AC-3.3:** Khi sang ngày mới (`LocalDate` khác), hạn mức chi trong ngày được tính lại từ đầu (`0.00`).

#### Story 4 — Chuyển tiền nguyên tử giữa hai ví (`transferTo`)
> *Là chủ ví, tôi muốn chuyển tiền sang ví khác một cách an toàn; nếu ví nhận gặp sự cố thì ví gửi không bị mất tiền.*
- **AC-4.1:** Chặn chuyển tiền tới ví `null` hoặc chuyển cho chính mình (cùng `walletId` / cùng instance).
- **AC-4.2:** Đảm bảo **tính nguyên tử (Atomicity)**: nếu ví đích đang `FROZEN` hoặc `CLOSED` (hoặc bước nhận tiền thất bại), số dư, hạn mức ngày đã dùng và lịch sử giao dịch của ví nguồn **không được thay đổi**.

#### Story 5 — Đóng gói trạng thái & Defensive Copying
> *Là lập trình viên tích hợp, tôi chỉ có thể đọc bản sao bất biến của lịch sử giao dịch và không thể làm sai lệch dữ liệu nội bộ của ví từ bên ngoài.*
- **AC-5.1:** Hỗ trợ `freeze(String reason)`, `unfreeze()`, và `close()`. Ví đã `CLOSED` không thể `unfreeze()` hay thực hiện bất kỳ giao dịch nào nữa.
- **AC-5.2:** Danh sách trả về từ `getTransactions()` / `getTransactionsOn(LocalDate)` phải là bản sao hoặc unmodifiable view (`List.copyOf(...)` / `Collections.unmodifiableList(...)`); mọi nỗ lực `.add()`, `.remove()`, `.clear()` từ bên ngoài phải thất bại và không tác động vào ví.
- **AC-5.3:** Đối tượng giao dịch (`WalletTransaction`) phải bất biến (`record` hoặc `final` class chỉ có getter).

---

### 3. Ràng buộc kỹ thuật & Yêu cầu Entry Point bắt buộc

1. **Tự thiết kế cấu trúc package trong `src/`:** Không dùng default package. Gợi ý:
   - `com.javalabs.lab09.model` (`DigitalWallet`, `WalletStatus`, `WalletTransaction`, `TransactionType`)
   - `com.javalabs.lab09.exception` (`WalletException`, `InvalidAmountException`, `InsufficientFundsException`, `DailyLimitExceededException`, `WalletStateException`)
   - `com.javalabs.lab09.Main`
2. **Custom Domain Exceptions:** Định nghĩa rõ ít nhất 3–4 loại exception nghiệp vụ chuyên biệt (khuyến khích một base exception như `WalletException extends RuntimeException` kèm các lớp con cụ thể).
3. **Bắt buộc có Entry Point tự kiểm chứng (`Main.java`):**
   - Bạn **bắt buộc phải tạo** file entry point (ví dụ `src/com/javalabs/lab09/Main.java` với `public static void main(String[] args)`) tự động chạy minh họa và kiểm chứng **đầy đủ Acceptance Criteria & Edge Cases**.
   - Entry point phải chạy **tự động không treo chờ nhập `Scanner(System.in)`** và **không dùng từ khóa `assert`** (vì sẽ bị bỏ qua nếu thiếu `-ea`); hãy tự viết helper kiểm tra in rõ `[PASS]` / `[FAIL]` từng kịch bản và tổng kết cuối chương trình để AI chấm bài có thể chạy kiểm chứng tự động.
4. **Java 21 LTS & Không thư viện ngoài:** Chỉ dùng JDK 21 Standard Library.

---

### 4. Kịch bản Input / Output mẫu & Edge Cases bắt buộc

#### Ví dụ kịch bản minh họa trong `Main`
```text
[1] Tạo ví W-01 (balance=500.00, dailyLimit=300.00) & ví W-02 (balance=100.00, dailyLimit=500.00)
[2] W-01 nạp 200.00 -> balance = 700.00
[3] W-01 rút 250.00 ngày 2026-11-27 -> thành công, balance = 450.00 (đã dùng 250/300 trong ngày)
[4] W-01 rút tiếp 60.00 ngày 2026-11-27 -> Ném DailyLimitExceededException (balance vẫn = 450.00)
[5] W-01 rút 60.00 ngày 2026-11-28 -> thành công vì sang ngày mới, balance = 390.00
[6] Đóng băng W-02 -> W-01 chuyển 50.00 sang W-02 -> Ném WalletStateException; W-01 vẫn giữ nguyên 390.00!
[7] Gọi w1.getTransactions().clear() -> Ném UnsupportedOperationException; lịch sử W-01 vẫn nguyên vẹn.
```

#### Danh sách Edge Cases bắt buộc kiểm chứng
1. Khởi tạo với `walletId`/`ownerName` null hoặc blank, `initialBalance < 0`, `dailyLimit <= 0`.
2. Giao dịch với `amount` là `null`, `0.00`, `-10.00`.
3. Rút vượt số dư hiện có (`InsufficientFundsException`).
4. Rút cộng dồn trong cùng 1 ngày vượt `dailyLimit`, rồi kiểm tra sang ngày kế tiếp rút lại bình thường.
5. Chuyển tiền sang chính mình (`W-01` → `W-01` hoặc 2 object có cùng `walletId`) và chuyển sang ví `null`.
6. Chuyển tiền thất bại do ví đích `FROZEN`/`CLOSED` → xác minh ví nguồn không bị trừ tiền, không bị tính vào hạn mức ngày, không sinh giao dịch ma.
7. Thực hiện nạp/rút/freeze trên ví đã `CLOSED` (`WalletStateException`).
8. Tấn công Encapsulation từ bên ngoài: sửa `List` lấy từ `getTransactions()` hoặc thay đổi `BigDecimal` / `Transaction` field (phải hoàn toàn bất lực).

---

### 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/09-encapsulation-digital-wallet
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab09.Main
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab09.Main
  ```
*(Nếu bạn đặt entry point ở tên class/package khác, hãy thay `com.javalabs.lab09.Main` tương ứng và ghi chú rõ khi gọi AI chấm bài.)*
