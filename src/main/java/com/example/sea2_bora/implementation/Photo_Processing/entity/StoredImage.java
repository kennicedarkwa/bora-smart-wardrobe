package com.example.sea2_bora.implementation.Photo_Processing.entity;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "stored_images")
public class StoredImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String originalFilename;

    @Setter
    @Column(nullable = false, unique = true, length = 1000)
    private String storedPath;

    @Setter
    @Column(nullable = false)
    private String contentType;

    @Setter
    @Column(nullable = false)
    private long fileSize;

    @Setter
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "kleidungsstueck_id", nullable = true)
    private Kleidungsstueck kleidungsstueck;

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }


    public String getStoredPath() {
        return storedPath;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Kleidungsstueck getKleidungsstueck() {
        return kleidungsstueck;
    }

}