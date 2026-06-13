package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.service.KleidungsstueckService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;  // Kennice
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.example.sea2_bora.DTO.KleidungsstueckDTO;

@RestController
@RequestMapping("/api/kleidung")
public class KleidungsstueckController
{
    private final KleidungsstueckService service;

    public KleidungsstueckController(KleidungsstueckService service)
    {
        this.service = service;
    }

    /**
     * Alle Kleidungsstuecke anzeigen lassen
     */
    @GetMapping
    public List<Kleidungsstueck> getAllKleidungsstueck()
    {
        return service.getAll();
    }

    /**
     * Ein neues Kleidungsstueck anlegen (Gibt das Objekt zurück, damit das Frontend die ID hat!)
     */
    @PostMapping //Änderung Lorenza -> Gibt JSON zurück
    public Kleidungsstueck addKleidungsstueck(@Valid @RequestBody Kleidungsstueck kleidungsstueck)
    {
        service.createKleidungsstueck(kleidungsstueck);
        return kleidungsstueck; // Gibt das fertige Objekt mit ID an JS zurück!
    }

    /**
     * Löscht ein Kleidungsstueck anhand seiner ID
     */
    @DeleteMapping("/{id}")
    public void deleteKleidung(@PathVariable Long id)
    {
        service.delete(id);
    }

    @PostMapping(value = "/anlegen", consumes = {"multipart/form-data"})
    public ResponseEntity<Kleidungsstueck> anlegen(
            @RequestPart("daten") KleidungsstueckDTO dto,
            @RequestPart(value = "bild", required = false) MultipartFile bild
    ) {
        Kleidungsstueck gespeichert = service.anlegen(dto, bild);
        return ResponseEntity.ok(gespeichert);
    }


    /**
     * Aktualisiert den Status eines bestehenden Kleidungsstücks (z.B. für den Wäschekorb)
     */
    @PutMapping("/{id}")
    public Kleidungsstueck updateKleidungsstueck(@PathVariable Long id, @RequestBody Kleidungsstueck kleidungsstueck)
    {
        service.createKleidungsstueck(kleidungsstueck);
        return kleidungsstueck;
    }
}
