package com.englishdatamanager.backend.controller.app;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.exception.BusinessException;
import com.englishdatamanager.backend.security.UserContext;
import com.englishdatamanager.backend.service.AppCourseQueryService;
import com.englishdatamanager.backend.service.UserCourseAccessService;
import com.englishdatamanager.backend.service.UserDownloadRecordService;
import com.englishdatamanager.backend.service.UserFavoriteService;
import com.englishdatamanager.backend.service.UserOrderService;
import com.englishdatamanager.backend.service.VideoAlbumService;
import com.englishdatamanager.backend.service.VideoService;
import com.englishdatamanager.backend.vo.AppCourseCardVo;
import com.englishdatamanager.backend.vo.AppCourseCategoryVo;
import com.englishdatamanager.backend.vo.AppCourseDetailVo;
import com.englishdatamanager.backend.vo.AppLessonPlayerVo;
import com.englishdatamanager.backend.vo.AppLessonVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/app/courses")
@RequiredArgsConstructor
public class AppCourseController {

    private final AppCourseQueryService appCourseQueryService;
    private final UserCourseAccessService userCourseAccessService;
    private final UserOrderService userOrderService;
    private final VideoAlbumService videoAlbumService;
    private final UserFavoriteService userFavoriteService;
    private final UserDownloadRecordService userDownloadRecordService;
    private final VideoService videoService;

    @GetMapping("/recommend")
    public ApiResponse<List<AppCourseCardVo>> recommend() {
        return ApiResponse.success(appCourseQueryService.recommendCourses());
    }

    @GetMapping("/daily-reading/categories")
    public ApiResponse<List<AppCourseCategoryVo>> dailyReadingCategories() {
        return ApiResponse.success(appCourseQueryService.dailyReadingCategories());
    }

    @GetMapping("/daily-reading")
    public ApiResponse<List<AppCourseCardVo>> dailyReadingCourses(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(appCourseQueryService.dailyReadingCourses(categoryId));
    }

    @GetMapping("/purchased")
    public ApiResponse<List<AppCourseCardVo>> purchased() {
        return ApiResponse.success(appCourseQueryService.purchasedCourses());
    }

    @GetMapping("/{courseId}")
    public ApiResponse<AppCourseDetailVo> detail(@PathVariable Long courseId) {
        return ApiResponse.success(appCourseQueryService.courseDetail(courseId));
    }

    @GetMapping("/{courseId}/lessons")
    public ApiResponse<List<AppLessonVo>> lessons(@PathVariable Long courseId) {
        return ApiResponse.success(appCourseQueryService.courseLessons(courseId));
    }

    @GetMapping("/{courseId}/lessons/{lessonId}/player")
    public ApiResponse<AppLessonPlayerVo> lessonPlayer(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return ApiResponse.success(appCourseQueryService.lessonPlayer(courseId, lessonId));
    }

    @PostMapping("/{courseId}/purchase")
    public ApiResponse<Void> purchase(@PathVariable Long courseId) {
        if (videoAlbumService.getById(courseId) == null) {
            throw new BusinessException("课程不存在");
        }
        userOrderService.createCourseOrder(UserContext.getUserId(), courseId);
        userCourseAccessService.grantAccess(UserContext.getUserId(), courseId, "purchase");
        return ApiResponse.success();
    }

    @PostMapping("/{courseId}/lessons/{lessonId}/favorite")
    public ApiResponse<Void> favoriteLesson(@PathVariable Long courseId, @PathVariable Long lessonId) {
        ensureLessonExists(courseId, lessonId);
        userFavoriteService.addFavorite(UserContext.getUserId(), UserFavoriteService.TARGET_TYPE_VIDEO, lessonId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{courseId}/lessons/{lessonId}/favorite")
    public ApiResponse<Void> unfavoriteLesson(@PathVariable Long courseId, @PathVariable Long lessonId) {
        ensureLessonExists(courseId, lessonId);
        userFavoriteService.removeFavorite(UserContext.getUserId(), UserFavoriteService.TARGET_TYPE_VIDEO, lessonId);
        return ApiResponse.success();
    }

    @PostMapping("/{courseId}/lessons/{lessonId}/download")
    public ApiResponse<Void> downloadLesson(@PathVariable Long courseId, @PathVariable Long lessonId) {
        ensureLessonExists(courseId, lessonId);
        var video = videoService.getById(lessonId);
        userDownloadRecordService.markDownloaded(
                UserContext.getUserId(),
                lessonId,
                video == null ? null : video.getVideoUrl(),
                video == null ? 0L : video.getFileSizeBytes()
        );
        return ApiResponse.success();
    }

    private void ensureLessonExists(Long courseId, Long lessonId) {
        boolean exists = videoAlbumService.getById(courseId) != null
                && videoService.getById(lessonId) != null;
        if (!exists) {
            throw new BusinessException("课程或章节不存在");
        }
    }
}
