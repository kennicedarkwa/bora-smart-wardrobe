package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.repository.BenutzerRepository;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate; // NEU: Für den direkten SQL-Sicherheitsgurt

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.NoSuchElementException;
import org.springframework.web.multipart.MultipartFile;
import com.example.sea2_bora.DTO.KleidungsstueckDTO;

@Service
public class KleidungsstueckService
{
    private final KleidungsstueckRepository repository;
    private final BenutzerRepository benutzerRepository;
    private final JdbcTemplate jdbcTemplate;


    public KleidungsstueckService(KleidungsstueckRepository repository, JdbcTemplate jdbcTemplate, BenutzerRepository benutzerRepository)
    {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
        this.benutzerRepository = benutzerRepository;
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
     * Gibt nur die Kleidungsstuecke des aktuell eingeloggten Benutzers aus.
     */
    @Transactional(readOnly = true)
    public List<Kleidungsstueck> getAllForUser(Long benutzerId)
    {
        Benutzer benutzer = getBenutzerOderFehler(benutzerId);
        return new ArrayList<>(benutzer.getKleiderschrank());
    }

    /**
     * Erstellt ein Kleidungsstueck und ordnet es direkt dem Kleiderschrank des Benutzers zu.
     */
    @Transactional
    public Kleidungsstueck createKleidungsstueckForUser(Kleidungsstueck kleidungsstueck, Long benutzerId)
    {
        Benutzer benutzer = getBenutzerOderFehler(benutzerId);
        Kleidungsstueck gespeichert = repository.save(kleidungsstueck);
        benutzer.kleidungsstueckHinzufuegen(gespeichert);
        benutzerRepository.save(benutzer);
        return gespeichert;
    }

    @Transactional(readOnly = true)
    public Kleidungsstueck getOwnedKleidungsstueck(Long id, Long benutzerId)
    {
        return findOwnedKleidungsstueck(getBenutzerOderFehler(benutzerId), id);
    }

    @Transactional
    public Kleidungsstueck updateForUser(Long id, Kleidungsstueck neueDaten, Long benutzerId)
    {
        Kleidungsstueck vorhanden = findOwnedKleidungsstueck(getBenutzerOderFehler(benutzerId), id);

        vorhanden.setName(neueDaten.getName());
        vorhanden.setTemperaturIndex(neueDaten.getTemperaturIndex());
        vorhanden.setIstFuerRegenGeeignet(neueDaten.isIstFuerRegenGeeignet());
        vorhanden.setIstImWaeschekorb(neueDaten.isIstImWaeschekorb());
        vorhanden.setHauptkategorie(neueDaten.getHauptkategorie());
        if (neueDaten.getTags() != null) {
            vorhanden.setTags(new ArrayList<>(neueDaten.getTags()));
        }
        if (neueDaten.getBildURL() != null) {
            vorhanden.setBildURL(neueDaten.getBildURL());
        }

        return repository.save(vorhanden);
    }

    /**
     * Löscht ein Kleidungsstueck und räumt vorher radikal Ehsans Bild-Tabelle auf!
     */
    @Transactional
    public void delete(Long id)
    {

        deleteAbhaengigeRelationen(id);


        repository.deleteById(id);
    }

    @Transactional
    public void deleteForUser(Long id, Long benutzerId)
    {
        Benutzer benutzer = getBenutzerOderFehler(benutzerId);
        Kleidungsstueck kleidungsstueck = findOwnedKleidungsstueck(benutzer, id);

        benutzer.kleidungsstueckEntfernen(kleidungsstueck);
        benutzerRepository.save(benutzer);

        deleteAbhaengigeRelationen(id);

        repository.delete(kleidungsstueck);
    }

    public Kleidungsstueck anlegen(KleidungsstueckDTO dto, MultipartFile bild) {
        return anlegenIntern(dto, bild);
    }

    @Transactional
    public Kleidungsstueck anlegen(KleidungsstueckDTO dto, MultipartFile bild, Long benutzerId) {
        Benutzer benutzer = getBenutzerOderFehler(benutzerId);
        Kleidungsstueck gespeichert = anlegenIntern(dto, bild);
        benutzer.kleidungsstueckHinzufuegen(gespeichert);
        benutzerRepository.save(benutzer);
        return gespeichert;
    }

    private Kleidungsstueck anlegenIntern(KleidungsstueckDTO dto, MultipartFile bild) {

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

    private Benutzer getBenutzerOderFehler(Long benutzerId) {
        if (benutzerId == null) {
            throw new IllegalArgumentException("Nicht eingeloggt");
        }
        return benutzerRepository.findById(benutzerId)
                .orElseThrow(() -> new NoSuchElementException("Benutzer nicht gefunden: " + benutzerId));
    }

    private Kleidungsstueck findOwnedKleidungsstueck(Benutzer benutzer, Long kleidungsstueckId) {
        return benutzer.getKleiderschrank()
                .stream()
                .filter(k -> k.getId() != null && k.getId().equals(kleidungsstueckId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Kleidungsstück gehört nicht zu diesem Benutzer: " + kleidungsstueckId));
    }

    private void deleteAbhaengigeRelationen(Long kleidungsstueckId) {
        if (jdbcTemplate == null) {
            return;
        }
        jdbcTemplate.update("DELETE FROM stored_images WHERE kleidungsstueck_id = ?", kleidungsstueckId);
        jdbcTemplate.update("DELETE FROM waechekorb_kleidungsstueck WHERE kleidungsstueck_id = ?", kleidungsstueckId);
    }
}
