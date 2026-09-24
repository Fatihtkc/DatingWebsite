package com.datingapp.backend.service;

import com.datingapp.backend.enums.ModerationStatus;

public interface ImageModerationService {
    ModerationStatus moderate(byte[] imageBytes);
}