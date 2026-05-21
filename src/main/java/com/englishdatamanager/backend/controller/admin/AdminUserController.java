package com.englishdatamanager.backend.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.common.PageResponse;
import com.englishdatamanager.backend.entity.AppUser;
import com.englishdatamanager.backend.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AppUserService appUserService;

    @GetMapping
    public ApiResponse<PageResponse<AppUser>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<AppUser> page = appUserService.lambdaQuery()
                .eq(status != null, AppUser::getStatus, status)
                .and(keyword != null && !keyword.isBlank(),
                        wrapper -> wrapper.like(AppUser::getMobile, keyword).or().like(AppUser::getNickname, keyword))
                .orderByDesc(AppUser::getId)
                .page(new Page<>(current, size));
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/{id}")
    public ApiResponse<AppUser> detail(@PathVariable Long id) {
        return ApiResponse.success(appUserService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody AppUser appUser) {
        appUser.setId(id);
        appUserService.updateById(appUser);
        return ApiResponse.success();
    }
}
