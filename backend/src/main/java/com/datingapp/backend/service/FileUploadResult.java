package com.datingapp.backend.service;

import com.datingapp.backend.enums.ModerationStatus;

public record FileUploadResult(String url, ModerationStatus moderationStatus) {}