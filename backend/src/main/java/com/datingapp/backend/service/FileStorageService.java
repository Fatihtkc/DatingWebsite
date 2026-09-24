package com.datingapp.backend.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.datingapp.backend.service.records.BatchUploadResult;


public interface FileStorageService {

    FileUploadResult storeFile(MultipartFile file);
    BatchUploadResult storeFiles(List<MultipartFile> files);
    Resource loadFileAsResource(String filename);
    boolean deleteFile(String fileUrl);

}