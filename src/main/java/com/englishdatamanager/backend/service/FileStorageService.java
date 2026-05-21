package com.englishdatamanager.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface FileStorageService {

    Map<String, Object> store(MultipartFile file) throws IOException;
}
