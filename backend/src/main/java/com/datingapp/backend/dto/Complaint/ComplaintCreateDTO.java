package com.datingapp.backend.dto.Complaint;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.datingapp.backend.dto.ImageDTO;

@Data
@NoArgsConstructor
public class ComplaintCreateDTO {
    private Long complainedId;
    private String reason;
    private List<ImageDTO> images;
}
