package com.datingapp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import com.datingapp.backend.records.BatchUploadResult;

@Data
@AllArgsConstructor
public class UploadedImagesResponseDTO {
    private List<ImageDTO> accepted;
    private List<BatchUploadResult.RejectedFile> rejected;
}