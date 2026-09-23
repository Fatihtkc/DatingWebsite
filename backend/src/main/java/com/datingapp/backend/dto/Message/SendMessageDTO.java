package com.datingapp.backend.dto.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDTO {

    private Long conversationId;
    private String content;
    private Boolean isImage;
}