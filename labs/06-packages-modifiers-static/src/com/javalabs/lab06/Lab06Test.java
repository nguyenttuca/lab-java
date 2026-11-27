package com.javalabs.lab06;

import com.javalabs.lab06.config.AppConfig;
import com.javalabs.lab06.model.AccountBase;
import com.javalabs.lab06.model.SamePackageAuditor;
import com.javalabs.lab06.service.ExternalSubAccount;
import java.util.List;

/**
 * Driver test tự động cho Lab 06 (dùng custom assertion helpers, không phụ thuộc -ea).
 */
public class Lab06Test {

    private static int passed = 0;
    private static int failed = 0;

    @FunctionalInterface
    private interface TestCase {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        System.out.println("=== Lab 06: Packages, Access Modifiers & static vs Instance Tests ===");

        runTest("SamePackageAuditor: public, protected & package-private in same package", () -> {
            AccountBase acc = new AccountBase("ACC-01", "VN-SOUTH", "GOLD", "9989");
            checkEquals("code=ACC-01|region=VN-SOUTH|tier=GOLD|pin=***89",
                    SamePackageAuditor.inspectAccountInSamePackage(acc),
                    "Same package must inspect public, protected, package-private and masked pin");

            SamePackageAuditor.upgradePackageTier(acc, "  PLATINUM  ");
            checkEquals("code=ACC-01|region=VN-SOUTH|tier=PLATINUM|pin=***89",
                    SamePackageAuditor.inspectAccountInSamePackage(acc),
                    "Package-private tier updated in same package");

            checkThrows(IllegalArgumentException.class,
                    () -> SamePackageAuditor.inspectAccountInSamePackage(null),
                    "Null account should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> SamePackageAuditor.upgradePackageTier(acc, " "),
                    "Blank tier should throw");
        });

        runTest("ExternalSubAccount: cross-package protected rules (this, peer vs base)", () -> {
            ExternalSubAccount sub1 = new ExternalSubAccount("ACC-02", "SG", "SILVER", "12345");
            ExternalSubAccount sub2 = new ExternalSubAccount("ACC-03", "JP", "BRONZE", "67890");
            AccountBase base = new AccountBase("ACC-99", "US", "VIP", "54321");

            checkEquals("code=ACC-02|region=SG|audit=PROTECTED:SG|pin=***45",
                    sub1.readOwnAccessibleDetails(),
                    "Subclass reads own public and protected members");

            sub1.updateOwnProtectedRegion("  EU-WEST  ");
            checkEquals("code=ACC-02|region=EU-WEST|audit=PROTECTED:EU-WEST|pin=***45",
                    sub1.readOwnAccessibleDetails(),
                    "Updated protected region via this");

            checkEquals("JP", sub1.readPeerSubAccountRegion(sub2),
                    "Subclass can read protected field on peer of same subclass type");

            checkEquals("code=ACC-99|pin=***21",
                    sub1.readOtherBaseViaPublicOnly(base),
                    "Cross-package reference typed as AccountBase only exposes public members");

            checkThrows(IllegalArgumentException.class,
                    () -> sub1.readPeerSubAccountRegion(null),
                    "Null peer should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> sub1.readOtherBaseViaPublicOnly(null),
                    "Null base should throw");
        });

        runTest("AppConfig: static block -> instance block -> constructor order", () -> {
            checkEquals("PRODUCTION", AppConfig.getGlobalEnvironment(),
                    "Static block initializes globalEnvironment before any instance exists");
            checkEquals(0, AppConfig.getTotalInstancesCreated(),
                    "Initial instance count must be 0");
            checkEquals(List.of("STATIC_BLOCK"), AppConfig.getInitOrderTrace(),
                    "Static block runs once on class load");

            AppConfig first = new AppConfig("  payment-api  ");
            checkEquals(1, first.getInstanceId(), "First instance id must be 1");
            checkEquals("payment-api", first.getServiceName(), "Trimmed serviceName");
            checkEquals("PRODUCTION", first.getEnvironment(), "Inherits globalEnvironment");
            checkEquals(List.of("STATIC_BLOCK", "INSTANCE_BLOCK", "CONSTRUCTOR"),
                    AppConfig.getInitOrderTrace(),
                    "First instantiation order");

            AppConfig second = AppConfig.createWithEnvironment("order-api", " staging ");
            checkEquals(2, second.getInstanceId(), "Second instance id increments to 2");
            checkEquals("STAGING", second.getEnvironment(), "Factory overrides instance environment");
            checkEquals("PRODUCTION", first.getEnvironment(), "First instance stays PRODUCTION");
            checkEquals(2, AppConfig.getTotalInstancesCreated(), "Static counter equals 2");
            checkEquals(List.of("STATIC_BLOCK", "INSTANCE_BLOCK", "CONSTRUCTOR", "INSTANCE_BLOCK", "CONSTRUCTOR"),
                    AppConfig.getInitOrderTrace(),
                    "Static block does NOT re-run on second instantiation");
        });

        runTest("AppConfig: static vs instance mutation & defensive trace copy", () -> {
            int before = AppConfig.getTotalInstancesCreated();
            AppConfig svc = new AppConfig("auth-api");
            checkEquals(before + 1, AppConfig.getTotalInstancesCreated(), "Counter increments by 1");

            AppConfig.setGlobalEnvironment(" sandbox ");
            checkEquals("SANDBOX", AppConfig.getGlobalEnvironment(), "Global env updated");
            checkEquals("[#" + svc.getInstanceId() + "] auth-api (PRODUCTION | global=SANDBOX)",
                    svc.formatInstanceSummary(),
                    "Instance env remains PRODUCTION while global env reflects SANDBOX");

            List<String> trace = AppConfig.getInitOrderTrace();
            checkThrows(UnsupportedOperationException.class,
                    () -> trace.add("TAMPER"),
                    "getInitOrderTrace must return unmodifiable snapshot");

            checkThrows(IllegalArgumentException.class,
                    () -> new AppConfig(" "),
                    "Blank serviceName should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> AppConfig.createWithEnvironment("api", null),
                    "Null customEnv should throw");
            checkThrows(IllegalArgumentException.class,
                    () -> AppConfig.setGlobalEnvironment(""),
                    "Empty globalEnv should throw");
        });

        System.out.printf("=== Kết quả Lab 06: %d PASS / %d FAIL ===%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void runTest(String name, TestCase testCase) {
        try {
            testCase.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    private static void checkEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " | Expected: <" + expected + "> but got: <" + actual + ">");
        }
    }

    private static void checkThrows(Class<? extends Throwable> expectedType, TestCase action, String message) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }
            throw new AssertionError(message + " | Expected " + expectedType.getSimpleName()
                    + " but got " + thrown.getClass().getSimpleName() + ": " + thrown.getMessage());
        }
        throw new AssertionError(message + " | Expected " + expectedType.getSimpleName() + " but nothing was thrown");
    }
}
