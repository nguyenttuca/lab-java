package com.javalabs.lab07;

/**
 * Custom Checked Exception dùng để báo lỗi phân tích cú pháp / dữ liệu dòng CSV.
 * Kế thừa Exception (Checked) nên compiler bắt buộc caller phải catch hoặc throws.
 */
public class CsvParseException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int lineNumber;

    public CsvParseException(String message, int lineNumber) {
        // TODO: Gọi super(message) và lưu lineNumber
        super(message);
        throw new UnsupportedOperationException("TODO: implement CsvParseException(String, int)");
    }

    public CsvParseException(String message, int lineNumber, Throwable cause) {
        // TODO: Gọi super(message, cause) để bảo toàn nguyên nhân gốc và lưu lineNumber
        super(message, cause);
        throw new UnsupportedOperationException("TODO: implement CsvParseException(String, int, Throwable)");
    }

    public int getLineNumber() {
        // TODO: Trả về lineNumber
        throw new UnsupportedOperationException("TODO: implement getLineNumber");
    }
}
