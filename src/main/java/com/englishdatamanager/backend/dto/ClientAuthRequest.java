package com.englishdatamanager.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class ClientAuthRequest {

    @JsonAlias({"phoneNumber", "phone_number"})
    private String mobile;

    private String password;

    private String code;

    private String nickname;

    public String resolveMobile() {
        return mobile == null ? null : mobile.trim();
    }
}
