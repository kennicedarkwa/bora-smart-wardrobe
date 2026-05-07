package com.example.sea2_bora.implementation.Photo_Processing.dto;

public class ImageResponse {

    private Long id;
    private String originalFilename;
    private String storedPath;

    public ImageResponse(Long id, String originalFilename, String storedPath) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.storedPath = storedPath;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStoredPath() {
        return storedPath;
    }
}