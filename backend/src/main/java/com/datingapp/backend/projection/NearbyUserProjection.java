package com.datingapp.backend.projection;

import java.time.LocalDate;

public interface NearbyUserProjection {

    Long getId();
    String getUsername();
    String getFirstName();
    String getLastName();
    LocalDate getBirthDate();

    String getGender();
    String getBodyType();
    String getRelationshipType();

    String getSmoke();
    String getAlcohol();
    String getDiet();

    String getShorterBio();

    Double getDistance();
}
