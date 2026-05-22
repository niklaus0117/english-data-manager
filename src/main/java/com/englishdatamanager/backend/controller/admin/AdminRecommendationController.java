package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.entity.Recommendation;
import com.englishdatamanager.backend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/recommendations")
@RequiredArgsConstructor
public class AdminRecommendationController {

    private final RecommendationService recommendationService;

    /**
     * 查询数据列表。
     */
    @GetMapping
    public ApiResponse<List<Recommendation>> list() {
        return ApiResponse.success(recommendationService.lambdaQuery().orderByAsc(Recommendation::getSortNo).list());
    }

    /**
     * 创建一条业务数据。
     */
    @PostMapping
    public ApiResponse<Void> create(@RequestBody Recommendation recommendation) {
        recommendationService.save(recommendation);
        return ApiResponse.success();
    }

    /**
     * 更新指定业务数据。
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Recommendation recommendation) {
        recommendation.setId(id);
        recommendationService.updateById(recommendation);
        return ApiResponse.success();
    }

    /**
     * 删除指定业务数据。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        recommendationService.removeById(id);
        return ApiResponse.success();
    }
}
