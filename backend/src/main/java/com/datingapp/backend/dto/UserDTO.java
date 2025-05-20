package com.datingapp.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String gender;
    private Double height;
    private Double weight;
    private String bodyType;
    private String location;
    private String relationshipType;
    private String agePreference;
    private String distancePreference;
    private Boolean smoke;
    private Boolean alcohol;
    private String about;
    // İsteğe bağlı: Profil resimleri listesini de göndermek için
    private List<UserImageDTO> images;
}
