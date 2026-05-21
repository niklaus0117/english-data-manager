package com.englishdatamanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppLoginRequest {

    @NotBlank(message = "mobile is required")
    private String mobile;

    @NotBlank(message = "password is required")
    private String password;
}
