package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.service.AppUserService;
import com.englishdatamanager.backend.service.CategoryService;
import com.englishdatamanager.backend.service.UserOrderService;
import com.englishdatamanager.backend.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final CategoryService categoryService;
    private final VideoService videoService;
    private final AppUserService appUserService;
    private final UserOrderService userOrderService;

    /**
     * 查询后台首页概览数据。
     */
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("categoryCount", categoryService.count());
        result.put("videoCount", videoService.count());
        result.put("userCount", appUserService.count());
        result.put("orderCount", userOrderService.count());
        return ApiResponse.success(result);
    }
}
