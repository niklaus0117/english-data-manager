package com.englishdatamanager.backend.controller.client;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.service.AppCourseQueryService;
import com.englishdatamanager.backend.service.AppVideoQueryService;
import com.englishdatamanager.backend.vo.AppLessonPlayerVo;
import com.englishdatamanager.backend.vo.AppLessonSentenceVo;
import com.englishdatamanager.backend.vo.AppVideoDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class ClientLessonController {

    private final AppCourseQueryService appCourseQueryService;
    private final AppVideoQueryService appVideoQueryService;

    /**
     * 查询数据详情。
     */
    @GetMapping("/{id}")
    public ApiResponse<AppVideoDetailVo> detail(@PathVariable Long id) {
        return ApiResponse.success(appVideoQueryService.getVideoDetail(id));
    }

    /**
     * 查询章节播放器数据。
     */
    @GetMapping("/{id}/player")
    public ApiResponse<AppLessonPlayerVo> player(@PathVariable Long id) {
        return ApiResponse.success(appCourseQueryService.lessonPlayerByLesson(id));
    }

    /**
     * 查询章节逐句脚本。
     */
    @GetMapping("/{id}/transcripts")
    public ApiResponse<List<AppLessonSentenceVo>> transcripts(@PathVariable Long id) {
        return ApiResponse.success(appCourseQueryService.lessonTranscript(id));
    }
}
