package com.datingapp.backend.projection;

import java.time.LocalDateTime;

public interface ComplaintProjection {

    Long getId();

    String getReason();

    LocalDateTime getComplaintDate();

    Long getComplainantId();
    String getComplainantUsername();
    String getComplainantFirstName();
    String getComplainantLastName();
    String getComplainantEmail();
    String getComplainantImageUrl();

    Long getComplainedId();
    String getComplainedUsername();
    String getComplainedFirstName();
    String getComplainedLastName();
    String getComplainedEmail();
    String getComplainedImageUrl();

    String getComplaintImageUrl();
}