package com.englishdatamanager.backend.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.common.PageResponse;
import com.englishdatamanager.backend.entity.Video;
import com.englishdatamanager.backend.service.AppVideoQueryService;
import com.englishdatamanager.backend.service.VideoService;
import com.englishdatamanager.backend.vo.AppVideoDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/videos")
@RequiredArgsConstructor
public class AppVideoController {

    private final VideoService videoService;
    private final AppVideoQueryService appVideoQueryService;

    @GetMapping
    public ApiResponse<PageResponse<Video>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        Page<Video> page = videoService.lambdaQuery()
                .eq(Video::getPublishStatus, 1)
                .eq(categoryId != null, Video::getCategoryId, categoryId)
                .and(keyword != null && !keyword.isBlank(),
                        wrapper -> wrapper.like(Video::getTitleCn, keyword).or().like(Video::getTitleEn, keyword))
                .orderByDesc(Video::getPublishedAt)
                .orderByDesc(Video::getId)
                .page(new Page<>(current, size));
        return ApiResponse.success(PageResponse.of(page));
    }

    @GetMapping("/{id}")
    public ApiResponse<AppVideoDetailVo> detail(@PathVariable Long id) {
        return ApiResponse.success(appVideoQueryService.getVideoDetail(id));
    }
}
