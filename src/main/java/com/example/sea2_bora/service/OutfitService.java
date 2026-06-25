package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.Outfit;
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

    public OutfitService(OutfitRepository repository, KleidungsstueckRepository kleidungsstueckRepository) {
        this.repository = repository;
        this.kleidungsstueckRepository = kleidungsstueckRepository;
    }

    @Transactional
    public Outfit createOutfit(Outfit outfit) {
        List<Kleidungsstueck> echteBestandteile = new ArrayList<>();

        // Sicherheitscheck: Falls die Liste der Bestandteile komplett null ist
        if (outfit.getBestandteile() != null) {

            // 1. Wir gehen durch alle vom Frontend geschickten Kleidungsstücke
            for (Kleidungsstueck frontendTeil : outfit.getBestandteile()) {

                // NEU: Nur in die Datenbank schauen, wenn das Objekt und die ID existieren!
                if (frontendTeil != null && frontendTeil.getId() != null) {

                    // 2. Wir holen das echte, vollständige Kleidungsstück aus der Datenbank
                    Kleidungsstueck echtesTeil = kleidungsstueckRepository.findById(frontendTeil.getId())
                            .orElseThrow(() -> new NoSuchElementException("Kleidungsstück mit ID " + frontendTeil.getId() + " nicht gefunden!"));

                    echteBestandteile.add(echtesTeil);
                }
                // Leere Slots im Frontend (ID = null) werden jetzt einfach übersprungen, ohne abzustürzen!
            }
        }

        // 3. Wir ersetzen die unvollständigen Frontend-Teile durch unsere echten Datenbank-Teile
        outfit.setBestandteile(echteBestandteile);

        // 4. Jetzt können wir das Outfit fehlerfrei speichern!
        return repository.save(outfit);
    }

    public List<Outfit> getAllOutfits() {
        return repository.findAll();
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