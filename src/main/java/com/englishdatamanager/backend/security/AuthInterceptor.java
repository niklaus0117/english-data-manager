package com.englishdatamanager.backend.security;

import com.englishdatamanager.backend.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    /**
     * 在请求进入控制器前完成登录态和用户类型校验。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isPublicApi(request)) {
            // 公开接口不强制登录，但允许携带 token 时自动注入用户上下文。
            return tryAuthenticateOptional(request);
        }
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            return unauthorized(response, "missing token");
        }
        String token = tokenService.resolveToken(authorization);
        AuthUser authUser = tokenService.parseToken(token);
        if (authUser == null) {
            return unauthorized(response, "invalid token");
        }
        String uri = request.getRequestURI();
        // 后台接口和 App 接口使用同一套 token 服务，但必须校验用户类型，避免跨端 token 复用。
        if (uri.startsWith("/api/admin/") && !"ADMIN".equals(authUser.getUserType())) {
            return unauthorized(response, "admin token required");
        }
        if ((uri.startsWith("/api/app/") || isClientAppApi(uri)) && !"APP".equals(authUser.getUserType())) {
            return unauthorized(response, "app token required");
        }
        request.setAttribute("currentToken", token);
        UserContext.set(authUser);
        return true;
    }

    /**
     * 请求结束后清理当前线程中的用户上下文。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    /**
     * 写入或构造未授权响应。
     */
    private boolean unauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.unauthorized(message)));
        return false;
    }

    /**
     * 公开接口携带 token 时尝试解析登录用户。
     */
    private boolean tryAuthenticateOptional(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            return true;
        }
        String token = tokenService.resolveToken(authorization);
        AuthUser authUser = tokenService.parseToken(token);
        if (authUser == null) {
            return true;
        }
        request.setAttribute("currentToken", token);
        UserContext.set(authUser);
        return true;
    }

    /**
     * 判断当前请求是否属于免登录接口。
     */
    private boolean isPublicApi(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // 兼容旧版客户端接口和新版 /api/app 接口，公开读接口在这里集中声明。
        if ("/api/auth/send-code".equals(uri) || "/api/auth/login".equals(uri)) {
            return true;
        }
        if ("/api/dict/word".equals(uri)) {
            return true;
        }
        if ("GET".equalsIgnoreCase(request.getMethod())
                && ("/api/courses".equals(uri)
                || "/api/courses/videos".equals(uri)
                || uri.matches("^/api/courses/\\d+$")
                || uri.matches("^/api/courses/\\d+/lessons$")
                || uri.matches("^/api/lessons/\\d+$")
                || uri.matches("^/api/lessons/\\d+/transcripts$"))) {
            return true;
        }
        if ("/api/app/auth/quick-login".equals(uri)) {
            return true;
        }
        if ("/api/app/home/index".equals(uri)) {
            return true;
        }
        if ("/api/app/courses/recommend".equals(uri)) {
            return true;
        }
        if (uri.startsWith("/api/app/courses/daily-reading")) {
            return true;
        }
        return "GET".equalsIgnoreCase(request.getMethod())
                && (uri.matches("^/api/app/courses/\\d+$")
                || uri.matches("^/api/app/courses/\\d+/lessons$"));
    }

    /**
     * 判断当前 URI 是否属于客户端兼容接口。
     */
    private boolean isClientAppApi(String uri) {
        return uri.startsWith("/api/auth/")
                || uri.startsWith("/api/courses")
                || uri.startsWith("/api/lessons")
                || uri.startsWith("/api/user")
                || uri.startsWith("/api/dict")
                || uri.startsWith("/api/ai");
    }
}
