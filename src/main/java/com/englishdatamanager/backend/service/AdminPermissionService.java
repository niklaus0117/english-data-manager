package com.englishdatamanager.backend.service;

import com.englishdatamanager.backend.entity.AdminMenu;
import com.englishdatamanager.backend.entity.AdminRole;
import com.englishdatamanager.backend.entity.AdminRoleMenu;
import com.englishdatamanager.backend.entity.AdminUserRole;
import com.englishdatamanager.backend.vo.AdminMenuTreeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminPermissionService {

    private final AdminUserRoleService adminUserRoleService;
    private final AdminRoleService adminRoleService;
    private final AdminRoleMenuService adminRoleMenuService;
    private final AdminMenuService adminMenuService;

    /**
     * 查询管理员拥有的角色列表。
     */
    public List<AdminRole> getRolesByAdminUserId(Long adminUserId) {
        List<Long> roleIds = adminUserRoleService.lambdaQuery()
                .eq(AdminUserRole::getAdminUserId, adminUserId)
                .list()
                .stream()
                .map(AdminUserRole::getRoleId)
                .distinct()
                .toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return adminRoleService.lambdaQuery()
                .in(AdminRole::getId, roleIds)
                .eq(AdminRole::getStatus, 1)
                .orderByAsc(AdminRole::getId)
                .list();
    }

    /**
     * 查询管理员可访问的菜单列表。
     */
    public List<AdminMenu> getMenusByAdminUserId(Long adminUserId) {
        List<AdminRole> roles = getRolesByAdminUserId(adminUserId);
        if (roles.isEmpty()) {
            return List.of();
        }
        List<Long> roleIds = roles.stream().map(AdminRole::getId).toList();
        Set<Long> menuIds = adminRoleMenuService.lambdaQuery()
                .in(AdminRoleMenu::getRoleId, roleIds)
                .list()
                .stream()
                .map(AdminRoleMenu::getMenuId)
                .collect(java.util.stream.Collectors.toSet());
        if (menuIds.isEmpty()) {
            return List.of();
        }
        return adminMenuService.lambdaQuery()
                .in(AdminMenu::getId, menuIds)
                .eq(AdminMenu::getStatus, 1)
                .orderByAsc(AdminMenu::getSortNo)
                .orderByAsc(AdminMenu::getId)
                .list();
    }

    /**
     * 查询管理员可访问的菜单树。
     */
    public List<AdminMenuTreeVo> getMenuTreeByAdminUserId(Long adminUserId) {
        return buildTree(getMenusByAdminUserId(adminUserId));
    }

    /**
     * 查询完整后台菜单树。
     */
    public List<AdminMenuTreeVo> getAllMenuTree() {
        List<AdminMenu> menus = adminMenuService.lambdaQuery()
                .orderByAsc(AdminMenu::getSortNo)
                .orderByAsc(AdminMenu::getId)
                .list();
        return buildTree(menus);
    }

    /**
     * 将菜单列表组装为树形结构。
     */
    private List<AdminMenuTreeVo> buildTree(List<AdminMenu> menus) {
        Map<Long, AdminMenuTreeVo> index = new LinkedHashMap<>();
        // 先建立 ID 索引，后续挂载子节点时可以 O(1) 找到父菜单。
        for (AdminMenu menu : menus) {
            AdminMenuTreeVo vo = new AdminMenuTreeVo();
            vo.setId(menu.getId());
            vo.setParentId(menu.getParentId());
            vo.setMenuName(menu.getMenuName());
            vo.setMenuCode(menu.getMenuCode());
            vo.setPath(menu.getPath());
            vo.setComponent(menu.getComponent());
            vo.setIcon(menu.getIcon());
            vo.setMenuType(menu.getMenuType());
            vo.setSortNo(menu.getSortNo());
            index.put(menu.getId(), vo);
        }
        List<AdminMenuTreeVo> roots = new ArrayList<>();
        for (AdminMenuTreeVo item : index.values()) {
            if (item.getParentId() == null || item.getParentId() == 0) {
                roots.add(item);
                continue;
            }
            AdminMenuTreeVo parent = index.get(item.getParentId());
            if (parent == null) {
                // 父菜单缺失时提升为根节点，避免菜单因为脏数据在前端不可见。
                roots.add(item);
            } else {
                parent.getChildren().add(item);
            }
        }
        return roots;
    }
}
