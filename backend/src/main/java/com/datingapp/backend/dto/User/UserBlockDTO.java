package com.datingapp.backend.dto.User;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserBlockDTO{
    
    @NotNull
    private Long blockedUserId;

}