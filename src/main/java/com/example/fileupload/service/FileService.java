package com.example.fileupload.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {
    Map<String, Object> uploadFile(MultipartFile file);
}
