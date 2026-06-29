package com.example.sea2_bora.repository;

import com.example.sea2_bora.implementation.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    // 🌟 Filtert die Outfits in der Datenbank nach der ID des Besitzers
    List<Outfit> findByBenutzer_BenutzerId(Long benutzerId);
}
