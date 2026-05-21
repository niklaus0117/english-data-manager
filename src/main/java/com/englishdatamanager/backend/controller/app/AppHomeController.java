package com.englishdatamanager.backend.controller.app;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.service.AppCourseQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/app/home")
@RequiredArgsConstructor
public class AppHomeController {

    private final AppCourseQueryService appCourseQueryService;

    @GetMapping("/index")
    public ApiResponse<Map<String, Object>> index() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recommendCourses", appCourseQueryService.recommendCourses());
        result.put("dailyReadingCategories", appCourseQueryService.dailyReadingCategories());
        result.put("dailyReadingCourses", appCourseQueryService.dailyReadingCourses(null));
        return ApiResponse.success(result);
    }
}
