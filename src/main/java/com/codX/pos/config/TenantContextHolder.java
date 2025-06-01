package com.codX.pos.config;

public class TenantContextHolder {
    private static final ThreadLocal<String> tenantId = new ThreadLocal<>();
    private static final ThreadLocal<String> branchId = new ThreadLocal<>();

    public static void setTenantId(String tenant) {
        tenantId.set(tenant);
    }

    public static String getTenantId() {
        return tenantId.get();
    }

    public static void clear() {
        tenantId.remove();
        branchId.remove();
    }

    public static void setBranchId(String branch) {
        branchId.set(branch);
    }

    public static String getBranchId() {
        return branchId.get();
    }
}