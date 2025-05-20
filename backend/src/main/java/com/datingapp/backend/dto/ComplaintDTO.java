package com.datingapp.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComplaintDTO {
    private Long id;
    private Long complainantId;
    private Long complainedId;
    private String reason;
    private LocalDateTime complaintDate;
}
