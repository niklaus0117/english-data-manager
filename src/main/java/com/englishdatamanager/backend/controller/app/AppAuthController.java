package com.englishdatamanager.backend.controller.app;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.dto.AppLoginRequest;
import com.englishdatamanager.backend.dto.QuickLoginRequest;
import com.englishdatamanager.backend.dto.SmsCodeRequest;
import com.englishdatamanager.backend.dto.SmsLoginRequest;
import com.englishdatamanager.backend.service.SmsCodeService;
import com.englishdatamanager.backend.service.AppAuthService;
import com.englishdatamanager.backend.vo.AppLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "App Auth")
@RestController
@RequestMapping("/api/app/auth")
@RequiredArgsConstructor
public class AppAuthController {

    private final AppAuthService appAuthService;
    private final SmsCodeService smsCodeService;

    @Operation(summary = "App login")
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody AppLoginRequest request) {
        return ApiResponse.success(appAuthService.login(request));
    }

    @Operation(summary = "Quick login for mobile app")
    @PostMapping("/quick-login")
    public ApiResponse<AppLoginVo> quickLogin(@Valid @RequestBody QuickLoginRequest request) {
        return ApiResponse.success(appAuthService.quickLogin(request));
    }

    @Operation(summary = "Send SMS verify code")
    @PostMapping("/sms-code")
    public ApiResponse<Map<String, Object>> sendSmsCode(@Valid @RequestBody SmsCodeRequest request) {
        return ApiResponse.success(smsCodeService.sendCode(request.getMobile()));
    }

    @Operation(summary = "App login by SMS code")
    @PostMapping("/sms-login")
    public ApiResponse<Map<String, Object>> smsLogin(@Valid @RequestBody SmsLoginRequest request) {
        return ApiResponse.success(appAuthService.loginBySms(request));
    }

    @Operation(summary = "App logout")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        Object currentToken = request.getAttribute("currentToken");
        if (currentToken != null) {
            appAuthService.logout(currentToken.toString());
        }
        return ApiResponse.success();
    }
}
