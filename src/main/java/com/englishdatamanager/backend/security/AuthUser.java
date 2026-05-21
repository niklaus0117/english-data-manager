package com.englishdatamanager.backend.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {

    private Long userId;
    private String userType;
    private String displayName;
    private Integer status;
    private Long groupId;
}
