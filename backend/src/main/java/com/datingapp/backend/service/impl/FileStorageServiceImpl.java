package com.datingapp.backend.service.impl;

import com.datingapp.backend.enums.ModerationStatus;
import com.datingapp.backend.service.FileStorageService;
import com.datingapp.backend.service.FileUploadResult;
import com.datingapp.backend.service.ImageModerationService;
import com.datingapp.backend.exception.ContentPolicyViolationException;
import com.datingapp.backend.exception.FileStorageException;
import com.datingapp.backend.exception.ResourceNotFoundException;
import com.datingapp.backend.exception.TooManyRequestsException;
import com.datingapp.backend.records.BatchUploadResult;

import net.coobird.thumbnailator.Thumbnails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Semaphore;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final int MAX_FILES_PER_REQUEST = 10;

    private static final long MAX_BATCH_SIZE = 25 * 1024 * 1024;

    private static final int MAX_WIDTH = 8000;

    private static final int MAX_HEIGHT = 8000;

    private static final long MAX_PIXELS = 20_000_000L;

    private static final int MAX_DIMENSION = 1600;

    private final Semaphore uploadSlots = new Semaphore(4);

    private final Path fileStorageLocation;
    private final ImageModerationService imageModerationService;

    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir, ImageModerationService imageModerationService){

        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.imageModerationService = imageModerationService;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex){
            throw new FileStorageException("Could not create upload directory.", ex);
        }
    }

    @Override
    public FileUploadResult storeFile(MultipartFile file){

        if(!uploadSlots.tryAcquire()){
            throw new TooManyRequestsException( "Too many image uploads are being processed.");
        }

        try{
            return storeFileInternal(file);
        } finally{
            uploadSlots.release();
        }
    }

    public FileUploadResult storeFileInternal(MultipartFile file){

        if (file == null || file.isEmpty()){
            throw new IllegalArgumentException("The file cannot be empty.");
        }

        if (file.getSize() > MAX_FILE_SIZE){
            throw new IllegalArgumentException("The file size cannot exceed 5 MB.");
        }

        byte[] originalBytes;
        BufferedImage image;
        String imageFormat;

        try{
            originalBytes = file.getBytes();

            try(
                ByteArrayInputStream input = new ByteArrayInputStream(originalBytes);
                ImageInputStream imageInput = ImageIO.createImageInputStream(input)
            ){
                if(imageInput == null){
                    throw new IllegalArgumentException("Invalid image.");
                }

                Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);

                if(!readers.hasNext()){
                    throw new IllegalArgumentException("The uploaded file is not a valid image.");
                }

                ImageReader reader = readers.next();

                try{
                    reader.setInput(imageInput, false, true);

                    String detectedFormat = reader.getFormatName().toLowerCase(Locale.ROOT);

                    if (!"jpg".equals(detectedFormat) && !"jpeg".equals(detectedFormat) && !"png".equals(detectedFormat)){
                        throw new IllegalArgumentException("Only JPG and PNG images are supported.");
                    }

                    imageFormat = "png".equals(detectedFormat) ? "png" : "jpeg";

                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);
                    long pixels = (long) width * height;

                    if(width <= 0 || height <= 0 || width > MAX_WIDTH || height > MAX_HEIGHT || pixels > MAX_PIXELS){
                        throw new IllegalArgumentException("The image dimensions are too large.");
                    }

                    image = reader.read(0);

                    if(image == null){
                        throw new IllegalArgumentException( "The uploaded file is not a valid image.");
                    }

                } finally{
                    reader.dispose();
                }
            }

        } catch (IOException ex){
            throw new FileStorageException("The uploaded image could not be read.", ex);
        }

        ModerationStatus moderationStatus = imageModerationService.moderate(originalBytes);

        if (moderationStatus == ModerationStatus.REJECTED){
            throw new ContentPolicyViolationException("The uploaded image violates our content policy.");
        }

        String outputExtension = "png".equals(imageFormat) ? "png" : "jpg";

        String generatedFilename = UUID.randomUUID() + "." + outputExtension;

        Path targetLocation = fileStorageLocation.resolve(generatedFilename).normalize();

        if(!targetLocation.startsWith(fileStorageLocation)){
            throw new SecurityException("Invalid file path.");
        }

        try{
            Thumbnails.of(image).size(MAX_DIMENSION, MAX_DIMENSION).outputFormat(outputExtension).toFile(targetLocation.toFile());
        } catch (IOException ex){
            throw new FileStorageException("The image could not be saved.", ex);
        }

        String fileUrl = "/uploads/" + generatedFilename;

        return new FileUploadResult(fileUrl, moderationStatus);
    }

    @Override
    public BatchUploadResult storeFiles(List<MultipartFile> files){
        
        if(files == null || files.isEmpty()){
            throw new IllegalArgumentException("At least one file is required.");
        }

        if(files.size() > MAX_FILES_PER_REQUEST){
            throw new IllegalArgumentException("You can upload at most 10 files at once.");
        }

        long totalSize = 0;

        for(MultipartFile file : files){
            totalSize += file.getSize();

            if (totalSize > MAX_BATCH_SIZE){
                throw new IllegalArgumentException("The total upload size cannot exceed 25 MB.");
            }
        }

        List<FileUploadResult> accepted = new ArrayList<>();
        List<BatchUploadResult.RejectedFile> rejected = new ArrayList<>();

        for (MultipartFile file : files){
            try {
                FileUploadResult result = storeFile(file);
                accepted.add(result);
            } catch (ContentPolicyViolationException e){
                rejected.add(new BatchUploadResult.RejectedFile(file.getOriginalFilename(), e.getMessage()));
            } catch (IllegalArgumentException e){
                rejected.add(new BatchUploadResult.RejectedFile(file.getOriginalFilename(), e.getMessage()));
            }
        }

        return new BatchUploadResult(accepted, rejected);
    }

    @Override
    public Resource loadFileAsResource(String filename){

        if(filename == null || filename.isBlank()){
            throw new IllegalArgumentException("The file name cannot be empty.");
        }

        Path filePath = fileStorageLocation.resolve(filename).normalize();

        if(!filePath.startsWith(fileStorageLocation)){
            throw new SecurityException("Invalid file name.");
        }

        try{
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()){
                return resource;
            }

            throw new ResourceNotFoundException("File not found: " + filename);

        } catch(MalformedURLException ex){
            throw new FileStorageException("The file could not be loaded: " + filename, ex);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl){

        if (fileUrl == null || fileUrl.isBlank()){
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

        } catch (Exception ex){

            logger.error("Error while deleting file: {}", fileUrl, ex);

            return false;
        }
    }

}