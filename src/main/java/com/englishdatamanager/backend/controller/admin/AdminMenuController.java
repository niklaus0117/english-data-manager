package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.entity.AdminMenu;
import com.englishdatamanager.backend.service.AdminMenuService;
import com.englishdatamanager.backend.service.AdminPermissionService;
import com.englishdatamanager.backend.service.AdminRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Menu")
@RestController
@RequestMapping("/api/admin/menus")
@RequiredArgsConstructor
public class AdminMenuController {

    private final AdminMenuService adminMenuService;
    private final AdminRoleMenuService adminRoleMenuService;
    private final AdminPermissionService adminPermissionService;

    /**
     * 查询数据列表。
     */
    @Operation(summary = "Menu list")
    @GetMapping
    public ApiResponse<List<AdminMenu>> list() {
        return ApiResponse.success(adminMenuService.lambdaQuery()
                .orderByAsc(AdminMenu::getSortNo)
                .orderByAsc(AdminMenu::getId)
                .list());
    }

    /**
     * 查询树形结构数据。
     */
    @Operation(summary = "Menu tree")
    @GetMapping("/tree")
    public ApiResponse<?> tree() {
        return ApiResponse.success(adminPermissionService.getAllMenuTree());
    }

    /**
     * 创建一条业务数据。
     */
    @Operation(summary = "Create menu")
    @PostMapping
    public ApiResponse<Void> create(@RequestBody AdminMenu menu) {
        adminMenuService.save(menu);
        return ApiResponse.success();
    }

    /**
     * 更新指定业务数据。
     */
    @Operation(summary = "Update menu")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody AdminMenu menu) {
        menu.setId(id);
        adminMenuService.updateById(menu);
        return ApiResponse.success();
    }

    /**
     * 删除指定业务数据。
     */
    @Operation(summary = "Delete menu")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminMenuService.removeById(id);
        adminRoleMenuService.lambdaUpdate().eq(com.englishdatamanager.backend.entity.AdminRoleMenu::getMenuId, id).remove();
        return ApiResponse.success();
    }
}
