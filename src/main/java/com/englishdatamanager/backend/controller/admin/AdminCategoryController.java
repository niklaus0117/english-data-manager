package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.entity.Category;
import com.englishdatamanager.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * 查询数据列表。
     */
    @GetMapping
    public ApiResponse<List<Category>> list() {
        return ApiResponse.success(categoryService.lambdaQuery().orderByAsc(Category::getSortNo).list());
    }

    /**
     * 查询数据详情。
     */
    @GetMapping("/{id}")
    public ApiResponse<Category> detail(@PathVariable Long id) {
        return ApiResponse.success(categoryService.getById(id));
    }

    /**
     * 创建一条业务数据。
     */
    @PostMapping
    public ApiResponse<Void> create(@RequestBody Category category) {
        categoryService.save(category);
        return ApiResponse.success();
    }

    /**
     * 更新指定业务数据。
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateById(category);
        return ApiResponse.success();
    }

    /**
     * 删除指定业务数据。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return ApiResponse.success();
    }
}
