package com.englishdatamanager.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_user")
public class AppUser extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String mobile;
    private String password;
    private String nickname;
    private String avatarUrl;
    private Integer status;
    private Long permissionGroupId;
    private LocalDateTime vipExpireAt;
    private LocalDateTime lastLoginAt;
}
