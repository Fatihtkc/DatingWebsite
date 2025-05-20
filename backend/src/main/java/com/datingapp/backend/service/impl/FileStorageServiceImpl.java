package com.datingapp.backend.service.impl;

import com.datingapp.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
            .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Dosya adı boş olamaz.");
        }
        String cleanedFilename = StringUtils.cleanPath(originalFilename);
        if (!cleanedFilename.endsWith(".png") && !cleanedFilename.endsWith(".jpg")) {
            throw new RuntimeException("Yalnızca JPG veya PNG dosyaları destekleniyor.");
        }        
        String generatedFilename = UUID.randomUUID().toString() + "_" + cleanedFilename;
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Cannot store empty file: " + cleanedFilename);
            }
            if (generatedFilename.contains("..")) {
                throw new RuntimeException("Filename contains invalid path sequence " + generatedFilename);
            }

            Path targetLocation = this.fileStorageLocation.resolve(generatedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + generatedFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + generatedFilename, ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + filename, ex);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String filename = Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (Exception ex) {
            System.err.println("Dosya silinirken hata: " + ex.getMessage());
        }
    }

}
