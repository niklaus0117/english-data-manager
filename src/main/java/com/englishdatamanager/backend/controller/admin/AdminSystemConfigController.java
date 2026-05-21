package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.entity.SystemConfig;
import com.englishdatamanager.backend.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system-configs")
@RequiredArgsConstructor
public class AdminSystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    public ApiResponse<List<SystemConfig>> list(@RequestParam(required = false) String configGroup) {
        return ApiResponse.success(systemConfigService.lambdaQuery()
                .eq(configGroup != null && !configGroup.isBlank(), SystemConfig::getConfigGroup, configGroup)
                .orderByAsc(SystemConfig::getId)
                .list());
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody SystemConfig systemConfig) {
        systemConfigService.save(systemConfig);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody SystemConfig systemConfig) {
        systemConfig.setId(id);
        systemConfigService.updateById(systemConfig);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        systemConfigService.removeById(id);
        return ApiResponse.success();
    }
}
