package com.javalabs.lab06.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Thực hành phân biệt `static` vs Instance, Static Initializer Block,
 * Instance Initializer Block và vòng đời / thứ tự khởi tạo biến trong JVM.
 */
public class AppConfig {

    public static final String DEFAULT_REGION = "ap-southeast-1";

    private static int totalInstancesCreated;
    private static String globalEnvironment;
    private static final List<String> INIT_ORDER_LOG = new ArrayList<>();

    static {
        // TODO: Khởi tạo giá trị mặc định cho class:
        // globalEnvironment = "PRODUCTION";
        // totalInstancesCreated = 0;
        // INIT_ORDER_LOG.add("STATIC_BLOCK");
    }

    private int instanceId;
    private String serviceName;
    private String environment;

    {
        // TODO: Instance Initializer Block (chạy mỗi khi `new`, trước thân constructor):
        // INIT_ORDER_LOG.add("INSTANCE_BLOCK");
    }

    /**
     * Khởi tạo AppConfig với `globalEnvironment` hiện hành.
     *
     * @throws IllegalArgumentException nếu serviceName null hoặc blank
     */
    public AppConfig(String serviceName) {
        // TODO:
        // 1. Kiểm tra serviceName != null && !serviceName.isBlank()
        // 2. Tăng totalInstancesCreated và gán this.instanceId = totalInstancesCreated
        // 3. Gán this.serviceName = serviceName.strip(), this.environment = globalEnvironment
        // 4. Ghi INIT_ORDER_LOG.add("CONSTRUCTOR")
        throw new UnsupportedOperationException("TODO: implement AppConfig constructor");
    }

    /**
     * Static factory method: tạo AppConfig mới và gán môi trường riêng `customEnv`
     * (strip + uppercase theo Locale.ROOT) mà không làm đổi `globalEnvironment`.
     *
     * @throws IllegalArgumentException nếu serviceName hoặc customEnv null/blank
     */
    public static AppConfig createWithEnvironment(String serviceName, String customEnv) {
        // TODO: Validate customEnv, tạo AppConfig mới và gán environment riêng cho instance đó
        throw new UnsupportedOperationException("TODO: implement createWithEnvironment");
    }

    public static void setGlobalEnvironment(String newEnv) {
        // TODO: Validate newEnv không null/blank và cập nhật globalEnvironment (strip + uppercase Locale.ROOT)
        throw new UnsupportedOperationException("TODO: implement setGlobalEnvironment");
    }

    public static String getGlobalEnvironment() {
        // TODO: Trả về globalEnvironment hiện tại
        throw new UnsupportedOperationException("TODO: implement getGlobalEnvironment");
    }

    public static int getTotalInstancesCreated() {
        // TODO: Trả về tổng số instance đã tạo
        throw new UnsupportedOperationException("TODO: implement getTotalInstancesCreated");
    }

    /**
     * Trả về bản sao bất biến của nhật ký thứ tự khởi tạo (tránh lộ internal mutable list).
     */
    public static List<String> getInitOrderTrace() {
        // TODO: Trả về List.copyOf(INIT_ORDER_LOG)
        throw new UnsupportedOperationException("TODO: implement getInitOrderTrace");
    }

    public int getInstanceId() {
        return instanceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getEnvironment() {
        return environment;
    }

    /**
     * Kết hợp thông tin instance (id, serviceName, environment) với biến static (globalEnvironment).
     * Định dạng: "[#<instanceId>] <serviceName> (<environment> | global=<globalEnvironment>)"
     */
    public String formatInstanceSummary() {
        // TODO: Kết hợp instance fields và static field globalEnvironment theo đúng định dạng
        throw new UnsupportedOperationException("TODO: implement formatInstanceSummary");
    }
}
