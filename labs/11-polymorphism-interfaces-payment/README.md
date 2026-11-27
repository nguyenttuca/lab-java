# Lab 11 — Polymorphism, Abstract Class & Interface (`[PAY-305]`)

- **Giai đoạn:** 2 — OOP & Mô phỏng công việc thực tế
- **Hình thức:** **From Scratch** (Đọc Technical Spec tự thiết kế toàn bộ mã nguồn trong `src/`)
- **Độ khó:** Khá
- **Thời gian dự kiến:** 3–4 giờ
- **Phiên bản chuẩn:** Java 21 LTS

---

## Jira Ticket: `[PAY-305]` Xây dựng Cổng Thanh toán & Thông báo Đa kênh (`Payment Gateway`)

### 1. Bối cảnh nghiệp vụ (Business Context)
Hệ thống thanh toán hiện tại của công ty đang chứa một hàm `processPayment(String type, ...)` cồng kềnh dài hàng trăm dòng `if (type.equals("CARD")) ... else if (type.equals("EWALLET")) ... else if (type.equals("BANK")) ...`. Mỗi lần thêm cổng thanh toán mới hoặc thêm kênh gửi biên lai mới (Email, SMS, Webhook), lập trình viên buộc phải sửa thẳng vào code cốt lõi của dịch vụ Checkout, gây rủi ro regression rất cao.

Bạn được giao thiết kế lại **Cổng Thanh toán & Thông báo Đa kênh** từ đầu dựa trên **Polymorphism (Đa hình)**, **`interface`** (kèm `default`/`static` methods, tách biệt capability interface) và **`abstract class`** (chia sẻ quy trình thanh toán chuẩn).

---

### 2. User Stories & Acceptance Criteria

#### Story 1 — Hợp đồng hành vi & Tách biệt khả năng bằng `interface`
> *Là kiến trúc sư phần mềm, tôi muốn tách biệt hợp đồng thanh toán, khả năng hoàn tiền và kênh gửi biên lai thành các `interface` độc lập để không ép mọi lớp phải cài đặt những hàm mình không hỗ trợ.*
- **AC-1.1 (`PaymentMethod` interface):**
  - Định nghĩa hợp đồng chung: `String getChannelCode()`, `PaymentReceipt pay(PaymentRequest request)`.
  - Có ít nhất **1 `default` method** mang tính nghiệp vụ (ví dụ: `default BigDecimal calculateFee(BigDecimal amount)` mặc định trả về `0.00`, hoặc `default boolean supports(BigDecimal amount)`), và **1 `static` helper method** trên interface (ví dụ: `static BigDecimal normalizeMoney(BigDecimal value)`).
- **AC-1.2 (`Refundable` interface — Tách biệt khả năng hoàn tiền):**
  - Định nghĩa tách riêng khỏi `PaymentMethod`: `RefundReceipt refund(String originalTransactionId, BigDecimal amount, String reason)`.
  - Chỉ các phương thức hỗ trợ hoàn tiền tự động (ví dụ `CreditCardPayment`, `EWalletPayment`) mới `implements Refundable`. Phương thức như `BankTransferPayment` chỉ `implements PaymentMethod` (không cần ném `UnsupportedOperationException` cho một hàm bị ép implement).
- **AC-1.3 (`ReceiptNotifier` interface):**
  - Định nghĩa hợp đồng gửi thông báo biên lai: `String getNotifierName()`, `void notify(PaymentReceipt receipt)`.
  - Cài đặt ít nhất 2–3 kênh thông báo (ví dụ `EmailNotifier`, `SmsNotifier`, hoặc `AuditLogNotifier`) có thể kiểm chứng được danh sách thông báo đã gửi trong bộ nhớ.

#### Story 2 — Chia sẻ quy trình chuẩn qua `abstract class`
> *Là lập trình viên backend, tôi muốn phần kiểm tra đầu vào, sinh mã giao dịch và đóng gói biên lai được dùng chung ở lớp trừu tượng thay vì copy-paste giữa các cổng thanh toán.*
- **AC-2.1 (`AbstractPaymentProcessor implements PaymentMethod`):**
  - Cài đặt luồng thanh toán chuẩn (`pay(PaymentRequest request)`):
    1. Validate `request != null`, `orderId` không blank, `amount > 0`, `currency` hợp lệ.
    2. Gọi bước kiểm tra điều kiện riêng của kênh (hook/abstract method, ví dụ hạn mức tối đa của từng kênh hoặc kiểm tra thông tin tài khoản).
    3. Tính phí giao dịch (`calculateFee(amount)`).
    4. Gọi bước thực thi trừ tiền đặc thù của lớp con (`protected abstract String executeCharge(PaymentRequest request)` trả về mã tham chiếu kênh).
    5. Đóng gói và trả về đối tượng `PaymentReceipt` bất biến (`record`) gồm: `transactionId`, `orderId`, `channelCode`, `amount`, `fee`, `totalCharged`, `status`, `createdAt`.
- **AC-2.2 (Cài đặt tối thiểu 3 kênh thanh toán kế thừa `AbstractPaymentProcessor`):**
  1. `CreditCardPayment` (implements thêm `Refundable`): có tính phí % (ví dụ `2.0%` trên `amount`), giới hạn giao dịch tối đa (ví dụ `10000.00`), hỗ trợ hoàn tiền (không vượt số tiền đã thu).
  2. `EWalletPayment` (implements thêm `Refundable`): miễn phí dưới ngưỡng hoặc phí cố định nhỏ (ví dụ `1.00`), kiểm tra số dư ví hoặc mã tài khoản, hỗ trợ hoàn tiền.
  3. `BankTransferPayment` (KHÔNG implements `Refundable`): miễn phí giao dịch (`0.00` qua `default` method), kiểm tra số tài khoản ngân hàng, không hỗ trợ hoàn tiền tự động qua API.

#### Story 3 — Điều phối Đa hình trong `CheckoutService` (Zero `if-else` theo loại kênh)
> *Là hệ thống Checkout, tôi muốn xử lý thanh toán, gửi thông báo đa kênh và mở rộng thêm cổng thanh toán mới mà không cần sửa code bên trong `CheckoutService`.*
- **AC-3.1:** `CheckoutService` quản lý các `PaymentMethod` đã đăng ký (không cho đăng ký trùng `channelCode` hoặc `null`) và danh sách `ReceiptNotifier`.
- **AC-3.2:** `checkout(String channelCode, PaymentRequest request)` tra cứu `PaymentMethod` theo mã kênh và gọi đa hình `method.pay(request)`, sau đó phát biên lai tới tất cả `ReceiptNotifier` đã đăng ký.
- **AC-3.3:** `refund(String channelCode, String transactionId, BigDecimal amount, String reason)`:
  - Kiểm tra kênh có tồn tại không; kiểm tra giao dịch gốc có tồn tại & số tiền hoàn hợp lệ không (`0 < amount <= số tiền giao dịch gốc`, không cho hoàn vượt tổng đã thanh toán khi hoàn nhiều lần!).
  - Kiểm tra năng lực bằng `method instanceof Refundable refundable` (kiểm tra *capability interface*, không rẽ nhánh theo tên kênh!). Nếu kênh không phải `Refundable`, ném custom exception `RefundNotSupportedException`.
- **AC-3.4 (Chứng minh Open/Closed Principle):** Trong `Main.java`, tạo thêm một lớp thanh toán mới (ví dụ `LoyaltyPointsPayment`) và đăng ký vào `CheckoutService` mà **không sửa bất kỳ dòng code nào của `CheckoutService`**.

---

### 3. Ràng buộc kỹ thuật & Yêu cầu Entry Point bắt buộc

1. **Tự thiết kế cấu trúc package trong `src/`:** Không dùng default package. Gợi ý:
   - `com.javalabs.lab11.model` (`PaymentRequest`, `PaymentReceipt`, `RefundReceipt`, `PaymentStatus` — khuyến khích dùng `record` & `enum`)
   - `com.javalabs.lab11.payment` (`PaymentMethod`, `Refundable`, `AbstractPaymentProcessor`, `CreditCardPayment`, `EWalletPayment`, `BankTransferPayment`)
   - `com.javalabs.lab11.notification` (`ReceiptNotifier`, `EmailNotifier`, `SmsNotifier`)
   - `com.javalabs.lab11.exception` (`PaymentException`, `ChannelNotFoundException`, `PaymentValidationException`, `RefundNotSupportedException`, `RefundLimitExceededException`)
   - `com.javalabs.lab11.service` (`CheckoutService`)
   - `com.javalabs.lab11.Main`
2. **Tuyệt đối cấm:** Không viết `if (channelCode.equals("CARD")) ... else if (...)` hoặc `switch (channelCode)` để thực hiện logic thanh toán trong `CheckoutService`.
3. **Bắt buộc có Entry Point tự kiểm chứng (`Main.java`):**
   - Bạn **bắt buộc phải tạo** file entry point (ví dụ `src/com/javalabs/lab11/Main.java` với `public static void main(String[] args)`) chạy kiểm chứng **đầy đủ Acceptance Criteria & Edge Cases** (bao gồm đăng ký thêm cổng mới ở `Main` chứng minh đa hình & OCP).
   - Chạy **tự động không treo chờ `Scanner(System.in)`**, không dùng từ khóa `assert` (tránh phụ thuộc `-ea`), tự viết helper kiểm tra in rõ `[PASS]` / `[FAIL]` và tổng kết.
4. **Java 21 LTS & Không thư viện ngoài:** Chỉ dùng JDK 21 Standard Library và `BigDecimal` (`scale = 2, RoundingMode.HALF_UP`).

---

### 4. Kịch bản Input / Output mẫu & Edge Cases bắt buộc

#### Ví dụ kịch bản minh họa trong `Main`
```text
[1] Đăng ký CARD, EWALLET, BANK vào CheckoutService + 2 notifiers (EMAIL, SMS)
[2] Checkout ORD-01 (amount=200.00) qua CARD -> fee=4.00, totalCharged=204.00, EMAIL & SMS đều nhận được receipt
[3] Checkout ORD-02 (amount=500.00) qua BANK -> fee=0.00 (dùng default method), totalCharged=500.00
[4] Refund 120.00 cho giao dịch CARD của ORD-01 -> thành công; refund thêm 100.00 -> ném RefundLimitExceededException (vượt 200.00)
[5] Refund cho giao dịch BANK của ORD-02 -> ném RefundNotSupportedException (BANK không implements Refundable)
[6] Tạo lớp mới LoyaltyPointsPayment và đăng ký động vào CheckoutService -> checkout thành công không sửa CheckoutService!
```

#### Danh sách Edge Cases bắt buộc kiểm chứng
1. Thanh toán với `PaymentRequest` là `null`, `orderId` blank, `amount <= 0`, hoặc vượt hạn mức kênh (`CreditCardPayment`).
2. `channelCode` không tồn tại / `null` / blank (`ChannelNotFoundException`), hoặc đăng ký trùng `channelCode`.
3. Hoàn tiền với giao dịch `transactionId` không tồn tại, số tiền hoàn `<= 0`, hoặc **hoàn tiền nhiều lần cộng dồn vượt quá số tiền gốc**.
4. Hoàn tiền trên kênh không implement `Refundable` (`BankTransferPayment`) phải ném `RefundNotSupportedException`.
5. Không có `ReceiptNotifier` nào được đăng ký vẫn thanh toán bình thường; khi có nhiều notifier thì tất cả đều nhận đủ thông báo.
6. Bổ sung phương thức thanh toán mới (`LoyaltyPointsPayment` hoặc `CryptoPayment`) không đụng vào `CheckoutService` vẫn chạy đa hình trơn tru.

---

### 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/11-polymorphism-interfaces-payment
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab11.Main
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab11.Main
  ```
*(Nếu bạn đặt entry point ở tên class/package khác, hãy thay `com.javalabs.lab11.Main` tương ứng và ghi chú rõ khi gọi AI chấm bài.)*
