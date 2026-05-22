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

    /**
     * 查询数据列表。
     */
    @GetMapping
    public ApiResponse<List<PermissionGroup>> list() {
        return ApiResponse.success(permissionGroupService.lambdaQuery().orderByDesc(PermissionGroup::getId).list());
    }

    /**
     * 创建一条业务数据。
     */
    @PostMapping
    public ApiResponse<Void> create(@RequestBody PermissionGroup group) {
        permissionGroupService.save(group);
        return ApiResponse.success();
    }

    /**
     * 更新指定业务数据。
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody PermissionGroup group) {
        group.setId(id);
        permissionGroupService.updateById(group);
        return ApiResponse.success();
    }

    /**
     * 删除指定业务数据。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionGroupService.removeById(id);
        return ApiResponse.success();
    }
}
