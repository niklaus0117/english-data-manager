package com.englishdatamanager.backend.service;

import com.englishdatamanager.backend.dto.AdminLoginRequest;
import com.englishdatamanager.backend.entity.AdminUser;
import com.englishdatamanager.backend.exception.BusinessException;
import com.englishdatamanager.backend.mapper.AdminUserMapper;
import com.englishdatamanager.backend.security.AuthUser;
import com.englishdatamanager.backend.security.PasswordCodec;
import com.englishdatamanager.backend.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordCodec passwordCodec;
    private final TokenService tokenService;
    private final AdminPermissionService adminPermissionService;

    /**
     * 处理登录请求并返回登录结果。
     */
    public Map<String, Object> login(AdminLoginRequest request) {
        AdminUser adminUser = adminUserMapper.selectOne(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<AdminUser>lambdaQuery()
                        .eq(AdminUser::getUsername, request.getUsername())
                        .last("limit 1")
        );
        if (adminUser == null || !passwordCodec.matches(request.getPassword(), adminUser.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (adminUser.getStatus() == null || adminUser.getStatus() != 1) {
            throw new BusinessException("管理员账号已禁用");
        }
        adminUser.setLastLoginAt(LocalDateTime.now());
        adminUserMapper.updateById(adminUser);

        String token = tokenService.createToken(new AuthUser(
                adminUser.getId(),
                "ADMIN",
                adminUser.getNickname(),
                adminUser.getStatus(),
                0L
        ));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("adminId", adminUser.getId());
        result.put("nickname", adminUser.getNickname());
        result.put("username", adminUser.getUsername());
        return result;
    }

    /**
     * 处理退出登录请求。
     */
    public void logout(String token) {
        tokenService.removeToken(token);
    }

    /**
     * 查询当前管理员资料和权限。
     */
    public Map<String, Object> currentProfile(Long adminUserId) {
        AdminUser adminUser = adminUserMapper.selectById(adminUserId);
        List<String> roleCodes = adminPermissionService.getRolesByAdminUserId(adminUserId)
                .stream()
                .map(item -> item.getRoleCode() == null ? "" : item.getRoleCode())
                .toList();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("admin", adminUser);
        result.put("roleCodes", roleCodes);
        result.put("menuTree", adminPermissionService.getMenuTreeByAdminUserId(adminUserId));
        return result;
    }
}
