package com.englishdatamanager.backend.service;

import com.englishdatamanager.backend.dto.AppLoginRequest;
import com.englishdatamanager.backend.dto.QuickLoginRequest;
import com.englishdatamanager.backend.dto.SmsLoginRequest;
import com.englishdatamanager.backend.entity.AppUser;
import com.englishdatamanager.backend.exception.BusinessException;
import com.englishdatamanager.backend.mapper.AppUserMapper;
import com.englishdatamanager.backend.security.AuthUser;
import com.englishdatamanager.backend.security.PasswordCodec;
import com.englishdatamanager.backend.security.TokenService;
import com.englishdatamanager.backend.vo.AppLoginVo;
import com.englishdatamanager.backend.vo.AppUserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppAuthService {

    private final AppUserMapper appUserMapper;
    private final PasswordCodec passwordCodec;
    private final TokenService tokenService;
    private final SmsCodeService smsCodeService;

    public Map<String, Object> login(AppLoginRequest request) {
        AppUser appUser = appUserMapper.selectOne(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<AppUser>lambdaQuery()
                        .eq(AppUser::getMobile, request.getMobile())
                        .last("limit 1")
        );
        if (appUser == null || !passwordCodec.matches(request.getPassword(), appUser.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }
        if (appUser.getStatus() == null || appUser.getStatus() != 1) {
            throw new BusinessException("当前用户不可用");
        }
        appUser.setLastLoginAt(LocalDateTime.now());
        appUserMapper.updateById(appUser);

        String token = tokenService.createToken(new AuthUser(
                appUser.getId(),
                "APP",
                appUser.getNickname(),
                appUser.getStatus(),
                appUser.getPermissionGroupId()
        ));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("userId", appUser.getId());
        result.put("nickname", appUser.getNickname());
        result.put("mobile", appUser.getMobile());
        result.put("groupId", appUser.getPermissionGroupId());
        return result;
    }

    public AppLoginVo quickLogin(QuickLoginRequest request) {
        String normalizedMobile = normalizeMobile(request.resolveMobile());
        AppUser appUser = appUserMapper.selectOne(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<AppUser>lambdaQuery()
                        .eq(AppUser::getMobile, normalizedMobile)
                        .last("limit 1")
        );
        if (appUser == null) {
            appUser = new AppUser();
            appUser.setMobile(normalizedMobile);
            appUser.setPassword(passwordCodec.encode("user123456"));
            appUser.setNickname(request.getNickname() == null || request.getNickname().isBlank()
                    ? "User" + normalizedMobile.substring(Math.max(0, normalizedMobile.length() - 4))
                    : request.getNickname());
            appUser.setAvatarUrl("https://picsum.photos/100/100");
            appUser.setStatus(1);
            appUser.setPermissionGroupId(1L);
            appUser.setLastLoginAt(LocalDateTime.now());
            appUserMapper.insert(appUser);
        } else {
            if (appUser.getStatus() == null || appUser.getStatus() != 1) {
                throw new BusinessException("当前用户不可用");
            }
            appUser.setLastLoginAt(LocalDateTime.now());
            appUserMapper.updateById(appUser);
        }

        String token = tokenService.createToken(new AuthUser(
                appUser.getId(),
                "APP",
                appUser.getNickname(),
                appUser.getStatus(),
                appUser.getPermissionGroupId()
        ));
        AppUserVo userVo = new AppUserVo();
        userVo.setId(String.valueOf(appUser.getId()));
        userVo.setPhoneNumber(maskMobile(normalizedMobile));
        userVo.setNickname(appUser.getNickname());
        userVo.setAvatar(appUser.getAvatarUrl());

        AppLoginVo result = new AppLoginVo();
        result.setToken(token);
        result.setUser(userVo);
        return result;
    }

    public void logout(String token) {
        tokenService.removeToken(token);
    }

    public Map<String, Object> loginBySms(SmsLoginRequest request) {
        smsCodeService.verifyCode(request.getMobile(), request.getCode());
        AppUser appUser = appUserMapper.selectOne(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<AppUser>lambdaQuery()
                        .eq(AppUser::getMobile, request.getMobile())
                        .last("limit 1")
        );
        if (appUser == null) {
            appUser = new AppUser();
            appUser.setMobile(request.getMobile());
            appUser.setPassword(passwordCodec.encode("user123456"));
            appUser.setNickname(request.getNickname() == null || request.getNickname().isBlank()
                    ? "User" + request.getMobile().substring(Math.max(0, request.getMobile().length() - 4))
                    : request.getNickname());
            appUser.setStatus(1);
            appUser.setPermissionGroupId(1L);
            appUser.setLastLoginAt(LocalDateTime.now());
            appUserMapper.insert(appUser);
        } else {
            if (appUser.getStatus() == null || appUser.getStatus() != 1) {
                throw new BusinessException("当前用户不可用");
            }
            appUser.setLastLoginAt(LocalDateTime.now());
            appUserMapper.updateById(appUser);
        }
        String token = tokenService.createToken(new AuthUser(
                appUser.getId(),
                "APP",
                appUser.getNickname(),
                appUser.getStatus(),
                appUser.getPermissionGroupId()
        ));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("userId", appUser.getId());
        result.put("nickname", appUser.getNickname());
        result.put("mobile", appUser.getMobile());
        result.put("groupId", appUser.getPermissionGroupId());
        result.put("loginType", "sms");
        return result;
    }

    private String normalizeMobile(String mobile) {
        if (mobile == null) {
            return null;
        }
        String trimmed = mobile.trim();
        if (trimmed.contains("*")) {
            return "13800008888";
        }
        return trimmed;
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }
}
