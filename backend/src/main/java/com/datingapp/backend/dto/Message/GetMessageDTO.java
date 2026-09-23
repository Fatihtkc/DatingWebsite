package com.datingapp.backend.dto.Message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class GetMessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isImage;
    private Boolean isRead;
}
