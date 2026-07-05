package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.Outfit;
import com.example.sea2_bora.repository.BenutzerRepository;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import com.example.sea2_bora.repository.OutfitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class OutfitService {

    private final OutfitRepository repository;
    private final KleidungsstueckRepository kleidungsstueckRepository;
    private final BenutzerRepository benutzerRepository; // NEU hinzugefügt

    public OutfitService(OutfitRepository repository,
                         KleidungsstueckRepository kleidungsstueckRepository,
                         BenutzerRepository benutzerRepository) {
        this.repository = repository;
        this.kleidungsstueckRepository = kleidungsstueckRepository;
        this.benutzerRepository = benutzerRepository;
    }

    @Transactional
    public Outfit createOutfit(Outfit outfit, Long benutzerId) {
        // 1. Den aktuell eingeloggten Benutzer aus der DB laden
        Benutzer benutzer = benutzerRepository.findById(benutzerId)
                .orElseThrow(() -> new NoSuchElementException("Benutzer mit ID " + benutzerId + " nicht gefunden!"));

        List<Kleidungsstueck> echteBestandteile = new ArrayList<>();

        if (outfit.getBestandteile() != null) {
            for (Kleidungsstueck frontendTeil : outfit.getBestandteile()) {
                if (frontendTeil != null && frontendTeil.getId() != null) {
                    Kleidungsstueck echtesTeil = kleidungsstueckRepository.findById(frontendTeil.getId())
                            .orElseThrow(() -> new NoSuchElementException("Kleidungsstück mit ID " + frontendTeil.getId() + " nicht gefunden!"));
                    echteBestandteile.add(echtesTeil);
                }
            }
        }

        outfit.setBestandteile(echteBestandteile);

        // 2. 🌟 WICHTIG: Das Outfit fest an den Benutzer binden!
        outfit.setBenutzer(benutzer);

        return repository.save(outfit);
    }

    // Geändert: Gibt nur noch die Outfits des angemeldeten Benutzers zurück
    public List<Outfit> getAllOutfitsForUser(Long benutzerId) {
        return repository.findByBenutzer_BenutzerId(benutzerId);
    }

    public Optional<Outfit> getOutfitById(Long id) {
        return repository.findById(id);
    }

    public boolean deleteOutfit(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public Outfit updateOutfit(Long id, Outfit neueDaten) {
        return repository.findById(id).map(outfit -> {
            outfit.outfitBearbeiten(
                    neueDaten.getName(),
                    neueDaten.getAnlass(),
                    neueDaten.getBestandteile(),
                    neueDaten.getTags()
            );
            return repository.save(outfit);
        }).orElse(null);
    }
}