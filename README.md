# Java Foundation to OOP Labs (Java 21 LTS)

Bộ repository thực hành gồm **12 Labs** được thiết kế riêng cho lập trình viên đã có nền tảng **C++ và JavaScript** chuyển sang học **Java** theo chuẩn môi trường làm việc thực tế — từ cú pháp cốt lõi, cơ chế JVM cho tới thiết kế Lập trình Hướng đối tượng (OOP) và các nguyên lý SOLID cơ bản.

---

## 1. Mục tiêu & Đối tượng phù hợp

- **Đối tượng:** Bạn đã từng code C++ và JavaScript, không cần học lại *"biến là gì"* hay *"vòng lặp chạy thế nào"*, nhưng cần:
  - Làm chủ hệ thống kiểu tĩnh chặt chẽ của Java (Primitives vs Wrappers, không có `unsigned`, `BigDecimal`, `Optional`).
  - Tránh các **bẫy chuyển ngữ** kinh điển từ C++/JS sang Java (so sánh `==` vs `.equals()`, Integer Cache, `String` bất biến, Pass-by-Value of Reference, Checked Exception, `package-private` vs `private`, không có con trỏ `&`).
  - Luyện tư duy thiết kế OOP chuẩn chỉnh: Encapsulation & Defensive Copying, Composition vs Inheritance, Polymorphism qua `interface` / `abstract class`, và kiến trúc phân tầng sạch.
- **Phiên bản mục tiêu:** **Java 21 (LTS)** (sử dụng thuần tính năng ổn định từ Java 21 trở xuống).
- **Công cụ build:** **Pure JDK CLI (`javac` / `java`)** — không phụ thuộc Maven/Gradle ở giai đoạn nền tảng để bạn hiểu trực tiếp cách ánh xạ `package` ↔ thư mục, bytecode `.class` và classpath (`-cp`).

---

## 2. Cài đặt môi trường JDK 21 LTS

Yêu cầu cài **JDK 21 LTS** trở lên (Eclipse Temurin / OpenJDK). Nếu máy bạn đã cài bản JDK mới hơn (ví dụ JDK 25/27), toàn bộ lệnh biên dịch trong repo đều dùng cờ `--release 21` để ép chuẩn biên dịch và API tương thích **Java 21 LTS**. *(Lưu ý: JDK 17 trở xuống sẽ không hỗ trợ cờ `--release 21`.)*

### Hướng dẫn cài đặt nhanh
- **macOS (Homebrew):**
  ```bash
  brew install --cask temurin@21
  ```
- **Windows (winget):**
  ```powershell
  winget install EclipseAdoptium.Temurin.21.JDK
  ```
- **Ubuntu / Debian:**
  ```bash
  sudo apt update && sudo apt install -y openjdk-21-jdk
  ```

### Kiểm tra cài đặt
Mở terminal và chạy:
```bash
java -version
javac -version
```
Đảm bảo cả hai lệnh đều hiển thị phiên bản **21** trở lên (ví dụ `javac 21.x.x`).

---

## 3. Cấu trúc toàn bộ Repository

```text
lab-java/
├── .gitignore
├── README.md
├── AGENTS.md                       # Quy chuẩn cho AI Agent trong IDE (Cursor, Roo, Copilot...)
├── AI_GRADING_GUIDE.md             # Giao thức chấm điểm phổ quát cho mọi AI
└── labs/
    ├── 01-primitives-and-syntax/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   └── src/com/javalabs/lab01/
    │       ├── PrimitivesToolkit.java
    │       ├── SensorCliFormatter.java
    │       └── Lab01Test.java
    ├── 02-wrappers-autoboxing-bigdecimal/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   └── src/com/javalabs/lab02/
    │       ├── WrapperDiagnostics.java
    │       ├── CurrencyCalculator.java
    │       └── Lab02Test.java
    ├── 03-strings-pool-and-text/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   └── src/com/javalabs/lab03/
    │       ├── StringPoolLab.java
    │       ├── TextProcessor.java
    │       └── Lab03Test.java
    ├── 04-control-flow-methods-pass-by-value/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   └── src/com/javalabs/lab04/
    │       ├── Box.java
    │       ├── PassByValueExperiment.java
    │       ├── ControlFlowEngine.java
    │       └── Lab04Test.java
    ├── 05-arrays-and-collections/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   └── src/com/javalabs/lab05/
    │       ├── ArrayToolkit.java
    │       ├── CollectionAnalytics.java
    │       └── Lab05Test.java
    ├── 06-packages-modifiers-static/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   └── src/com/javalabs/lab06/
    │       ├── config/AppConfig.java
    │       ├── model/AccountBase.java
    │       ├── model/SamePackageAuditor.java
    │       ├── service/ExternalSubAccount.java
    │       └── Lab06Test.java
    ├── 07-exceptions-and-file-io/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   ├── data/
    │   │   └── transactions_sample.csv
    │   └── src/com/javalabs/lab07/
    │       ├── CsvParseException.java
    │       ├── SafeFileProcessor.java
    │       └── Lab07Test.java
    ├── 08-bridge-to-oop-modern-java/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   └── src/com/javalabs/lab08/
    │       ├── OrderStatus.java
    │       ├── ProductDto.java
    │       ├── Product.java
    │       ├── CatalogLookup.java
    │       └── Lab08Test.java
    ├── 09-encapsulation-digital-wallet/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   └── src/.gitkeep
    ├── 10-inheritance-vs-composition/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   └── src/.gitkeep
    ├── 11-polymorphism-interfaces-payment/
    │   ├── README.md
    │   ├── RUBRIC.md
    │   └── src/.gitkeep
    └── 12-capstone-order-management/
        ├── README.md
        ├── RUBRIC.md
        └── src/.gitkeep
```

---

## 4. Lộ trình học 12 Labs

### Giai đoạn 1 — Nền tảng Java, Cú pháp & Thư viện chuẩn (Lab 01 → Lab 08)
> **Hình thức:** Có sẵn **Starter Code** (`// TODO`), mục **"Góc nhìn cho dev C++ / JS"** và file `*Test.java` để chạy kiểm tra nhanh.

| STT | Tên Lab | Chủ đề trọng tâm | Hình thức | Thư mục |
| :---: | :--- | :--- | :---: | :--- |
| 01 | Primitives, Ép kiểu & Toán tử | 8 primitives, không có `unsigned`, overflow, `final`, `var`, `Scanner`, `printf` | Starter Code | [01-primitives-and-syntax](labs/01-primitives-and-syntax/) |
| 02 | Wrappers, Autoboxing & Độ chính xác | `Integer`/`Double`, bẫy NPE unboxing, Integer Cache `-128..127`, `BigDecimal` | Starter Code | [02-wrappers-autoboxing-bigdecimal](labs/02-wrappers-autoboxing-bigdecimal/) |
| 03 | String Immutability, Pool & Xử lý chuỗi | String Pool, `==` vs `.equals()`, `StringBuilder`, Text Blocks `"""`, chuẩn hóa text | Starter Code | [03-strings-pool-and-text](labs/03-strings-pool-and-text/) |
| 04 | Control Flow, Methods & Truyền tham số | `switch` expression, `for-each`, labeled `break`, overloading, `varargs`, Pass-by-Value | Starter Code | [04-control-flow-methods-pass-by-value](labs/04-control-flow-methods-pass-by-value/) |
| 05 | Mảng & Collections nhập môn | Mảng 1D/2D, `List`/`ArrayList`, `Set`/`HashSet`, `Map`/`HashMap`, `Comparator` & Lambda | Starter Code | [05-arrays-and-collections](labs/05-arrays-and-collections/) |
| 06 | Package, Access Modifiers & `static` | Cấu trúc `package`, `import`, 4 mức truy cập, `static` vs instance, `static` block | Starter Code | [06-packages-modifiers-static](labs/06-packages-modifiers-static/) |
| 07 | Exception Handling & File I/O cơ bản | Checked vs Unchecked Exception, `try-with-resources`, `Path`/`Files`, CSV parser | Starter Code | [07-exceptions-and-file-io](labs/07-exceptions-and-file-io/) |
| 08 | Cầu nối OOP & Modern Java | Class hoàn chỉnh, `toString`/`equals`/`hashCode`, `enum` nâng cao, `record`, `Optional` | Starter Code | [08-bridge-to-oop-modern-java](labs/08-bridge-to-oop-modern-java/) |

### Giai đoạn 2 — OOP & Mô phỏng công việc thực tế (Lab 09 → Lab 12)
> **Hình thức:** **From Scratch** — đọc Technical Spec / Jira Ticket và tự thiết kế toàn bộ cấu trúc package, class, interface từ đầu trong `src/`. Bắt buộc tự tạo 1 entry point (ví dụ `Main.java`) chạy demo đủ Acceptance Criteria & Edge Cases.

| STT | Tên Lab | Chủ đề trọng tâm | Hình thức | Thư mục |
| :---: | :--- | :--- | :---: | :--- |
| 09 | Encapsulation & Domain Validation | Ticket `[FIN-101]` — Lõi Ví điện tử, invariants, Defensive Copying, Custom Exceptions | From Scratch | [09-encapsulation-digital-wallet](labs/09-encapsulation-digital-wallet/) |
| 10 | Inheritance vs Composition | Ticket `[HR-204]` — Hệ thống Tính lương, phân tách *IS-A* (`extends`/`super`) vs *HAS-A* | From Scratch | [10-inheritance-vs-composition](labs/10-inheritance-vs-composition/) |
| 11 | Polymorphism, Abstract Class & Interface | Ticket `[PAY-305]` — Cổng thanh toán đa kênh, `interface` (`default`/`static`), loại bỏ `if-else` | From Scratch | [11-polymorphism-interfaces-payment](labs/11-polymorphism-interfaces-payment/) |
| 12 | Mini-Project Tổng hợp OOP & SOLID | Ticket `[CORE-400]` — Quản lý Đơn hàng CLI đa tầng (`model`/`repository`/`service`), SRP/OCP/DIP | From Scratch | [12-capstone-order-management](labs/12-capstone-order-management/) |

---

## 5. Quy trình tự học & Luyện tập

Thực hiện tuần tự theo 6 bước cho mỗi bài Lab:

1. **Clone repository:**
   ```bash
   git clone git@github.com:nguyenttuca/lab-java.git
   cd lab-java
   ```

2. **Chọn Lab & đọc kỹ đề bài:**
   - Mở `labs/<lab-folder>/README.md` và `RUBRIC.md` để nắm rõ mục tiêu, Edge Cases và các Java Red Flags cần tránh.

3. **Code giải pháp trong `src/`:**
   - **Lab 01–08:** Hoàn thiện toàn bộ phần `// TODO` trong các file `.java` có sẵn (không đổi chữ ký method của starter code để test driver chạy khớp).
   - **Lab 09–12:** Tự tạo cấu trúc package trong `src/` và bắt buộc tạo một file entry point (ví dụ `Main.java`) có hàm `main` thực thi kiểm chứng toàn bộ Acceptance Criteria & Edge Cases.

4. **Biên dịch & tự chạy kiểm tra trên Terminal:**
   Di chuyển vào thư mục của Lab đang làm (ví dụ `cd labs/01-primitives-and-syntax`):
   - **Trên macOS / Linux (`zsh` / `bash`):**
     ```bash
     rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
     java -cp out com.javalabs.lab01.Lab01Test
     ```
   - **Trên Windows (`PowerShell`):**
     ```powershell
     if (Test-Path out) { Remove-Item -Recurse -Force out }
     javac --release 21 -encoding UTF-8 -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
     java -cp out com.javalabs.lab01.Lab01Test
     ```
   - *(Với Lab 09–12, thay `com.javalabs.lab01.Lab01Test` bằng tên đầy đủ của class chứa `main` do bạn tự đặt, ví dụ `com.javalabs.lab09.Main`.)*

5. **Gọi AI chấm bài & Code Review:**
   - Sử dụng các mẫu prompt ở Mục 6 bên dưới để AI đọc code, chạy kiểm chứng và chấm điểm theo `RUBRIC.md`.

6. **Refactor đến khi đạt `PASS`:**
   - Sửa toàn bộ lỗi `[Critical]` và các lỗi `[Major]`/`[Minor]` theo góp ý, chạy lại test và nhờ AI chấm lại cho tới khi đạt `>= 80/100` với `0` lỗi `[Critical]`.

---

## 6. Cơ chế chấm điểm bằng AI (Universal AI Grading)

Repository tích hợp sẵn giao thức chấm bài chuẩn tại [AI_GRADING_GUIDE.md](AI_GRADING_GUIDE.md) và đồng bộ cho các Coding Agent tại [AGENTS.md](AGENTS.md). Khi được yêu cầu chấm bài, mọi AI sẽ đóng vai **Senior Java Code Reviewer**:
- Đọc `README.md`, `RUBRIC.md` và toàn bộ code trong `src/`.
- Biên dịch với `--release 21` và chạy kiểm thử (nếu có terminal) hoặc phân tích tĩnh + dry-run (nếu chỉ chat trên web).
- **Không tự sửa code hộ** và **không tiết lộ full solution** nếu chưa đạt — chỉ chỉ rõ `File:Line`, giải thích cơ chế Java (đối chiếu C++/JS) và gợi ý hướng sửa.
- Trả về báo cáo chấm điểm cố định (`PASS` / `NEEDS REVISION`, bảng điểm 100, danh sách lỗi `[Critical]`/`[Major]`/`[Minor]`, checklist bước tiếp theo).

### Mẫu Prompt Copy-Paste

#### A. Dành cho AI Agent tích hợp trong IDE / Terminal
*(Cursor, Roo Code, GitHub Copilot Workspace, Claude Code, Codex, Antigravity...)*

```text
Hãy đóng vai Senior Java Code Reviewer theo đúng quy chuẩn trong AGENTS.md và AI_GRADING_GUIDE.md.
Chấm bài giúp mình tại thư mục: labs/01-primitives-and-syntax/

Yêu cầu:
1. Đọc README.md, RUBRIC.md và toàn bộ file trong src/ của Lab này.
2. Dùng terminal biên dịch bằng lệnh `javac --release 21 -encoding UTF-8 -d out ...`, chạy test mẫu và tự kiểm tra thêm các edge cases.
3. Không sửa trực tiếp code trong src/ của mình và không đưa full solution nếu chưa đạt.
4. Xuất báo cáo chấm điểm đúng theo template trong AI_GRADING_GUIDE.md.
```
*(Thay `labs/01-primitives-and-syntax/` bằng thư mục Lab bạn đang làm. Nếu là Lab 09–12, bổ sung thêm: *"Class entry point của mình là `com.javalabs.labXX.Main`."*)*

#### B. Dành cho AI Chat trên Web (ChatGPT, Claude, Gemini web)
*(Khi AI không truy cập trực tiếp vào thư mục máy bạn)*

```text
Hãy đóng vai Senior Java Code Reviewer và chấm bài Lab Java của mình theo đúng quy trình 4 bước, nguyên tắc sư phạm và template báo cáo bên dưới.

[1] Quy tắc chấm bài (Dán nội dung file AI_GRADING_GUIDE.md vào đây)
[2] Đề bài Lab (Dán nội dung labs/XX-.../README.md vào đây)
[3] Bảng điểm Lab (Dán nội dung labs/XX-.../RUBRIC.md vào đây)
[4] Code bài làm của mình trong src/ (Dán toàn bộ các file .java của bạn vào đây)

Lưu ý: Hãy phân tích tĩnh, trace logic chi tiết từng trường hợp biên, không viết lại full solution nếu bài bị NEEDS REVISION, và đối chiếu nhanh với C++/JS nếu lỗi xuất phát từ thói quen chuyển ngôn ngữ.
```

#### C. Mẫu Prompt xin gợi ý khi bị kẹt (không lộ đáp án)

```text
Mình đang làm labs/04-control-flow-methods-pass-by-value/ và gặp lỗi ở file PassByValueExperiment.java (hoặc lỗi compiler dưới đây).
Đừng viết code đáp án hoàn chỉnh giúp mình. Hãy giải thích nguyên nhân theo cơ chế Java (liên hệ với C++/JS nếu phù hợp) và gợi ý hướng tự sửa.

<Dán đoạn code hoặc thông báo lỗi terminal tại đây>
```
