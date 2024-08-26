package com.example.fileupload.service.impl;

import com.example.fileupload.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {
    @Value("${file.upload.url}")
    private String url;
    @Value("${file.upload.allow.type}")
    private String[] allowType;
    @Value("${file.upload.maxsize}")
    private long maxsize;

    @Override
    public Map<String, Object> uploadFile(MultipartFile file){
        Map<String, Object> response = new HashMap<>();
        try {
            File uploadDir = new File(url);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            if (file.isEmpty()) {
                response.put("status", "failure");
                response.put("message", "No file is provided in the request.");
                return response;
            }

            if (file.getSize() > maxsize) {
                response.put("status", "failure");
                response.put("message", "The file exceeds a predefined size limit.");
                return response;
            }

            if (!isValidFileType(file.getContentType())) {
                response.put("status", "failure");
                response.put("message", "The file type is not allowed.");
                return response;
            }
            Path path = Paths.get(url + file.getOriginalFilename());
            if(Files.exists(path)){
                response.put("status", "failure");
                response.put("message", "The path exists.");
                return response;
            }
            Files.copy(file.getInputStream(), path);
            response.put("status", "success");
            response.put("message", "File uploaded successfully");
            response.put("file_url", path.toString());
        } catch (Exception e) {
            response.put("status", "failure");
            response.put("message", "Server errors occur during file handling.");
            e.printStackTrace();
        }
        return response;
    }

    private boolean isValidFileType(String contentType) {
        for (String allowedType : allowType) {
            if (allowedType.equals(contentType)) {
                return true;
            }
        }
        return false;
    }
}
