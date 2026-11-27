# Lab 12 — Mini-Project Tổng hợp OOP & SOLID (`[CORE-400]`)

- **Giai đoạn:** 2 — OOP & Mô phỏng công việc thực tế (Capstone Project)
- **Hình thức:** **From Scratch** (Đọc Technical Spec tự thiết kế kiến trúc đa tầng hoàn chỉnh trong `src/`)
- **Độ khó:** Khá → Nâng cao (Tổng hợp 12 Labs)
- **Thời gian dự kiến:** 4–6 giờ
- **Phiên bản chuẩn:** Java 21 LTS

---

## Jira Ticket: `[CORE-400]` Xây dựng Hệ thống Quản lý Đơn hàng & Tồn kho (`SmartOrder CLI`)

### 1. Bối cảnh nghiệp vụ (Business Context)
Đây là dự án tổng kết toàn bộ lộ trình Java Foundation → OOP. Bạn đóng vai kỹ sư phần mềm xây dựng **SmartOrder** — ứng dụng console quản lý danh mục sản phẩm tồn kho, đặt và xử lý vòng đời đơn hàng, áp dụng chiến lược giảm giá linh hoạt, trừ/hoàn kho nguyên tử, và xuất báo cáo tổng kết ra file văn bản.

Yêu cầu cốt lõi không chỉ là "chạy đúng chức năng" mà phải thể hiện **kiến trúc phân tầng chuẩn môi trường làm việc (`model`, `exception`, `repository`, `service`, `app`)** kết hợp các nguyên lý **Clean Code & SOLID nền tảng (SRP, OCP, DIP)**.

---

### 2. User Stories & Acceptance Criteria

#### Story 1 — Tầng Domain (`model`) & Vòng đời Đơn hàng
> *Là hệ thống bán hàng, tôi muốn dữ liệu sản phẩm và đơn hàng luôn nhất quán theo đúng nghiệp vụ tại mọi thời điểm.*
- **AC-1.1 (`Product` & Kho hàng):**
  - Mỗi `Product` có `sku` (duy nhất, chuẩn hóa uppercase), `name`, `unitPrice` (`BigDecimal >= 0`), `stock` (`int >= 0`), override `equals`/`hashCode` theo `sku`.
  - Hỗ trợ `restock(int)` và `deductStock(int)` có kiểm tra tồn kho (ném custom exception `InsufficientStockException` nếu vượt tồn kho).
- **AC-1.2 (`OrderStatus` `enum`):**
  - Hỗ trợ các trạng thái vòng đời: `NEW` → `PAID` → `SHIPPED` → `DELIVERED`, và `CANCELLED` (chỉ được hủy từ `NEW` hoặc `PAID`).
  - Cài đặt quy tắc chuyển trạng thái ngay trong `enum` (`canTransitionTo`) — từ chối chuyển ngược hoặc chuyển tiếp từ trạng thái kết thúc (`DELIVERED`, `CANCELLED`).
- **AC-1.3 (`OrderItem` & `Order`):**
  - `OrderItem` (khuyến khích `record` bất biến) lưu snapshot `sku`, `productName`, `unitPriceAtPurchase`, `quantity` (`> 0`) để khi giá `Product` trong kho thay đổi về sau, tổng tiền các đơn đã đặt **không bị biến đổi theo**.
  - `Order` đóng gói chặt chẽ danh sách `OrderItem` (Defensive Copying / `List.copyOf`), giữ `subtotal`, `discountAmount`, `finalTotal` (`>= 0.00`), `status`, và chỉ thay đổi trạng thái qua nghiệp vụ hợp lệ.

#### Story 2 — Tầng Lưu trữ Trừu tượng (`repository` — Áp dụng DIP)
> *Là kiến trúc sư hệ thống, tôi muốn tầng Service không phụ thuộc vào cách lưu trữ cụ thể (`HashMap` trong RAM hay Database/File sau này).*
- **AC-2.1:** Định nghĩa `interface ProductRepository` và `interface OrderRepository` (sử dụng `Optional<T>` cho các hàm `findBy...`, trả về bản sao/unmodifiable list cho `findAll()`).
- **AC-2.2:** Cài đặt `InMemoryProductRepository` và `InMemoryOrderRepository` dùng `Map` (`HashMap`/`LinkedHashMap`).

#### Story 3 — Chiến lược Giảm giá & Nghiệp vụ Đơn hàng (`service` — Áp dụng SRP & OCP)
> *Là quản lý vận hành, tôi muốn áp dụng nhiều chương trình khuyến mãi khác nhau, đảm bảo đặt đơn trừ kho nguyên tử và hủy đơn tự động hoàn kho.*
- **AC-3.1 (`interface DiscountPolicy` — OCP):**
  - Thiết kế strategy interface `DiscountPolicy` tính số tiền giảm (`BigDecimal calculateDiscount(BigDecimal subtotal)`), kèm ít nhất 3 cài đặt:
    1. `NoDiscountPolicy` (giảm `0.00`).
    2. `PercentageDiscountPolicy` (giảm theo `%` kèm mức giảm tối đa `maxDiscountAmount`).
    3. `ThresholdDiscountPolicy` (giảm số tiền cố định nếu `subtotal >= minThreshold`).
  - Mức giảm thực tế không bao giờ vượt quá `subtotal` (đảm bảo `finalTotal >= 0.00`).
- **AC-3.2 (`OrderService` — Constructor Injection & DIP):**
  - `OrderService` nhận `ProductRepository`, `OrderRepository` (và có thể nhận `DiscountPolicy` mặc định hoặc cho phép truyền theo đơn) qua **Constructor** — **KHÔNG** tự `new InMemoryOrderRepository()` bên trong service!
  - **Đặt đơn (`placeOrder`):**
    - Từ chối danh sách món rỗng/null, trùng `orderId`, `quantity <= 0`, hoặc SKU không tồn tại (`EntityNotFoundException`).
    - Nếu cùng 1 SKU xuất hiện nhiều dòng trong một yêu cầu đặt đơn, phải kiểm tra tổng số lượng hoặc cộng gộp chuẩn xác trước khi trừ kho!
    - **Tính nguyên tử (Atomicity):** Kiểm tra toàn bộ sản phẩm đủ tồn kho **trước khi** trừ kho bất kỳ sản phẩm nào (nếu sản phẩm thứ 2 thiếu hàng, sản phẩm thứ 1 tuyệt đối không bị trừ kho!).
  - **Chuyển trạng thái & Hủy đơn (`payOrder`, `shipOrder`, `deliverOrder`, `cancelOrder`):**
    - Chuyển trạng thái sai quy tắc ném `InvalidOrderStateException`.
    - `cancelOrder`: chỉ cho hủy khi đơn đang ở `NEW` hoặc `PAID`; khi hủy thành công phải **tự động hoàn trả đúng số lượng từng món về lại tồn kho `Product`**! Tuyệt đối chặn hủy lại đơn đã `CANCELLED` (tránh hoàn kho 2 lần) hoặc đơn đã `SHIPPED`/`DELIVERED`.

#### Story 4 — Xuất Báo cáo File & Giao diện CLI tách biệt (`app` — Áp dụng SRP)
> *Là cửa hàng trưởng, tôi muốn xuất báo cáo tổng kết đơn hàng ra file UTF-8 và thao tác qua menu console gọn gàng.*
- **AC-4.1 (Export Report — `Files` & `try-with-resources`):**
  - Hỗ trợ xuất báo cáo ra `Path` bằng UTF-8 (`Files.writeString` hoặc `BufferedWriter` trong `try-with-resources`): thống kê tổng số đơn, số đơn theo từng `OrderStatus`, và tổng doanh thu từ các đơn hợp lệ (không tính đơn `CANCELLED`!).
- **AC-4.2 (Tách biệt I/O khỏi Business Logic):**
  - Các class trong `service` và `repository` **KHÔNG** được gọi `System.out.println` hay đọc `Scanner(System.in)`. Mọi nhập xuất console chỉ nằm ở tầng `app` (ví dụ `SmartOrderCli` nhận `Scanner` và `PrintStream` qua tham số).

---

### 3. Ràng buộc kỹ thuật & Yêu cầu Entry Point bắt buộc

1. **Tự thiết kế cấu trúc package phân tầng trong `src/`:**
   - `com.javalabs.lab12.model`
   - `com.javalabs.lab12.exception`
   - `com.javalabs.lab12.repository`
   - `com.javalabs.lab12.service`
   - `com.javalabs.lab12.app` (chứa `SmartOrderCli` và `Main.java`)
2. **Bắt buộc có Entry Point tự kiểm chứng (`Main.java`):**
   - Tạo `src/com/javalabs/lab12/app/Main.java` (hoặc `com.javalabs.lab12.Main`) với nguyên tắc:
     - **Mặc định khi chạy không đối số (`java -cp out com.javalabs.lab12.app.Main`):** Chạy **tự động** toàn bộ kịch bản kiểm chứng End-to-End (đủ Acceptance Criteria, mô phỏng CLI qua `Scanner(String)` + `ByteArrayOutputStream`, test xuất & đọc kiểm tra file report trong thư mục tạm rồi tự xóa sạch thư mục tạm, test mọi Edge Cases), dùng helper tự viết (KHÔNG dùng `assert` phụ thuộc `-ea`), in rõ `[PASS]`/`[FAIL]` và tổng kết — **tuyệt đối không treo chờ `System.in`**.
     - *(Tùy chọn khuyến khích)* Nếu người dùng chạy kèm cờ `--interactive` (`java -cp out com.javalabs.lab12.app.Main --interactive`), mới khởi chạy menu CLI tương tác với `System.in`.
3. **Java 21 LTS & Không thư viện ngoài:** Chỉ dùng JDK 21 Standard Library, `BigDecimal` (`scale = 2, RoundingMode.HALF_UP`), `Optional`, Collections đúng kiểu.

---

### 4. Kịch bản Input / Output mẫu & Edge Cases bắt buộc

#### Ví dụ luồng nghiệp vụ kiểm chứng trong `Main`
```text
[1] Nhập kho: P01 (Laptop, 1000.00, stock=5), P02 (Mouse, 25.00, stock=10)
[2] Tạo đơn ORD-01 (1 Laptop + 2 Mouse), dùng PercentageDiscountPolicy(10%, max=80.00):
    -> subtotal = 1050.00, discount = 80.00 (bị chặn trần max), finalTotal = 970.00
    -> Tồn kho: P01 còn 4, P02 còn 8
[3] Tạo đơn ORD-02 (1 Mouse + 10 Laptop - vượt tồn kho P01!):
    -> Ném InsufficientStockException -> P02 vẫn giữ nguyên tồn kho = 8 (Atomic!)
[4] Đổi giá P01 lên 1200.00 -> Kiểm tra ORD-01 vẫn giữ finalTotal = 970.00 (Snapshot price!)
[5] Thanh toán ORD-01 (NEW -> PAID), rồi hủy ORD-01 (PAID -> CANCELLED):
    -> Tồn kho P01 hoàn lại 5, P02 hoàn lại 10!
    -> Gọi cancelOrder("ORD-01") lần 2 -> Ném InvalidOrderStateException (không hoàn kho kép!)
[6] Xuất báo cáo ra Path tạm -> Xác minh file tồn tại, chứa số liệu chính xác, rồi dọn file tạm.
```

#### Danh sách Edge Cases bắt buộc kiểm chứng
1. Đặt đơn với `orderId` trùng lặp, `items` rỗng/null, `quantity <= 0`, hoặc `SKU` không tồn tại.
2. Đặt đơn có 2 dòng, dòng 1 đủ hàng nhưng dòng 2 thiếu hàng → xác minh tồn kho dòng 1 **không bị trừ**.
3. Đặt đơn lặp lại cùng 1 SKU ở 2 dòng khác nhau (ví dụ dòng 1 mua `3` cái, dòng 2 mua `3` cái khi kho chỉ có `5` cái) → phải phát hiện thiếu hàng hoặc gộp chính xác.
4. Giảm giá vượt `subtotal` (ví dụ giảm cố định `100.00` cho đơn `40.00`) → `discountAmount` tối đa `40.00`, `finalTotal = 0.00` (không âm!).
5. Thay đổi giá sản phẩm sau khi đơn hàng đã tạo → đơn cũ vẫn giữ nguyên đơn giá tại thời điểm mua.
6. Hủy đơn ở trạng thái `SHIPPED`/`DELIVERED` hoặc hủy lại đơn đã `CANCELLED` → ném `InvalidOrderStateException`.
7. Thao tác sửa danh sách `order.getItems().clear()` hoặc `productRepo.findAll().clear()` từ bên ngoài → bị chặn.
8. Kiểm tra xuất báo cáo không tính doanh thu từ đơn `CANCELLED`, và dọn dẹp file/thư mục tạm sạch sẽ sau test.

---

### 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/12-capstone-order-management
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab12.app.Main
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab12.app.Main
  ```
*(Nếu bạn đặt entry point ở `com.javalabs.lab12.Main`, hãy chạy tương ứng và ghi chú rõ khi gọi AI chấm bài.)*

> **Ghi chú chuyển tiếp sang dự án thực tế (Maven / Gradle):**
> Cấu trúc package `com.javalabs.lab12.{model,exception,repository,service,app}` trong `src/` bạn vừa xây dựng chính là cấu trúc chuẩn công nghiệp bên trong `src/main/java/` của mọi dự án Spring Boot / Maven / Gradle thực tế — nơi bạn chỉ cần bổ sung `pom.xml` hoặc `build.gradle` và chuyển phần kiểm thử tự động trong `Main.java` sang `src/test/java/` với JUnit 5.
