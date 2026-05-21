package com.englishdatamanager.backend.security;

public final class UserContext {

    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(AuthUser authUser) {
        HOLDER.set(authUser);
    }

    public static AuthUser get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        AuthUser authUser = HOLDER.get();
        return authUser == null ? null : authUser.getUserId();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
