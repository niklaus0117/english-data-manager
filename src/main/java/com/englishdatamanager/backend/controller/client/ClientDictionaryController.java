package com.englishdatamanager.backend.controller.client;

import com.englishdatamanager.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
public class ClientDictionaryController {

    @GetMapping("/word")
    public ApiResponse<Map<String, Object>> word(@RequestParam("q") String word) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("word", word);
        result.put("phonetic", "");
        result.put("pronunciations", Map.of("us", "", "uk", ""));
        result.put("definitions", List.of());
        result.put("examples", List.of());
        result.put("source", "local-placeholder");
        return ApiResponse.success(result);
    }
}
