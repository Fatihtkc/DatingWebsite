package com.datingapp.backend.service.records;

import java.util.List;

import com.datingapp.backend.service.FileUploadResult;

public record BatchUploadResult(
    List<FileUploadResult> accepted,   
    List<RejectedFile> rejected        
) {
    public record RejectedFile(String originalFilename, String reason) {}
}