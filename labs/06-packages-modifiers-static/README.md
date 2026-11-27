# Lab 06 — Package, Access Modifiers & `static` vs Instance

- **Giai đoạn:** 1 — Nền tảng Java & Cú pháp
- **Hình thức:** Starter Code (`// TODO`)
- **Độ khó:** Trung bình
- **Thời gian dự kiến:** 75–90 phút
- **Phiên bản chuẩn:** Java 21 LTS

---

## 1. Mục tiêu học tập (Learning Objectives)

Sau khi hoàn thành bài Lab này, bạn sẽ nắm vững:
- Quy tắc ánh xạ 1-1 giữa **`package`** và cấu trúc thư mục vật lý trong `src/`, cách dùng `import` và xử lý xung đột tên class bằng Fully Qualified Class Name (FQCN).
- Phân biệt chính xác **4 mức Access Modifiers** của Java: `public`, `protected`, **package-private (default)**, và `private` — đặc biệt là quy tắc truy cập `protected` khi khác package (JLS §6.6.2).
- Phân biệt rạch ròi thành viên **`static` (thuộc về Class)** với thành viên **Instance (thuộc về từng Object)**, quy tắc không thể truy cập `this`/instance member từ ngữ cảnh `static`.
- Hiểu rõ **thứ tự khởi tạo của JVM** (`static` block → instance initializer block → `constructor`) và vòng đời của biến cục bộ vs instance field vs `static` field.

---

## 2. Góc nhìn cho dev C++ / JS

- **`package` bắt buộc khớp thư mục vật lý:** Khác với `namespace` trong C++ (có thể đặt tên tùy ý trong file nào cũng được), trong Java nếu file nằm ở `src/com/javalabs/lab06/model/AccountBase.java` thì dòng đầu tiên **bắt buộc** là `package com.javalabs.lab06.model;`.
- **Không ghi gì không phải là `private` hay `public`!**
  - Trong C++, thành viên mặc định của `class` là `private`.
  - Trong JS, mọi method/property khai báo trong `class` mặc định là công khai (`public`).
  - Trong Java, khi **không ghi modifier** nào, phạm vi truy cập là **package-private (default)**: *mọi class khác trong cùng thư mục package đều truy cập trực tiếp được*, nhưng khác package thì bị chặn (kể cả lớp con!).
- **`protected` trong Java rộng hơn C++ ở chỗ cùng package, nhưng chặt hơn khi khác package:**
  1. Ở **cùng package** (`model.SamePackageAuditor` và `model.AccountBase`), các class **không hề kế thừa** vẫn truy cập được trường `protected` của nhau! (Trong C++ thì không).
  2. Ở **khác package** (`service.ExternalSubAccount extends AccountBase`), lớp con chỉ truy cập được trường `protected` qua **`this` hoặc biến có kiểu lớp con (`ExternalSubAccount peer`)** — **KHÔNG** được truy cập `otherBase.protectedField` thông qua tham chiếu có kiểu lớp cha `AccountBase`!
- **`static` block & thứ tự nạp class:** Java có khối `static { ... }` chạy **duy nhất 1 lần** khi JVM nạp class vào bộ nhớ (trước khi tạo bất kỳ object nào). Trong `static` method (như `main`), không tồn tại con trỏ `this`.

---

## 3. Đề bài chi tiết

Lab 06 tổ chức theo cấu trúc đa package trong `src/com/javalabs/lab06/`:
- [model/AccountBase.java](src/com/javalabs/lab06/model/AccountBase.java) — Lớp nền có sẵn hoàn chỉnh chứa đủ 4 mức truy cập (`public`, `protected`, package-private, `private`). *(Không chỉnh sửa file này.)*
- Bạn cần hoàn thiện `// TODO` trong 3 file sau:

### Phần A: `model/SamePackageAuditor.java` (Cùng package với `AccountBase`)

1. `inspectAccountInSamePackage(AccountBase account)` → `String`
   - Nếu `account == null`, ném `IllegalArgumentException`.
   - Truy cập trực tiếp các thành viên mà một class **cùng package** nhìn thấy được (`publicAccountCode`, `protectedRegionTag`, `packageInternalTier`), kết hợp getter công khai `account.getMaskedPin()` cho dữ liệu `private`.
   - Trả về chuỗi đúng định dạng: `"code=%s|region=%s|tier=%s|pin=%s"`.
2. `upgradePackageTier(AccountBase account, String newTier)` → `void`
   - Nếu `account == null || newTier == null || newTier.isBlank()`, ném `IllegalArgumentException`.
   - Gán trực tiếp `account.packageInternalTier = newTier.strip();` (chứng minh quyền truy cập package-private trong cùng package `model`).

### Phần B: `service/ExternalSubAccount.java` (`extends AccountBase`, khác package)

1. Constructor `ExternalSubAccount(String accountCode, String regionTag, String internalTier, String secretPin)`
   - Đã gọi sẵn `super(...)` để truyền dữ liệu lên `AccountBase`.
2. `readOwnAccessibleDetails()` → `String`
   - Đọc trực tiếp trên `this` những gì lớp con ở **khác package** truy cập được (`publicAccountCode`, `protectedRegionTag`, `buildProtectedAuditLine()`), kết hợp `getMaskedPin()` (vì `privateSecretPin` và `packageInternalTier` đều bị chặn!).
   - Trả về chuỗi đúng định dạng: `"code=%s|region=%s|audit=%s|pin=%s"`.
3. `updateOwnProtectedRegion(String newRegion)` → `void`
   - Nếu `newRegion == null || newRegion.isBlank()`, ném `IllegalArgumentException`.
   - Cập nhật trực tiếp `this.protectedRegionTag = newRegion.strip();`.
4. `readPeerSubAccountRegion(ExternalSubAccount peer)` → `String`
   - Nếu `peer == null`, ném `IllegalArgumentException`.
   - Truy cập trực tiếp `peer.protectedRegionTag` (hợp lệ vì `peer` có kiểu tĩnh là `ExternalSubAccount` — cùng lớp con ở package `service`).
5. `readOtherBaseViaPublicOnly(AccountBase otherBase)` → `String`
   - Nếu `otherBase == null`, ném `IllegalArgumentException`.
   - Chỉ được đọc `otherBase.publicAccountCode` và `otherBase.getMaskedPin()` (vì với tham chiếu kiểu lớp cha `AccountBase` ở khác package, trình biên dịch **cấm** truy cập `otherBase.protectedRegionTag`!).
   - Trả về chuỗi: `"code=%s|pin=%s"`.

### Phần C: `config/AppConfig.java` (`static` vs Instance & Lifecycle)

1. Khối `static { ... }` (Static Initializer Block):
   - Chạy duy nhất 1 lần khi nạp class: gán `globalEnvironment = "PRODUCTION"`, `totalInstancesCreated = 0`, và thêm `"STATIC_BLOCK"` vào `INIT_ORDER_LOG`.
2. Khối Instance `{ ... }` (Instance Initializer Block):
   - Chạy mỗi lần tạo object (ngay trước thân constructor): thêm `"INSTANCE_BLOCK"` vào `INIT_ORDER_LOG`.
3. Constructor `public AppConfig(String serviceName)`:
   - Nếu `serviceName == null || serviceName.isBlank()`, ném `IllegalArgumentException`.
   - Tăng biến `static` `totalInstancesCreated`, gán `this.instanceId = totalInstancesCreated`, gán `this.serviceName = serviceName.strip()`, `this.environment = globalEnvironment`, và thêm `"CONSTRUCTOR"` vào `INIT_ORDER_LOG`.
4. Static factory `createWithEnvironment(String serviceName, String customEnv)` → `AppConfig`:
   - Nếu `customEnv == null || customEnv.isBlank()`, ném `IllegalArgumentException`.
   - Tạo `AppConfig` mới qua constructor `new AppConfig(serviceName)`, cập nhật `config.environment = customEnv.strip().toUpperCase(Locale.ROOT);` và trả về.
5. `setGlobalEnvironment(String newEnv)` (`static`) & `getGlobalEnvironment()` (`static`):
   - Validate không `null`/blank, cập nhật/đọc biến `static` `globalEnvironment` (trim & uppercase theo `Locale.ROOT`).
6. `getTotalInstancesCreated()` (`static`), `getInitOrderTrace()` (`static` trả về `List.copyOf(INIT_ORDER_LOG)`), cùng các instance getters `getInstanceId()`, `getServiceName()`, `getEnvironment()` và `formatInstanceSummary()` (trả về `"[#%d] %s (%s | global=%s)"`).

---

## 4. Ví dụ Input / Output & Edge Cases bắt buộc

### Ví dụ minh họa
- `SamePackageAuditor.inspectAccountInSamePackage(acc)` → `"code=ACC-01|region=VN-SOUTH|tier=GOLD|pin=***89"`
- `ExternalSubAccount.readOwnAccessibleDetails()` → `"code=ACC-02|region=SG|audit=PROTECTED:SG|pin=***45"`
- `ExternalSubAccount.readOtherBaseViaPublicOnly(base)` → `"code=ACC-01|pin=***89"`
- `AppConfig.getInitOrderTrace()` sau khi khởi tạo object đầu tiên → `["STATIC_BLOCK", "INSTANCE_BLOCK", "CONSTRUCTOR"]`; sau object thứ hai → `["STATIC_BLOCK", "INSTANCE_BLOCK", "CONSTRUCTOR", "INSTANCE_BLOCK", "CONSTRUCTOR"]`.

### Danh sách Edge Cases bắt buộc kiểm tra
1. Đổi `globalEnvironment` qua `static` method ảnh hưởng tới giá trị trả về của `formatInstanceSummary()` trên mọi instance nhưng **không** ghi đè `environment` riêng đã chốt lúc khởi tạo của từng instance.
2. Thứ tự khởi tạo: `"STATIC_BLOCK"` chỉ xuất hiện **đúng 1 lần** ở đầu danh sách, bất kể tạo bao nhiêu object `AppConfig`.
3. `instanceId` tăng tuần tự độc lập cho từng instance (`c1.getInstanceId() + 1 == c2.getInstanceId()`).
4. Truyền `null` vào toàn bộ các hàm kiểm tra/inspector ở `SamePackageAuditor` và `ExternalSubAccount`, hoặc chuỗi blank vào `upgradePackageTier`, `updateOwnProtectedRegion`, `AppConfig`.
5. `getInitOrderTrace()` trả về snapshot bất biến (`List.copyOf`) — caller không thể sửa trộm log nội bộ.

---

## 5. Lệnh biên dịch & chạy kiểm tra

Di chuyển vào thư mục bài Lab:
```bash
cd labs/06-packages-modifiers-static
```

- **macOS / Linux (`zsh` / `bash`):**
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  java -cp out com.javalabs.lab06.Lab06Test
  ```
- **Windows (`PowerShell`):**
  ```powershell
  if (Test-Path out) { Remove-Item -Recurse -Force out }
  javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
  java -cp out com.javalabs.lab06.Lab06Test
  ```
