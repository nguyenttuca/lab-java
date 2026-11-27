# AGENTS.md — Repository Rules for AI Coding Agents

Tài liệu này định hướng trực tiếp cho các AI Agent tích hợp IDE / CLI (Cursor, Roo Code, GitHub Copilot, Claude Code, Codex, Antigravity...) làm việc trong repository thực hành Java này.

## 1. Bối cảnh & Mục tiêu Repository

- Bộ **12 Labs** đào tạo **Java (Java 21 LTS)** từ nền tảng đến OOP chuẩn môi trường làm việc thực tế.
- Đối tượng học viên: Đã biết **C++ và JavaScript**, chưa biết Java.
- Công cụ build: **Pure JDK CLI (`javac` / `java`)**, không dùng Maven/Gradle hay thư viện bên thứ ba.
- Chuẩn biên dịch bắt buộc: Luôn dọn `out/` cũ và dùng cờ `--release 21 -encoding UTF-8`:
  ```bash
  rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
  ```
- Cấu trúc 2 giai đoạn:
  - **Lab 01–08 (Giai đoạn 1):** Có sẵn starter code kèm `// TODO` và driver test `LabXXTest.java` trong `src/com/javalabs/labXX/`.
  - **Lab 09–12 (Giai đoạn 2):** Dạng Technical Spec / Jira Ticket (`src/` ban đầu trống). Học viên tự thiết kế toàn bộ package/class và **bắt buộc tự tạo entry point** (ví dụ `Main.java` với `public static void main(String[] args)`) chạy kiểm chứng đủ Acceptance Criteria và Edge Cases.

## 2. Chế độ hoạt động & Quy tắc ứng xử bắt buộc

Trước khi hành động, hãy xác định người dùng đang yêu cầu chế độ nào:

### Chế độ A: Chấm bài / Code Review (Mặc định khi được nhờ "chấm bài", "review", "kiểm tra Lab XX")
Tuân thủ **100%** chuẩn mực trong [AI_GRADING_GUIDE.md](./AI_GRADING_GUIDE.md):
1. **Quy trình 4 bước:**
   - Bước 1: Đọc `labs/<lab>/README.md` và `labs/<lab>/RUBRIC.md`.
   - Bước 2: Đọc toàn bộ file `.java` trong `labs/<lab>/src/` (và `data/` nếu có).
   - Bước 3: Chạy terminal dọn `out/` cũ, biên dịch với `rm -rf out && javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")` và thực thi `java -cp out ...` (nếu có quyền terminal); đồng thời kiểm tra thêm các Edge Cases. Nếu không có terminal, thực hiện phân tích tĩnh & dry-run.
   - Bước 4: Chấm theo trọng số trong `RUBRIC.md` và trả về đúng **Template Báo cáo Chấm điểm** trong `AI_GRADING_GUIDE.md`.
2. **Ràng buộc sư phạm nghiêm ngặt:**
   - **KHÔNG sửa trực tiếp file code của học viên** trong `src/` trừ khi người dùng yêu cầu rõ ràng (*"hãy sửa giúp mình"*, *"refactor giúp mình"*).
   - **KHÔNG để lại file tạm** trong `src/` hoặc làm bẩn `git status` khi tự chạy test biên bổ sung (nếu cần test tạm, đặt ở thư mục tạm ngoài repo như `/tmp/` hoặc xóa sạch ngay sau khi chấm).
   - **KHÔNG đưa full solution** nếu bài bị `NEEDS REVISION` — chỉ nêu `File:Line`, giải thích cơ chế Java (đối chiếu nhanh C++/JS nếu liên quan), và gợi ý hướng sửa.
   - Điều kiện `PASS`: Tổng điểm `>= 80/100` **và** `0` lỗi `[Critical]`.

### Chế độ B: Hỏi đáp / Gợi ý / Giải thích khái niệm
- Trả lời ngắn gọn, bám sát chuẩn **Java 21 LTS**.
- Liên hệ nhanh điểm khác biệt với **C++ / JavaScript** khi giải thích cơ chế (ví dụ: JVM memory, pass-by-value of reference, checked exception, package visibility, `String` pool).
- Nếu học viên đang bí một bài Lab cụ thể, ưu tiên gợi ý từng bước (hints) thay vì viết hộ toàn bộ lời giải.

### Chế độ C: Được yêu cầu trực tiếp viết/sửa code
- Chỉ thực hiện khi người dùng yêu cầu tường minh.
- Khi viết hoặc sửa code Java trong repo này:
  - Giữ tương thích tuyệt đối với **Java 21 LTS** (kiểm chứng bằng `javac --release 21`).
  - Tuân thủ nghiêm Java Naming Conventions, không dùng raw types, không nuốt exception, không để lộ mutable internal state.
  - Chạy lại biên dịch và test bằng terminal sau khi chỉnh sửa.

## 3. Quy tắc kiểm chứng thông tin

- Chỉ báo cáo `PASS`, tên class, số dòng hoặc kết quả test mà bạn đã trực tiếp đọc từ file hoặc quan sát từ output lệnh vừa chạy.
- Không bao giờ bỏ qua bước đọc `RUBRIC.md` của Lab đang chấm.
