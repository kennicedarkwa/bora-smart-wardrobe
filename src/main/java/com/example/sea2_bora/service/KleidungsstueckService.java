package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.example.sea2_bora.DTO.KleidungsstueckDTO;

@Service
public class KleidungsstueckService
{
    private final KleidungsstueckRepository repository;

    public KleidungsstueckService(KleidungsstueckRepository repository)
    {
        this.repository = repository;
    }

    /**
     * Erstellt ein Kleidungsstueck in der Datenbank
     */
    public void createKleidungsstueck(Kleidungsstueck kleidungsstueck)
    {
        repository.save(kleidungsstueck);
    }

    /**
     * Gibt alle Kleidungsstuecke aus
     */
    public List<Kleidungsstueck> getAll()
    {
        return repository.findAll();
    }

    /**
     * Löscht ein bestimmtes Kleidungsstueck anhand der Id
     */
    public void delete(Long id)
    {
        repository.deleteById(id);
    }

    public Kleidungsstueck anlegen(KleidungsstueckDTO dto, MultipartFile bild) {

        String bildURL = null;

        if (bild != null && !bild.isEmpty()) {
            try {
                String dateiname = System.currentTimeMillis() + "_" + bild.getOriginalFilename();
                Path pfad = Paths.get("uploads/" + dateiname);
                Files.createDirectories(pfad.getParent());
                Files.write(pfad, bild.getBytes());
                bildURL = "/uploads/" + dateiname;
            } catch (IOException e) {
                throw new RuntimeException("Bild konnte nicht gespeichert werden");
            }
        }

        Kleidungsstueck k = new Kleidungsstueck();
        k.setName(dto.name());
        k.setTemperaturIndex(dto.temperaturIndex());
        k.setIstFuerRegenGeeignet(dto.istFuerRegenGeeignet());
        k.setHauptkategorie(dto.hauptkategorie());
        k.setTags(dto.tags());
        k.setBildURL(bildURL);

        return repository.save(k);
    }
}