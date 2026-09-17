package com.datingapp.backend.dto.Complaint;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.datingapp.backend.dto.ImageDTO;
import com.datingapp.backend.dto.UserAdminDTO;

@Data
@NoArgsConstructor
public class ComplaintDTO {
    private Long id;
    private UserAdminDTO complainant;
    private UserAdminDTO complained;
    private String reason;
    private LocalDateTime complaintDate;
    private List<ImageDTO> images;
}
