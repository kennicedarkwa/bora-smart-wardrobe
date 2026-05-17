package com.example.sea2_bora.implementation.Photo_Processing.service;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.Photo_Processing.config.StorageProperties;
import com.example.sea2_bora.implementation.Photo_Processing.dto.ImageResponse;
import com.example.sea2_bora.implementation.Photo_Processing.entity.StoredImage;
import com.example.sea2_bora.implementation.Photo_Processing.repository.StoredImageRepository;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private final StoredImageRepository storedImageRepository;
    private final KleidungsstueckRepository kleidungsstueckRepository;
    private final BackgroundRemovalService backgroundRemovalService;
    private final StorageProperties properties;

    public ImageService(
            StoredImageRepository storedImageRepository,
            KleidungsstueckRepository kleidungsstueckRepository,
            BackgroundRemovalService backgroundRemovalService,
            StorageProperties properties
    ) {
        this.storedImageRepository = storedImageRepository;
        this.kleidungsstueckRepository = kleidungsstueckRepository;
        this.backgroundRemovalService = backgroundRemovalService;
        this.properties = properties;
    }

    public ImageResponse storeWithoutBackground(Long kleidungsstueckId, MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Datei ist leer.");
        }

        Kleidungsstueck kleidungsstueck = kleidungsstueckRepository.findById(kleidungsstueckId)
                .orElseThrow(() -> new RuntimeException("Kleidungsstück nicht gefunden: " + kleidungsstueckId));

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload"
        );

        String lower = originalFilename.toLowerCase();
        if (!(lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".webp"))) {
            throw new IllegalArgumentException("Nur PNG, JPG, JPEG und WEBP sind erlaubt.");
        }

        Path uploadDir = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        String baseName = UUID.randomUUID().toString();
        Path inputPath = uploadDir.resolve(baseName + "-input" + getExtension(originalFilename));
        Path outputPath = uploadDir.resolve(baseName + "-no-bg.png");

        Files.write(inputPath, file.getBytes());

        try {
            backgroundRemovalService.removeBackground(inputPath, outputPath);

            StoredImage entity = new StoredImage();
            entity.setOriginalFilename(originalFilename);
            entity.setStoredPath(outputPath.toString());
            entity.setContentType("image/png");
            entity.setFileSize(Files.size(outputPath));
            entity.setCreatedAt(LocalDateTime.now());
            entity.setKleidungsstueck(kleidungsstueck);

            StoredImage saved = storedImageRepository.save(entity);

            return new ImageResponse(
                    saved.getId(),
                    saved.getOriginalFilename(),
                    saved.getStoredPath()
            );
        } finally {
            Files.deleteIfExists(inputPath);
        }
    }

    public StoredImage getImage(Long imageId) {
        return storedImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Bild nicht gefunden: " + imageId));
    }

    public List<StoredImage> getImagesByKleidungsstueck(Long kleidungsstueckId)
    {
        return storedImageRepository.findByKleidungsstueckId(kleidungsstueckId);
    }


    private String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx) : "";
    }
}