# RUBRIC — Lab 09: Encapsulation & Domain Validation (`[FIN-101]`)

- **Thang điểm:** 100
- **Điều kiện Đạt (`PASS`):** Tổng điểm `>= 80 / 100` **và** `0` lỗi `[Critical]`.

---

## 1. Bảng tiêu chí chấm điểm

| Nhóm tiêu chí | Trọng số | Mô tả chi tiết yêu cầu đạt điểm tối đa |
| :--- | :---: | :--- |
| **1. Đáp ứng Acceptance Criteria** | **30** | Đầy đủ nghiệp vụ khởi tạo, nạp, rút theo hạn mức ngày, chuyển khoản nguyên tử (atomic transfer), đóng băng/mở khóa/đóng ví, lịch sử giao dịch. |
| **2. Xử lý Edge Cases & Domain Validation** | **25** | Bao phủ đủ 8 Edge Cases bắt buộc (số âm/0/`null`, vượt số dư, vượt hạn mức ngày & reset sang ngày mới, self-transfer, chuyển sang ví bị khóa không mất tiền, ví đã đóng). |
| **3. Thiết kế Encapsulation & Exception** | **30** | Đóng gói chặt chẽ (không lộ field `public`, không có `setBalance`/`setStatus` bừa bãi), Defensive Copying (`List.copyOf` / `record` bất biến), Custom Domain Exceptions phân tách rõ ràng, dùng `BigDecimal` chuẩn xác. |
| **4. Clean Code, Package & Entry Point** | **15** | Chia package hợp lý (không dùng default package), biên dịch sạch `--release 21`, có `Main.java` chạy kiểm chứng tự động không treo `System.in` và không dựa vào `assert` thiếu `-ea`. |
| **Tổng cộng** | **100** | |

---

## 2. Phân loại mức độ lỗi

### `[Critical]` (Tồn tại ≥ 1 lỗi → `NEEDS REVISION` ngay)
- Code không biên dịch được bằng `javac --release 21 -encoding UTF-8 -d out ...`.
- Thiếu entry point (`Main.java` hoặc class chứa `main`), hoặc `Main` bị treo chờ `Scanner(System.in)` không chạy tự động được.
- Phá vỡ Encapsulation: để field `balance`, `status`, `transactions` là `public` hoặc cung cấp setter công khai cho phép sửa trực tiếp số dư/trạng thái bỏ qua validate.
- Lộ internal mutable state: `getTransactions()` trả về trực tiếp `this.transactions` (`ArrayList`) hoặc `WalletTransaction` vẫn có thể bị mutate từ bên ngoài.
- Dùng `double` / `float` cho số dư và giao dịch tiền tệ.
- Chuyển tiền (`transferTo`) **không nguyên tử**: trừ tiền ví nguồn xong, ví đích `FROZEN` ném exception nhưng ví nguồn đã mất tiền (hoặc lưu lịch sử sai).
- Dùng `IllegalArgumentException` / `RuntimeException` chung chung cho mọi thứ mà không thiết kế Custom Domain Exceptions.

### `[Major]`
- Không có logic reset hạn mức ngày theo `LocalDate` (hoặc không truyền được `LocalDate` khiến không kiểm chứng được trường hợp qua ngày mới).
- Thiếu kiểm tra trường hợp self-transfer (cùng `walletId` giữa 2 đối tượng hoặc `this == target`).
- Cho phép `unfreeze()` trên ví đã `CLOSED`.
- Đặt toàn bộ các class vào 1 package phẳng hoặc default package không tách `model`/`exception`.
- Dùng từ khóa `assert` trong `Main.java` để kiểm chứng (bỏ qua toàn bộ check nếu chạy thiếu `-ea`).

### `[Minor]`
- Chuẩn hóa `scale = 2` của `BigDecimal` thiếu nhất quán giữa các phương thức hiển thị/tính toán.
- Thiếu `toString()` / `equals()`/`hashCode()` hợp lý trên entity hoặc naming chưa chuẩn Java.
- Xuất hiện warning khi biên dịch với `-Xlint:all`.

---

## 3. Danh sách "Java Red Flags" riêng cho Lab 09

1. **Anemic Setter (`wallet.setBalance(999999)`):** Biến OOP thành `struct` rỗng, phá vỡ toàn bộ bất biến nghiệp vụ.
2. **Trả về `return this.transactions;`:** Bất kỳ caller nào gọi `.clear()` hay `.add()` đều phá hỏng dữ liệu lõi của Ví.
3. **Mutate trước, Validate sau trong `transferTo`:** Gây mất cân đối kế toán khi giao dịch nửa chừng gặp lỗi ở ví đích.
4. **Dùng `double` cho tiền tệ:** Sai số `0.1 + 0.2`.
5. **Nuốt Exception hoặc trả về `boolean false` im lặng khi lỗi nghiệp vụ phức tạp:** Caller không phân biệt được lỗi do hết tiền, quá hạn mức ngày hay ví bị khóa.
6. **Dùng `==` so sánh `walletId` (`String`) hoặc `BigDecimal`:** Bug ngay khi chuỗi đọc từ ngoài hoặc khác `scale`.
