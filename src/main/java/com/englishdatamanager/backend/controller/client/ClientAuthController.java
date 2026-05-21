package com.englishdatamanager.backend.controller.client;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.dto.AppLoginRequest;
import com.englishdatamanager.backend.dto.ClientAuthRequest;
import com.englishdatamanager.backend.dto.SmsLoginRequest;
import com.englishdatamanager.backend.service.AppAuthService;
import com.englishdatamanager.backend.service.AppUserService;
import com.englishdatamanager.backend.service.SmsCodeService;
import com.englishdatamanager.backend.vo.AppUserVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ClientAuthController {

    private final AppAuthService appAuthService;
    private final SmsCodeService smsCodeService;
    private final AppUserService appUserService;

    @PostMapping("/send-code")
    public ApiResponse<Map<String, Object>> sendCode(@RequestBody ClientAuthRequest request) {
        return ApiResponse.success(smsCodeService.sendCode(request.resolveMobile()));
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody ClientAuthRequest request) {
        if (request.getCode() != null && !request.getCode().isBlank()) {
            SmsLoginRequest smsLoginRequest = new SmsLoginRequest();
            smsLoginRequest.setMobile(request.resolveMobile());
            smsLoginRequest.setCode(request.getCode());
            smsLoginRequest.setNickname(request.getNickname());
            return ApiResponse.success(appAuthService.loginBySms(smsLoginRequest));
        }
        AppLoginRequest loginRequest = new AppLoginRequest();
        loginRequest.setMobile(request.resolveMobile());
        loginRequest.setPassword(request.getPassword());
        return ApiResponse.success(appAuthService.login(loginRequest));
    }

    @GetMapping("/me")
    public ApiResponse<AppUserVo> me() {
        return ApiResponse.success(appUserService.currentUserCard());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        Object currentToken = request.getAttribute("currentToken");
        if (currentToken != null) {
            appAuthService.logout(currentToken.toString());
        }
        return ApiResponse.success();
    }
}
