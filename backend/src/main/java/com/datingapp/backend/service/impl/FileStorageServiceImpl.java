package com.datingapp.backend.service.impl;

import com.datingapp.backend.service.FileStorageService;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private final Path fileStorageLocation;

    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Dosya boş olamaz.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Dosya boyutu 5MB'ı aşamaz.");
        }

        // 1) Dosya adını temizle (sadece bilgi amaçlı, güvenlik kararı burada verilmez)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Dosya adı boş olamaz.");
        }
        StringUtils.cleanPath(originalFilename); // path traversal denemesi varsa normalize eder

        // 2) Gerçek içeriği decode ederek doğrula ve YENİDEN encode et
        //    Bu adım hem "sahte uzantılı" saldırıları hem gömülü zararlı veriyi (EXIF, ek payload) engeller.
        BufferedImage image;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Dosya okunamadı.", ex);}
        if (image == null) {
            throw new IllegalArgumentException("Geçersiz veya desteklenmeyen resim dosyası.");
        }

        // Content-Type'ı da çapraz kontrol et (ImageIO zaten "gerçek resim mi" sorusunu cevaplıyor,
        // bu ek bir savunma katmanı)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Yalnızca JPG veya PNG dosyaları destekleniyor.");
        }

        // 3) Çıktı formatını biz belirliyoruz (client'ın verdiği uzantıya güvenmiyoruz)
        String outputExtension = contentType.equals("image/png") ? "png" : "jpg";
        String generatedFilename = UUID.randomUUID() + "." + outputExtension;

        Path targetLocation = this.fileStorageLocation.resolve(generatedFilename).normalize();

        // 4) Ekstra güvenlik: hedef yolun gerçekten storage dizini altında kaldığını doğrula
        if (!targetLocation.startsWith(this.fileStorageLocation)) {
            throw new SecurityException("Geçersiz dosya yolu.");
        }

        try {
            Thumbnails.of(image)
            .size(1600, 1600)   // orantılı küçültür, büyütmez
            .outputFormat(outputExtension)
            .toFile(targetLocation.toFile());
        } catch (IOException ex) {
            throw new RuntimeException("Dosya kaydedilemedi: " + generatedFilename, ex);
        }

        return "/uploads/" + generatedFilename;
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        // Path traversal koruması: gelen filename'i normalize edip storage dizini dışına
        // çıkmadığını doğrula
        Path filePath = this.fileStorageLocation.resolve(filename).normalize();

        if (!filePath.startsWith(this.fileStorageLocation)) {
            throw new SecurityException("Geçersiz dosya adı.");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + filename, ex);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String filename = Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();

            if (!filePath.startsWith(this.fileStorageLocation)) {
                return false; // path traversal denemesi
            }
            return Files.deleteIfExists(filePath);
        } catch (Exception ex) {
            // TODO: SLF4J logger ile değiştirin
            System.err.println("Dosya silinirken hata: " + ex.getMessage());
            return false;
        }
    }
}