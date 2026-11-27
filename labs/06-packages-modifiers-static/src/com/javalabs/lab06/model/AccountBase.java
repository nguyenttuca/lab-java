package com.javalabs.lab06.model;

/**
 * Lớp nền có sẵn hoàn chỉnh (KHÔNG chỉnh sửa file này) minh họa đủ 4 mức
 * Access Modifiers trong Java: public, protected, package-private (default), private.
 */
public class AccountBase {

    public String publicAccountCode;
    protected String protectedRegionTag;
    String packageInternalTier;
    private String privateSecretPin;

    public AccountBase(
            String publicAccountCode,
            String protectedRegionTag,
            String packageInternalTier,
            String privateSecretPin
    ) {
        if (publicAccountCode == null || publicAccountCode.isBlank()
                || protectedRegionTag == null || protectedRegionTag.isBlank()
                || packageInternalTier == null || packageInternalTier.isBlank()
                || privateSecretPin == null || privateSecretPin.isBlank()) {
            throw new IllegalArgumentException("Account fields must not be null or blank");
        }
        this.publicAccountCode = publicAccountCode.strip();
        this.protectedRegionTag = protectedRegionTag.strip();
        this.packageInternalTier = packageInternalTier.strip();
        this.privateSecretPin = privateSecretPin.strip();
    }

    protected String buildProtectedAuditLine() {
        return "PROTECTED:" + protectedRegionTag;
    }

    String buildPackageInternalNote() {
        return "PKG:" + packageInternalTier;
    }

    public String getMaskedPin() {
        int len = privateSecretPin.length();
        if (len <= 2) {
            return "****";
        }
        return "***" + privateSecretPin.substring(len - 2);
    }

    public boolean verifyPin(String candidate) {
        return privateSecretPin.equals(candidate);
    }
}
