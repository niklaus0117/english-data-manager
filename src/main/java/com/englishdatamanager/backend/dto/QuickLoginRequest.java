package com.englishdatamanager.backend.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class QuickLoginRequest {

    private String mobile;

    private String phoneNumber;

    private String nickname;

    @AssertTrue(message = "mobile or phoneNumber is required")
    public boolean isValidMobileInput() {
        return (mobile != null && !mobile.isBlank()) || (phoneNumber != null && !phoneNumber.isBlank());
    }

    public String resolveMobile() {
        return mobile != null && !mobile.isBlank() ? mobile : phoneNumber;
    }
}
