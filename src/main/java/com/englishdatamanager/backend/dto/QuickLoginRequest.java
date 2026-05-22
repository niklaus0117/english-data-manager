package com.englishdatamanager.backend.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class QuickLoginRequest {

    private String mobile;

    private String phoneNumber;

    private String nickname;

    /**
     * 校验手机号输入是否存在。
     */
    @AssertTrue(message = "mobile or phoneNumber is required")
    public boolean isValidMobileInput() {
        return (mobile != null && !mobile.isBlank()) || (phoneNumber != null && !phoneNumber.isBlank());
    }

    /**
     * 解析请求中的手机号字段。
     */
    public String resolveMobile() {
        return mobile != null && !mobile.isBlank() ? mobile : phoneNumber;
    }
}
