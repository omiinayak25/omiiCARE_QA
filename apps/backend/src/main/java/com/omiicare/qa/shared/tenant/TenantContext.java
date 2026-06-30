package com.omiicare.qa.shared.tenant;

/**
 * Holds the tenant identifier for the current request on a thread-local, set by the security layer
 * once the caller is authenticated. Every tenant-scoped query and audit record reads the active
 * tenant from here, so isolation is enforced consistently rather than passed around by hand.
 */
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
