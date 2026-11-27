package com.javalabs.lab06.model;

/**
 * Class nằm CÙNG PACKAGE `com.javalabs.lab06.model` với AccountBase (KHÔNG kế thừa AccountBase).
 * Dùng để kiểm chứng: trong cùng package, Java cho phép truy cập cả `public`,
 * `protected` (khác với C++!) và `package-private`, nhưng vẫn chặn `private`.
 */
public final class SamePackageAuditor {

    private SamePackageAuditor() {
        // Utility class
    }

    /**
     * Đọc thông tin AccountBase từ một class không kế thừa nhưng cùng package:
     * Truy cập trực tiếp `account.publicAccountCode`, `account.protectedRegionTag`,
     * `account.packageInternalTier` và gọi `account.getMaskedPin()`.
     * Định dạng trả về: "code=%s|region=%s|tier=%s|pin=%s"
     *
     * @throws IllegalArgumentException nếu account là null
     */
    public static String inspectAccountInSamePackage(AccountBase account) {
        // TODO: Validate account != null và đọc trực tiếp 3 field cùng package + getMaskedPin()
        throw new UnsupportedOperationException("TODO: implement inspectAccountInSamePackage");
    }

    /**
     * Cập nhật trực tiếp trường package-private `account.packageInternalTier`
     * (chỉ khả thi vì SamePackageAuditor ở cùng package `model`).
     *
     * @throws IllegalArgumentException nếu account null hoặc newTier null/blank
     */
    public static void upgradePackageTier(AccountBase account, String newTier) {
        // TODO: Validate đầu vào và gán account.packageInternalTier = newTier.strip();
        throw new UnsupportedOperationException("TODO: implement upgradePackageTier");
    }
}
