package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.entity.PermissionGroup;
import com.englishdatamanager.backend.service.PermissionGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/groups")
@RequiredArgsConstructor
public class AdminPermissionGroupController {

    private final PermissionGroupService permissionGroupService;

    @GetMapping
    public ApiResponse<List<PermissionGroup>> list() {
        return ApiResponse.success(permissionGroupService.lambdaQuery().orderByDesc(PermissionGroup::getId).list());
    }

    @PostMapping
    public ApiResponse<Void> create(@RequestBody PermissionGroup group) {
        permissionGroupService.save(group);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody PermissionGroup group) {
        group.setId(id);
        permissionGroupService.updateById(group);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionGroupService.removeById(id);
        return ApiResponse.success();
    }
}
