package com.example.sea2_bora.service;

import com.example.sea2_bora.repository.KleidungsstueckRepository;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.Photo_Processing.config.StorageProperties;
import com.example.sea2_bora.implementation.Photo_Processing.dto.ImageResponse;
import com.example.sea2_bora.implementation.Photo_Processing.entity.StoredImage;
import com.example.sea2_bora.implementation.Photo_Processing.repository.StoredImageRepository;
import com.example.sea2_bora.implementation.Photo_Processing.service.BackgroundRemovalService;
import com.example.sea2_bora.implementation.Photo_Processing.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImageFullTest {

    private StoredImageRepository storedImageRepository;
    private KleidungsstueckRepository kleidungsstueckRepository;
    private BackgroundRemovalService backgroundRemovalService;
    private StorageProperties storageProperties;

    private ImageService imageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        storedImageRepository = mock(StoredImageRepository.class);
        kleidungsstueckRepository = mock(KleidungsstueckRepository.class);
        backgroundRemovalService = mock(BackgroundRemovalService.class);
        storageProperties = new StorageProperties();

        storageProperties.setUploadDir(tempDir.toString());

        imageService = new ImageService(
                storedImageRepository,
                kleidungsstueckRepository,
                backgroundRemovalService,
                storageProperties
        );
    }

    @Test
    void shouldProcessAndStoreImage() throws Exception {

        // echtes Testbild aus Projekt-Root laden
        Path imagePath = Path.of("test.jpg");

        assertTrue(Files.exists(imagePath), "test.jpg existiert nicht im Projekt-Root");

        byte[] imageBytes = Files.readAllBytes(imagePath);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                imageBytes
        );

        Kleidungsstueck kleidungsstueck = new Kleidungsstueck();

        when(kleidungsstueckRepository.findById(1L))
                .thenReturn(Optional.of(kleidungsstueck));

        // simuliert fertiges PNG nach Python-Verarbeitung
        doAnswer(invocation -> {
            Path outputPath = invocation.getArgument(1);

            Files.write(outputPath, imageBytes);
            return null;
        }).when(backgroundRemovalService)
                .removeBackground(any(Path.class), any(Path.class));

        when(storedImageRepository.save(any(StoredImage.class)))
                .thenAnswer(invocation -> {
                    StoredImage image = invocation.getArgument(0);

                    // simuliert DB-ID
                    var field = StoredImage.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(image, 1L);

                    return image;
                });

        ImageResponse response = imageService.storeWithoutBackground(1L, multipartFile);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test.jpg", response.getOriginalFilename());

        ArgumentCaptor<StoredImage> captor = ArgumentCaptor.forClass(StoredImage.class);

        verify(storedImageRepository).save(captor.capture());

        StoredImage savedImage = captor.getValue();

        assertEquals("image/png", savedImage.getContentType());
        assertNotNull(savedImage.getStoredPath());
        assertNotNull(savedImage.getCreatedAt());
        assertEquals(kleidungsstueck, savedImage.getKleidungsstueck());

        Path savedPath = Path.of(savedImage.getStoredPath());

        assertTrue(Files.exists(savedPath));
    }

    @Test
    void upload_pdfDatei_wirftException() throws Exception {
        MockMultipartFile pdf = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", new byte[100]);

        Kleidungsstueck kleidungsstueck = new Kleidungsstueck();
        when(kleidungsstueckRepository.findById(1L))
                .thenReturn(Optional.of(kleidungsstueck));

        assertThrows(IllegalArgumentException.class, () ->
                imageService.storeWithoutBackground(1L, pdf));
    }
}