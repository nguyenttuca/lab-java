package com.javalabs.lab07;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Thực hành Checked vs Unchecked Exception, try-with-resources (AutoCloseable),
 * và đọc/ghi file an toàn bằng java.nio.file.Path & Files.
 */
public final class SafeFileProcessor {

    private SafeFileProcessor() {
        // Utility class
    }

    /**
     * Đối tượng báo cáo kết quả bất biến (có sẵn, không cần sửa).
     */
    public static final class ProcessingReport {
        private final int validCount;
        private final int errorCount;
        private final BigDecimal netTotal;
        private final List<String> errorMessages;

        public ProcessingReport(int validCount, int errorCount, BigDecimal netTotal, List<String> errorMessages) {
            if (validCount < 0 || errorCount < 0 || netTotal == null || errorMessages == null) {
                throw new IllegalArgumentException("Invalid report parameters");
            }
            this.validCount = validCount;
            this.errorCount = errorCount;
            this.netTotal = netTotal;
            this.errorMessages = List.copyOf(errorMessages);
        }

        public int getValidCount() {
            return validCount;
        }

        public int getErrorCount() {
            return errorCount;
        }

        public BigDecimal getNetTotal() {
            return netTotal;
        }

        public List<String> getErrorMessages() {
            return errorMessages;
        }
    }

    /**
     * Phân tích 1 dòng CSV định dạng: `tx_id,type,amount,currency`.
     * - Trả về +amount nếu type là CREDIT, -amount nếu type là DEBIT.
     * - Ném IllegalArgumentException (Unchecked) nếu line null/blank hoặc lineNumber < 1.
     * - Ném CsvParseException (Checked) nếu sai số cột (!= 4), có cột blank,
     *   type không phải CREDIT/DEBIT, amount không hợp lệ/<= 0 (giữ cause NumberFormatException),
     *   hoặc currency không phải 3 chữ cái ASCII in hoa.
     */
    public static BigDecimal parseSignedTransactionLine(String line, int lineNumber) throws CsvParseException {
        // TODO: Dùng line.split(",", -1), BigDecimal và ném CsvParseException khi dữ liệu dòng lỗi
        throw new UnsupportedOperationException("TODO: implement parseSignedTransactionLine");
    }

    /**
     * Đọc file CSV giao dịch bằng try-with-resources (Files.newBufferedReader + UTF-8):
     * - Bỏ qua dòng 1 nếu là dòng header (`tx_id,...`).
     * - Bỏ qua các dòng trắng (isBlank).
     * - Bắt CsvParseException từng dòng để đếm errorCount & ghi nhận `"Line X: ..."`
     *   thay vì làm gián đoạn cả batch.
     * - Để nguyên IOException (ví dụ file không tồn tại) truyền ra ngoài.
     * - Chuẩn hóa netTotal về scale = 2, RoundingMode.HALF_UP.
     *
     * @throws IllegalArgumentException nếu csvPath là null
     * @throws IOException              nếu xảy ra lỗi đọc file / file không tồn tại
     */
    public static ProcessingReport processCsvLedger(Path csvPath) throws IOException {
        // TODO: Dùng try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8))
        throw new UnsupportedOperationException("TODO: implement processCsvLedger");
    }

    /**
     * Xuất ProcessingReport ra file UTF-8 (tự tạo thư mục cha nếu chưa tồn tại):
     * Định dạng:
     * VALID_ROWS=<validCount>
     * ERROR_ROWS=<errorCount>
     * NET_TOTAL=<netTotal.toPlainString()>
     * ERRORS:
     * - <error1>
     * (Nếu không có lỗi nào, dòng cuối thay bằng `ERRORS: NONE`)
     *
     * @throws IllegalArgumentException nếu outputPath hoặc report null
     * @throws IOException              nếu lỗi ghi file
     */
    public static void exportReport(Path outputPath, ProcessingReport report) throws IOException {
        // TODO: Tạo parent directories nếu cần và ghi file bằng Files.writeString(..., StandardCharsets.UTF_8)
        throw new UnsupportedOperationException("TODO: implement exportReport");
    }

    /**
     * Thực thi task trong phạm vi quản lý của AutoCloseable bằng `try-with-resources`
     * để bảo đảm `resource.close()` luôn được gọi ngay cả khi `task.run()` ném exception.
     *
     * @throws IllegalArgumentException nếu resource hoặc task là null
     * @throws Exception                nếu resource.close() ném lỗi
     */
    public static void useAutoCloseableSafely(AutoCloseable resource, Runnable task) throws Exception {
        // TODO: Validate non-null và dùng try (AutoCloseable managed = resource) { task.run(); }
        throw new UnsupportedOperationException("TODO: implement useAutoCloseableSafely");
    }
}
