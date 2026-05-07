package com.example.sea2_bora.implementation.Photo_Processing.Controller;


import com.example.sea2_bora.implementation.Photo_Processing.dto.ImageResponse;
import com.example.sea2_bora.implementation.Photo_Processing.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "/remove-background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponse> uploadAndRemoveBackground(
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(imageService.storeWithoutBackground(file));
    }
}