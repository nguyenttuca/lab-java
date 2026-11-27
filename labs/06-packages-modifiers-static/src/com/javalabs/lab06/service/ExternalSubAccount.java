package com.javalabs.lab06.service;

import com.javalabs.lab06.model.AccountBase;

/**
 * Lớp con nằm KHÁC PACKAGE (`service` vs `model`), kế thừa `AccountBase`.
 * Nhấn mạnh quy tắc `protected` của Java (JLS §6.6.2):
 * - Khác package KHÔNG thấy `package-private` (`packageInternalTier`).
 * - Chỉ truy cập được thành viên `protected` thông qua `this` hoặc tham chiếu kiểu lớp con (`ExternalSubAccount`),
 *   KHÔNG truy cập được `otherBase.protectedRegionTag` qua tham chiếu kiểu lớp cha `AccountBase`.
 */
public class ExternalSubAccount extends AccountBase {

    public ExternalSubAccount(
            String accountCode,
            String regionTag,
            String internalTier,
            String secretPin
    ) {
        super(accountCode, regionTag, internalTier, secretPin);
    }

    /**
     * Đọc các thành viên truy cập được trên `this` (public & protected) và getMaskedPin().
     * Định dạng trả về: "code=%s|region=%s|audit=%s|pin=%s"
     */
    public String readOwnAccessibleDetails() {
        // TODO: Truy cập `publicAccountCode`, `protectedRegionTag`, `buildProtectedAuditLine()`, `getMaskedPin()`
        throw new UnsupportedOperationException("TODO: implement readOwnAccessibleDetails");
    }

    /**
     * Cập nhật trường `protectedRegionTag` trên chính đối tượng hiện tại (`this`).
     *
     * @throws IllegalArgumentException nếu newRegion null hoặc blank
     */
    public void updateOwnProtectedRegion(String newRegion) {
        // TODO: Validate newRegion và cập nhật `this.protectedRegionTag = newRegion.strip();`
        throw new UnsupportedOperationException("TODO: implement updateOwnProtectedRegion");
    }

    /**
     * Đọc trực tiếp `peer.protectedRegionTag` từ một đối tượng cùng kiểu lớp con `ExternalSubAccount`.
     * (Hợp lệ trong Java dù khác package với AccountBase vì kiểu tĩnh của `peer` là ExternalSubAccount!)
     *
     * @throws IllegalArgumentException nếu peer là null
     */
    public String readPeerSubAccountRegion(ExternalSubAccount peer) {
        // TODO: Validate peer != null và trả về peer.protectedRegionTag
        throw new UnsupportedOperationException("TODO: implement readPeerSubAccountRegion");
    }

    /**
     * Đọc thông tin từ một tham chiếu kiểu lớp cha `AccountBase` ở khác package.
     * Lưu ý: Ở đây trình biên dịch CẤM `otherBase.protectedRegionTag` và `otherBase.packageInternalTier`!
     * Chỉ có thể dùng thành viên `public`. Trả về: "code=%s|pin=%s"
     *
     * @throws IllegalArgumentException nếu otherBase là null
     */
    public String readOtherBaseViaPublicOnly(AccountBase otherBase) {
        // TODO: Validate otherBase != null và chỉ đọc `otherBase.publicAccountCode` + `otherBase.getMaskedPin()`
        throw new UnsupportedOperationException("TODO: implement readOtherBaseViaPublicOnly");
    }
}
