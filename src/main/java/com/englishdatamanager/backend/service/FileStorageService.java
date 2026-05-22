package com.englishdatamanager.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface FileStorageService {

    /**
     * 存储上传文件并返回访问元数据。
     */
    Map<String, Object> store(MultipartFile file) throws IOException;
}
