package com.englishdatamanager.backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.englishdatamanager.backend.entity.AppUser;
import com.englishdatamanager.backend.entity.PermissionGroup;
import com.englishdatamanager.backend.exception.BusinessException;
import com.englishdatamanager.backend.mapper.AppUserMapper;
import com.englishdatamanager.backend.security.UserContext;
import com.englishdatamanager.backend.vo.AppUserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppUserService extends ServiceImpl<AppUserMapper, AppUser> {

    private final PermissionGroupService permissionGroupService;

    public AppUserVo currentUserCard() {
        AppUser user = getCurrentActiveUser();
        AppUserVo vo = new AppUserVo();
        vo.setId(String.valueOf(user.getId()));
        vo.setPhoneNumber(maskMobile(user.getMobile()));
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatarUrl());
        return vo;
    }

    public Map<String, Object> currentUserGroup() {
        AppUser user = getCurrentActiveUser();
        PermissionGroup group = permissionGroupService.getById(user.getPermissionGroupId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("group", group);
        result.put("features", group == null || group.getFeatureCodes() == null || group.getFeatureCodes().isBlank()
                ? List.of()
                : Arrays.stream(group.getFeatureCodes().split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList());
        return result;
    }

    public AppUser getCurrentActiveUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("please login first");
        }
        AppUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("user not found");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("current user is disabled");
        }
        return user;
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }
}
