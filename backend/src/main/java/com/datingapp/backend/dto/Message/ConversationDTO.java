package com.datingapp.backend.dto.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {

    private Long id;

    private Long user1Id;

    private Long user2Id;

    private LocalDateTime createdAt;

    private LocalDateTime lastMessageAt;

    private Boolean user1HasUnread;

    private Boolean user2HasUnread;
}