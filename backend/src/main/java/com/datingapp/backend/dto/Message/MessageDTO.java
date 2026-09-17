package com.datingapp.backend.dto.Message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MessageDTO {
    private Long id;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isImage;
}
