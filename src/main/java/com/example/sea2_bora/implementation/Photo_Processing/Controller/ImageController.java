package com.example.sea2_bora.implementation.Photo_Processing.Controller;


import com.example.sea2_bora.implementation.Photo_Processing.dto.ImageResponse;
import com.example.sea2_bora.implementation.Photo_Processing.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.sea2_bora.implementation.Photo_Processing.entity.StoredImage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

//    @PostMapping(value = "/remove-background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ImageResponse> uploadAndRemoveBackground(
//            @RequestParam("file") MultipartFile file
//    ) throws Exception {
//        return ResponseEntity.ok(imageService.storeWithoutBackground(file));
//    }

    @PostMapping(
            value = "/kleidungsstuecke/{id}/image/remove-background",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ImageResponse> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(imageService.storeWithoutBackground(id, file));
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> getImage(@PathVariable Long imageId) throws Exception {
        StoredImage image = imageService.getImage(imageId);

        Path path = Paths.get(image.getStoredPath()).toAbsolutePath().normalize();
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getOriginalFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/kleidungsstuecke/{id}/images")
    public ResponseEntity<List<ImageResponse>> getImagesByKleidungsstueck(@PathVariable Long id) {
        List<ImageResponse> images = imageService.getImagesByKleidungsstueck(id)
                .stream()
                .map(image -> new ImageResponse(
                        image.getId(),
                        image.getOriginalFilename(),
                        "/image/" + image.getId()
                ))
                .toList();

        return ResponseEntity.ok(images);
    }
}