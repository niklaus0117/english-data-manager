package com.englishdatamanager.backend.service;

import com.englishdatamanager.backend.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final AppProperties appProperties;

    /**
     * 存储上传文件并返回访问元数据。
     */
    @Override
    public Map<String, Object> store(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        // 用随机文件名避免用户上传的原始文件名冲突或泄露本地路径信息。
        String fileName = UUID.randomUUID().toString().replace("-", "");
        if (extension != null && !extension.isBlank()) {
            fileName = fileName + "." + extension;
        }
        Path storagePath = Paths.get(appProperties.getStorage().getLocalPath()).toAbsolutePath();
        Files.createDirectories(storagePath);
        Path target = storagePath.resolve(fileName);
        file.transferTo(target);

        // 返回前端可直接使用的公开 URL，同时保留文件大小和存储类型供后台展示。
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fileName", fileName);
        result.put("url", appProperties.getStorage().getPublicPrefix() + fileName);
        result.put("size", file.getSize());
        result.put("storageType", "local");
        return result;
    }
}
