package com.englishdatamanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmsLoginRequest {

    @NotBlank(message = "mobile is required")
    private String mobile;

    @NotBlank(message = "code is required")
    private String code;

    private String nickname;
}
