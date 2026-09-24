package com.datingapp.backend.service.impl;

import com.datingapp.backend.enums.ModerationStatus;
import com.datingapp.backend.service.FileStorageService;
import com.datingapp.backend.service.FileUploadResult;
import com.datingapp.backend.service.ImageModerationService;
import com.datingapp.backend.service.records.BatchUploadResult;
import com.datingapp.backend.exception.ContentPolicyViolationException;

import net.coobird.thumbnailator.Thumbnails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final int MAX_DIMENSION = 1600;

    private final Path fileStorageLocation;
    private final ImageModerationService imageModerationService;

    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir, ImageModerationService imageModerationService){

        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.imageModerationService = imageModerationService;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex){
            throw new RuntimeException("Could not create upload directory.", ex);
        }
    }

    @Override
    public FileUploadResult storeFile(MultipartFile file){

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("The file cannot be empty.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("The file size cannot exceed 5 MB.");
        }

        BufferedImage image;
        byte[] originalBytes;

        try {
            originalBytes = file.getBytes();
            image = ImageIO.read(new ByteArrayInputStream(originalBytes));
        } catch (IOException ex) {
            throw new IllegalArgumentException("The file could not be read.", ex);
        }

        if (image == null) {
            throw new IllegalArgumentException("The uploaded file is not a valid or supported image.");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())){
            throw new IllegalArgumentException("Only JPG and PNG images are supported.");
        }

        ModerationStatus moderationStatus = imageModerationService.moderate(originalBytes);

        if (moderationStatus == ModerationStatus.REJECTED) {
            throw new ContentPolicyViolationException("The uploaded image violates our content policy.");
        }

        String outputExtension =contentType.equalsIgnoreCase("image/png") ? "png" : "jpg";

        String generatedFilename = UUID.randomUUID() + "." + outputExtension;

        Path targetLocation = fileStorageLocation.resolve(generatedFilename).normalize();

        if (!targetLocation.startsWith(fileStorageLocation)) {
            throw new SecurityException("Invalid file path.");
        }

        try {
            Thumbnails.of(image).size(MAX_DIMENSION, MAX_DIMENSION).outputFormat(outputExtension).toFile(targetLocation.toFile());
        } catch (IOException ex) {
            throw new RuntimeException("The file could not be saved: " + generatedFilename, ex);
        }

        String fileUrl = "/uploads/" + generatedFilename;

        return new FileUploadResult(fileUrl, moderationStatus);
    }

    @Override
    public BatchUploadResult storeFiles(List<MultipartFile> files) {
        List<FileUploadResult> accepted = new ArrayList<>();
        List<BatchUploadResult.RejectedFile> rejected = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                FileUploadResult result = storeFile(file);
                accepted.add(result);
            } catch (ContentPolicyViolationException e) {
                rejected.add(new BatchUploadResult.RejectedFile(file.getOriginalFilename(), e.getMessage()));
            } catch (IllegalArgumentException e) {
                rejected.add(new BatchUploadResult.RejectedFile(file.getOriginalFilename(), e.getMessage()));
            }
        }

        return new BatchUploadResult(accepted, rejected);
    }

    @Override
    public Resource loadFileAsResource(String filename) {

        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("The file name cannot be empty.");
        }

        Path filePath = fileStorageLocation.resolve(filename).normalize();

        if (!filePath.startsWith(fileStorageLocation)) {
            throw new SecurityException("Invalid file name.");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()){
                return resource;
            }

            throw new RuntimeException("File not found: " + filename);

        } catch (MalformedURLException ex) {
            throw new RuntimeException("The file could not be loaded: " + filename, ex);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl){

        if (fileUrl == null || fileUrl.isBlank()) {
            return false;
        }

        try {
            String filename = Paths.get(new URI(fileUrl).getPath()).getFileName().toString();

            Path filePath =fileStorageLocation.resolve(filename).normalize();

            if (!filePath.startsWith(fileStorageLocation)){

                logger.warn("Blocked path traversal attempt while deleting file: {}", fileUrl);

                return false;
            }

            return Files.deleteIfExists(filePath);

        } catch (Exception ex) {

            logger.error("Error while deleting file: {}", fileUrl, ex);

            return false;
        }
    }
}