package com.example.sea2_bora.implementation.Photo_Processing.repository;


import com.example.sea2_bora.implementation.Photo_Processing.entity.StoredImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredImageRepository extends JpaRepository<StoredImage, Long> {
}