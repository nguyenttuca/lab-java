package com.javalabs.lab01;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

/**
 * Đọc dữ liệu cảm biến qua Scanner được truyền vào từ bên ngoài
 * và định dạng output chuẩn bằng printf / String.format.
 */
public final class SensorCliFormatter {

    private SensorCliFormatter() {
        // Utility class
    }

    /**
     * Đọc thông số cảm biến từ đối tượng Scanner truyền vào (không hardcode System.in)
     * và trả về dòng báo cáo định dạng chuẩn Locale.US:
     * "[#%04d] %-18s | %6.2f C | %s" (cột cuối là "ONLINE" hoặc "OFFLINE").
     *
     * Thứ tự 4 dòng đầu vào trong Scanner:
     * 1. sensorId (int > 0)
     * 2. location (String, có thể chứa khoảng trắng, ví dụ "Server Room A")
     * 3. temperature (double >= -273.15, không NaN/Infinite)
     * 4. online (boolean)
     *
     * @throws IllegalArgumentException nếu scanner null hoặc giá trị đọc được không hợp lệ
     */
    public static String readAndFormatReading(Scanner scanner) {
        // TODO:
        // 1. Kiểm tra scanner != null (gợi ý thiết lập scanner.useLocale(Locale.US) hoặc parse chuẩn dấu '.')
        // 2. Đọc sensorId bằng nextInt(), xử lý ký tự xuống dòng thừa trước khi đọc location bằng nextLine()
        // 3. Đọc temperature và online (chú ý trim location, validate toàn bộ giá trị)
        // 4. Trả về chuỗi format bằng String.format(Locale.US, ...)
        throw new UnsupportedOperationException("TODO: implement readAndFormatReading");
    }

    /**
     * In 1 dòng tóm tắt bảng thống kê ra PrintStream bằng printf (Locale.US):
     * "Sensor: %-15s | Samples: %4d | Avg: %7.2f C | Alert: %s%n"
     * với Alert là "YES" nếu alert == true, ngược lại "NO".
     *
     * @throws IllegalArgumentException nếu out null, sensorName null/blank, sampleCount < 0,
     *                                  hoặc avgTemp là NaN/Infinite
     */
    public static void printSummaryTable(
            PrintStream out,
            String sensorName,
            int sampleCount,
            double avgTemp,
            boolean alert
    ) {
        // TODO: Validate tham số đầu vào và gọi out.printf(Locale.US, ...)
        throw new UnsupportedOperationException("TODO: implement printSummaryTable");
    }

    /**
     * Entry point tùy chọn để bạn tự chạy thử nhập từ bàn phím sau khi hoàn thiện TODO.
     * Driver test tự động (Lab01Test) sẽ không gọi main() này.
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in).useLocale(Locale.US)) {
            System.out.println("Nhập lần lượt 4 dòng: <sensorId> / <location> / <temperature> / <true|false>");
            String reading = readAndFormatReading(scanner);
            System.out.println(reading);
            printSummaryTable(System.out, "ManualSensor", 1, 25.0, false);
        }
    }
}
