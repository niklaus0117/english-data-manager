package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.dto.AdminAssignRolesRequest;
import com.englishdatamanager.backend.dto.RoleMenuAssignRequest;
import com.englishdatamanager.backend.entity.AdminRole;
import com.englishdatamanager.backend.entity.AdminRoleMenu;
import com.englishdatamanager.backend.entity.AdminUserRole;
import com.englishdatamanager.backend.service.AdminPermissionService;
import com.englishdatamanager.backend.service.AdminRoleMenuService;
import com.englishdatamanager.backend.service.AdminRoleService;
import com.englishdatamanager.backend.service.AdminUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin Role")
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;
    private final AdminUserRoleService adminUserRoleService;
    private final AdminRoleMenuService adminRoleMenuService;
    private final AdminPermissionService adminPermissionService;

    /**
     * 查询数据列表。
     */
    @Operation(summary = "Role list")
    @GetMapping
    public ApiResponse<List<AdminRole>> list() {
        return ApiResponse.success(adminRoleService.lambdaQuery().orderByDesc(AdminRole::getId).list());
    }

    /**
     * 创建一条业务数据。
     */
    @Operation(summary = "Create role")
    @PostMapping
    public ApiResponse<Void> create(@RequestBody AdminRole role) {
        adminRoleService.save(role);
        return ApiResponse.success();
    }

    /**
     * 更新指定业务数据。
     */
    @Operation(summary = "Update role")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody AdminRole role) {
        role.setId(id);
        adminRoleService.updateById(role);
        return ApiResponse.success();
    }

    /**
     * 删除指定业务数据。
     */
    @Operation(summary = "Delete role")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adminRoleService.removeById(id);
        adminUserRoleService.lambdaUpdate().eq(AdminUserRole::getRoleId, id).remove();
        adminRoleMenuService.lambdaUpdate().eq(AdminRoleMenu::getRoleId, id).remove();
        return ApiResponse.success();
    }

    /**
     * 处理 assignUsers 接口请求。
     */
    @Operation(summary = "Assign roles to admin user")
    @PostMapping("/assign-users")
    public ApiResponse<Void> assignUsers(@RequestBody AdminAssignRolesRequest request) {
        adminUserRoleService.lambdaUpdate()
                .eq(AdminUserRole::getAdminUserId, request.getAdminUserId())
                .remove();
        if (request.getRoleIds() != null) {
            for (Long roleId : request.getRoleIds()) {
                AdminUserRole relation = new AdminUserRole();
                relation.setAdminUserId(request.getAdminUserId());
                relation.setRoleId(roleId);
                adminUserRoleService.save(relation);
            }
        }
        return ApiResponse.success();
    }

    /**
     * 处理 assignMenus 接口请求。
     */
    @Operation(summary = "Assign menus to role")
    @PostMapping("/assign-menus")
    public ApiResponse<Void> assignMenus(@RequestBody RoleMenuAssignRequest request) {
        adminRoleMenuService.lambdaUpdate()
                .eq(AdminRoleMenu::getRoleId, request.getRoleId())
                .remove();
        if (request.getMenuIds() != null) {
            for (Long menuId : request.getMenuIds()) {
                AdminRoleMenu relation = new AdminRoleMenu();
                relation.setRoleId(request.getRoleId());
                relation.setMenuId(menuId);
                adminRoleMenuService.save(relation);
            }
        }
        return ApiResponse.success();
    }

    /**
     * 处理 permissions 接口请求。
     */
    @Operation(summary = "Role permission detail")
    @GetMapping("/{id}/permissions")
    public ApiResponse<Map<String, Object>> permissions(@PathVariable Long id) {
        List<Long> menuIds = adminRoleMenuService.lambdaQuery()
                .eq(AdminRoleMenu::getRoleId, id)
                .list()
                .stream()
                .map(AdminRoleMenu::getMenuId)
                .toList();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("menuIds", menuIds);
        result.put("menuTree", adminPermissionService.getAllMenuTree());
        return ApiResponse.success(result);
    }
}
