package com.englishdatamanager.backend.controller.client;

import com.englishdatamanager.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ClientAiController {

    @PostMapping("/analyze-sentence")
    public ApiResponse<Map<String, Object>> analyzeSentence(@RequestBody Map<String, String> request) {
        String sentence = request.getOrDefault("sentence", "");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sentence", sentence);
        result.put("summary", "AI provider is not configured. Please set up a model gateway before enabling sentence analysis.");
        result.put("grammar", "");
        result.put("vocabulary", java.util.List.of());
        return ApiResponse.success(result);
    }

    @PostMapping("/chat")
    public ApiResponse<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        return ApiResponse.success(Map.of(
                "reply", "AI chat provider is not configured.",
                "provider", "local-placeholder"
        ));
    }
}
