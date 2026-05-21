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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isPublicApi(request)) {
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

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean unauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.unauthorized(message)));
        return false;
    }

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

    private boolean isPublicApi(HttpServletRequest request) {
        String uri = request.getRequestURI();
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

    private boolean isClientAppApi(String uri) {
        return uri.startsWith("/api/auth/")
                || uri.startsWith("/api/courses")
                || uri.startsWith("/api/lessons")
                || uri.startsWith("/api/user")
                || uri.startsWith("/api/dict")
                || uri.startsWith("/api/ai");
    }
}
