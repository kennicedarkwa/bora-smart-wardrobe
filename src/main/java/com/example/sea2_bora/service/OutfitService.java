package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Outfit;
import com.example.sea2_bora.repository.OutfitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OutfitService {

    private final OutfitRepository repository;

    public OutfitService(OutfitRepository repository) {
        this.repository = repository;
    }

    public Outfit createOutfit(Outfit outfit) {
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
