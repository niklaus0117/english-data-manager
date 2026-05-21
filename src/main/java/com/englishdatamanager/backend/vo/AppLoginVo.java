package com.englishdatamanager.backend.vo;

import lombok.Data;

@Data
public class AppLoginVo {

    private String token;
    private AppUserVo user;
}
