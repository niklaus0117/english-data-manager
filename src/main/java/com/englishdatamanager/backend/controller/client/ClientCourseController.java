package com.englishdatamanager.backend.controller.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.common.PageResponse;
import com.englishdatamanager.backend.entity.Video;
import com.englishdatamanager.backend.service.AppCourseQueryService;
import com.englishdatamanager.backend.service.VideoService;
import com.englishdatamanager.backend.vo.AppCourseCardVo;
import com.englishdatamanager.backend.vo.AppCourseDetailVo;
import com.englishdatamanager.backend.vo.AppLessonVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class ClientCourseController {

    private final AppCourseQueryService appCourseQueryService;
    private final VideoService videoService;

    @GetMapping
    public ApiResponse<List<AppCourseCardVo>> list(@RequestParam(required = false) Long categoryId,
                                                   @RequestParam(required = false) Boolean isVip,
                                                   @RequestParam(defaultValue = "10") Integer limit) {
        List<AppCourseCardVo> courses = appCourseQueryService.courseList(categoryId, isVip, limit);
        return ApiResponse.success(courses);
    }

    @GetMapping("/{id}")
    public ApiResponse<AppCourseDetailVo> detail(@PathVariable Long id) {
        return ApiResponse.success(appCourseQueryService.courseDetail(id));
    }

    @GetMapping("/{id}/lessons")
    public ApiResponse<List<AppLessonVo>> lessons(@PathVariable Long id) {
        return ApiResponse.success(appCourseQueryService.courseLessons(id));
    }

    @GetMapping("/videos")
    public ApiResponse<PageResponse<Video>> videos(@RequestParam(defaultValue = "1") long current,
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
}
