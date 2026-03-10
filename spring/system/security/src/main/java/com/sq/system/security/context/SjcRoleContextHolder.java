package com.sq.system.security.context;

import com.sq.system.security.model.SjcUserRoleInfo;

public class SjcRoleContextHolder {
    private static final ThreadLocal<SjcUserRoleInfo> CONTEXT = new ThreadLocal<>();

    public static void set(SjcUserRoleInfo roleInfo) {
        CONTEXT.set(roleInfo);
    }

    public static SjcUserRoleInfo get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
