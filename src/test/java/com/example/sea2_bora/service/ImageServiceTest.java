package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Photo_Processing.config.StorageProperties;
import com.example.sea2_bora.implementation.Photo_Processing.dto.ImageResponse;
import com.example.sea2_bora.implementation.Photo_Processing.entity.StoredImage;
import com.example.sea2_bora.implementation.Photo_Processing.repository.StoredImageRepository;
import com.example.sea2_bora.implementation.Photo_Processing.service.BackgroundRemovalService;
import com.example.sea2_bora.implementation.Photo_Processing.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private StoredImageRepository repository;

    @Mock
    private BackgroundRemovalService backgroundRemovalService;

    @Mock
    private StorageProperties properties;

    @InjectMocks
    private ImageService imageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // HIER DIE ÄNDERUNG: lenient() erlaubt, dass dieses Mocking
        // in Fehler-Tests ungenutzt bleiben darf.
        lenient().when(properties.getUploadDir()).thenReturn(tempDir.toString());
    }

//
//    // storeWithoutBackground() – Fehler-Fälle
//
//    @Test
//    void storeWithoutBackground_leerereDatei_wirftException() {
//        MockMultipartFile leereDate = new MockMultipartFile(
//                "file", "bild.png", "image/png", new byte[0]);
//
//        assertThrows(IllegalArgumentException.class,
//                () -> imageService.storeWithoutBackground(leereDate));
//    }
//
//    @Test
//    void storeWithoutBackground_ungueltigeEndung_wirftException() {
//        MockMultipartFile gifDatei = new MockMultipartFile(
//                "file", "bild.gif", "image/gif", new byte[]{1, 2, 3});
//
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
//                () -> imageService.storeWithoutBackground(gifDatei));
//        assertTrue(ex.getMessage().contains("PNG, JPG, JPEG und WEBP"));
//    }
//
//    @Test
//    void storeWithoutBackground_txtDatei_wirftException() {
//        MockMultipartFile txtDatei = new MockMultipartFile(
//                "file", "dokument.txt", "text/plain", "hallo".getBytes());
//
//        assertThrows(IllegalArgumentException.class,
//                () -> imageService.storeWithoutBackground(txtDatei));
//    }
//
//
//    // storeWithoutBackground() – Erfolgs-Fälle
//
//    @Test
//    void storeWithoutBackground_gueltigePng_gibtImageResponseZurueck() throws Exception {
//        MockMultipartFile pngDatei = new MockMultipartFile(
//                "file", "foto.png", "image/png", new byte[]{1, 2, 3});
//
//        doAnswer(inv -> {
//            Path out = inv.getArgument(1);
//            java.nio.file.Files.write(out, new byte[]{9, 8, 7});
//            return null;
//        }).when(backgroundRemovalService).removeBackground(any(), any());
//
//        StoredImage gespeichert = new StoredImage();
//        gespeichert.setOriginalFilename("foto.png");
//        gespeichert.setStoredPath("/some/path/output.png");
//        when(repository.save(any())).thenReturn(gespeichert);
//
//        ImageResponse response = imageService.storeWithoutBackground(pngDatei);
//
//        assertNotNull(response);
//        assertEquals("foto.png", response.getOriginalFilename());
//    }
//
//    @Test
//    void storeWithoutBackground_gueltigeJpg_wirdAkzeptiert() throws Exception {
//        MockMultipartFile jpgDatei = new MockMultipartFile(
//                "file", "foto.jpg", "image/jpeg", new byte[]{1, 2, 3});
//
//        doAnswer(inv -> {
//            Path out = inv.getArgument(1);
//            java.nio.file.Files.write(out, new byte[]{9, 8, 7});
//            return null;
//        }).when(backgroundRemovalService).removeBackground(any(), any());
//
//        StoredImage gespeichert = new StoredImage();
//        gespeichert.setOriginalFilename("foto.jpg");
//        gespeichert.setStoredPath("/some/path/output.png");
//        when(repository.save(any())).thenReturn(gespeichert);
//
//        assertDoesNotThrow(() -> imageService.storeWithoutBackground(jpgDatei));
//    }
//
//    @Test
//    void storeWithoutBackground_gueltigeWebp_wirdAkzeptiert() throws Exception {
//        MockMultipartFile webpDatei = new MockMultipartFile(
//                "file", "foto.webp", "image/webp", new byte[]{1, 2, 3});
//
//        doAnswer(inv -> {
//            Path out = inv.getArgument(1);
//            java.nio.file.Files.write(out, new byte[]{9, 8, 7});
//            return null;
//        }).when(backgroundRemovalService).removeBackground(any(), any());
//
//        StoredImage gespeichert = new StoredImage();
//        gespeichert.setOriginalFilename("foto.webp");
//        gespeichert.setStoredPath("/some/path/output.png");
//        when(repository.save(any())).thenReturn(gespeichert);
//
//        assertDoesNotThrow(() -> imageService.storeWithoutBackground(webpDatei));
//    }
//
//    @Test
//    void storeWithoutBackground_ruftRepositorySaveAuf() throws Exception {
//        MockMultipartFile pngDatei = new MockMultipartFile(
//                "file", "foto.png", "image/png", new byte[]{1, 2, 3});
//
//        doAnswer(inv -> {
//            Path out = inv.getArgument(1);
//            java.nio.file.Files.write(out, new byte[]{9, 8, 7});
//            return null;
//        }).when(backgroundRemovalService).removeBackground(any(), any());
//
//        StoredImage gespeichert = new StoredImage();
//        gespeichert.setOriginalFilename("foto.png");
//        gespeichert.setStoredPath("/some/out.png");
//        when(repository.save(any())).thenReturn(gespeichert);
//
//        imageService.storeWithoutBackground(pngDatei);
//
//        ArgumentCaptor<StoredImage> captor = ArgumentCaptor.forClass(StoredImage.class);
//        verify(repository).save(captor.capture());
//        assertEquals("foto.png", captor.getValue().getOriginalFilename());
//        assertEquals("image/png", captor.getValue().getContentType());
//        assertNotNull(captor.getValue().getCreatedAt());
//    }
//
//    @Test
//    void storeWithoutBackground_backgroundRemovalFehler_wirftException() throws Exception {
//        MockMultipartFile pngDatei = new MockMultipartFile(
//                "file", "foto.png", "image/png", new byte[]{1, 2, 3});
//
//        doThrow(new RuntimeException("Python-Fehler"))
//                .when(backgroundRemovalService).removeBackground(any(), any());
//
//        assertThrows(RuntimeException.class,
//                () -> imageService.storeWithoutBackground(pngDatei));
//
//        verify(repository, never()).save(any());
//    }

}










