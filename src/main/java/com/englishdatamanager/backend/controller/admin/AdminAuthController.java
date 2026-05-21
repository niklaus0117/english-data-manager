package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.dto.AdminLoginRequest;
import com.englishdatamanager.backend.security.UserContext;
import com.englishdatamanager.backend.service.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Admin Auth")
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @Operation(summary = "Admin login")
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.success(adminAuthService.login(request));
    }

    @Operation(summary = "Current admin profile and permissions")
    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me() {
        return ApiResponse.success(adminAuthService.currentProfile(UserContext.getUserId()));
    }

    @Operation(summary = "Admin logout")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        Object currentToken = request.getAttribute("currentToken");
        if (currentToken != null) {
            adminAuthService.logout(currentToken.toString());
        }
        return ApiResponse.success();
    }
}
