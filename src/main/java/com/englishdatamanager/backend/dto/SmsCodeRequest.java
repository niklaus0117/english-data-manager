package com.englishdatamanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmsCodeRequest {

    @NotBlank(message = "mobile is required")
    private String mobile;
}
