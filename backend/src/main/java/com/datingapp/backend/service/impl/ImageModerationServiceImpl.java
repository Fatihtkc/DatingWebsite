package com.datingapp.backend.service.impl;

import com.datingapp.backend.enums.ModerationStatus;
import com.datingapp.backend.service.ImageModerationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.List;

@Service
public class ImageModerationServiceImpl implements ImageModerationService {

    private static final Logger log = LoggerFactory.getLogger(ImageModerationServiceImpl.class);

    private static final List<String> BLOCKED_LABELS = List.of(
        "Explicit Nudity", "Nudity", "Graphic Male Nudity", "Graphic Female Nudity",
        "Sexual Activity", "Violence", "Graphic Violence", "Weapons"
    );

    private static final float MIN_CONFIDENCE = 75.0f;

    private final RekognitionClient rekognitionClient;

    public ImageModerationServiceImpl(@Value("${aws.rekognition.region}") String region){
        this.rekognitionClient = RekognitionClient.builder()
            .region(Region.of(region))
            .build();
    }

    @Override
    public ModerationStatus moderate(byte[] imageBytes){

        try {
            Image image = Image.builder()
                .bytes(SdkBytes.fromByteArray(imageBytes))
                .build();

            DetectModerationLabelsRequest request = DetectModerationLabelsRequest.builder()
                .image(image)
                .minConfidence(MIN_CONFIDENCE)
                .build();

            DetectModerationLabelsResponse response = rekognitionClient.detectModerationLabels(request);

            boolean hasViolation = response.moderationLabels().stream()
                .anyMatch(label -> BLOCKED_LABELS.contains(label.name()));

            if (hasViolation) {
                log.warn("Inappropriate content detected. Tags: {}",
                    response.moderationLabels().stream().map(ModerationLabel::name).toList());
                return ModerationStatus.REJECTED;
            }

            return ModerationStatus.APPROVED;

        } catch (Exception e) {
            log.error("Rekognition call failed.", e);
            return ModerationStatus.PENDING;
        }
    }
}