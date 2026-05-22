package com.englishdatamanager.backend.security;

public final class UserContext {

    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<>();

    /**
     * 初始化 UserContext 实例。
     */
    private UserContext() {
    }

    /**
     * 执行 set 安全相关逻辑。
     */
    public static void set(AuthUser authUser) {
        HOLDER.set(authUser);
    }

    /**
     * 执行 get 安全相关逻辑。
     */
    public static AuthUser get() {
        return HOLDER.get();
    }

    /**
     * 执行 getUserId 安全相关逻辑。
     */
    public static Long getUserId() {
        AuthUser authUser = HOLDER.get();
        return authUser == null ? null : authUser.getUserId();
    }

    /**
     * 执行 clear 安全相关逻辑。
     */
    public static void clear() {
        HOLDER.remove();
    }
}
