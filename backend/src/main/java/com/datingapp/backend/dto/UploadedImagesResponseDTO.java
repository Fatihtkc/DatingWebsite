package com.datingapp.backend.dto;

import com.datingapp.backend.service.records.BatchUploadResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UploadedImagesResponseDTO {
    private List<ImageDTO> accepted;
    private List<BatchUploadResult.RejectedFile> rejected;
}