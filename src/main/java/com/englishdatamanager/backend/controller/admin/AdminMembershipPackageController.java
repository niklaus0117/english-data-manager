package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.entity.MembershipPackage;
import com.englishdatamanager.backend.service.MembershipPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Membership Package")
@RestController
@RequestMapping("/api/admin/packages")
@RequiredArgsConstructor
public class AdminMembershipPackageController {

    private final MembershipPackageService membershipPackageService;

    /**
     * 查询数据列表。
     */
    @Operation(summary = "Package list")
    @GetMapping
    public ApiResponse<List<MembershipPackage>> list() {
        return ApiResponse.success(membershipPackageService.lambdaQuery()
                .orderByAsc(MembershipPackage::getSortNo)
                .orderByDesc(MembershipPackage::getId)
                .list());
    }

    /**
     * 查询数据详情。
     */
    @Operation(summary = "Package detail")
    @GetMapping("/{id}")
    public ApiResponse<MembershipPackage> detail(@PathVariable Long id) {
        return ApiResponse.success(membershipPackageService.getById(id));
    }

    /**
     * 创建一条业务数据。
     */
    @Operation(summary = "Create package")
    @PostMapping
    public ApiResponse<Void> create(@RequestBody MembershipPackage membershipPackage) {
        membershipPackageService.save(membershipPackage);
        return ApiResponse.success();
    }

    /**
     * 更新指定业务数据。
     */
    @Operation(summary = "Update package")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody MembershipPackage membershipPackage) {
        membershipPackage.setId(id);
        membershipPackageService.updateById(membershipPackage);
        return ApiResponse.success();
    }

    /**
     * 删除指定业务数据。
     */
    @Operation(summary = "Delete package")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        membershipPackageService.removeById(id);
        return ApiResponse.success();
    }
}
