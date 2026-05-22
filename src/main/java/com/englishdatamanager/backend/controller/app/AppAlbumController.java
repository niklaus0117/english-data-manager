package com.englishdatamanager.backend.controller.app;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.service.AppAlbumQueryService;
import com.englishdatamanager.backend.vo.AppAlbumDetailVo;
import com.englishdatamanager.backend.vo.AppAlbumListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppAlbumController {

    private final AppAlbumQueryService appAlbumQueryService;

    /**
     * 按分类查询课程专辑列表。
     */
    @GetMapping("/categories/{categoryId}/albums")
    public ApiResponse<AppAlbumListVo> albumsByCategory(@PathVariable Long categoryId) {
        return ApiResponse.success(appAlbumQueryService.getAlbumsByCategory(categoryId));
    }

    /**
     * 查询课程专辑详情。
     */
    @GetMapping("/albums/{albumId}")
    public ApiResponse<AppAlbumDetailVo> albumDetail(@PathVariable Long albumId) {
        return ApiResponse.success(appAlbumQueryService.getAlbumDetail(albumId));
    }
}
