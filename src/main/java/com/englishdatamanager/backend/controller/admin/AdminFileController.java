package com.englishdatamanager.backend.controller.admin;

import com.englishdatamanager.backend.common.ApiResponse;
import com.englishdatamanager.backend.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Tag(name = "Admin File")
@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
public class AdminFileController {

    private final FileStorageService fileStorageService;

    /**
     * 上传文件并返回文件访问信息。
     */
    @Operation(summary = "Upload file")
    @PostMapping("/upload")
    public ApiResponse<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.success(fileStorageService.store(file));
    }
}
