package com.datingapp.backend.dto;

import lombok.Data;

@Data
public class UserImageDTO {
    private Long id;
    private String imageUrl;
    // İsteğe bağlı: Kullanıcı ID'si de eklenebilir
    private Long userId;
}
