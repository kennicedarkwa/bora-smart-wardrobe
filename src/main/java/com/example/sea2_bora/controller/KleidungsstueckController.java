package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.service.KleidungsstueckService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;  // Kennice
import java.util.List;
import java.util.NoSuchElementException;

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
    public ResponseEntity<List<Kleidungsstueck>> getAllKleidungsstueck(HttpSession session)
    {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(service.getAllForUser(benutzerId));
    }

    /**
     * Ein neues Kleidungsstueck anlegen (Gibt das Objekt zurück, damit das Frontend die ID hat!)
     */
    @PostMapping //Änderung Lorenza -> Gibt JSON zurück
    public ResponseEntity<Kleidungsstueck> addKleidungsstueck(@Valid @RequestBody Kleidungsstueck kleidungsstueck,
                                                              HttpSession session)
    {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        Kleidungsstueck gespeichert = service.createKleidungsstueckForUser(kleidungsstueck, benutzerId);
        return ResponseEntity.ok(gespeichert); // Gibt das fertige Objekt mit ID an JS zurück!
    }

    /**
     * Löscht ein Kleidungsstueck anhand seiner ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKleidung(@PathVariable Long id, HttpSession session)
    {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            service.deleteForUser(id, benutzerId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/anlegen", consumes = {"multipart/form-data"})
    public ResponseEntity<Kleidungsstueck> anlegen(
            @RequestPart("daten") KleidungsstueckDTO dto,
            @RequestPart(value = "bild", required = false) MultipartFile bild,
            HttpSession session
    ) {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        Kleidungsstueck gespeichert = service.anlegen(dto, bild, benutzerId);
        return ResponseEntity.ok(gespeichert);
    }


    /**
     * Aktualisiert den Status eines bestehenden Kleidungsstücks (z.B. für den Wäschekorb)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Kleidungsstueck> updateKleidungsstueck(@PathVariable Long id,
                                                                 @RequestBody Kleidungsstueck kleidungsstueck,
                                                                 HttpSession session)
    {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(service.updateForUser(id, kleidungsstueck, benutzerId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Long getBenutzerId(HttpSession session) {
        return (Long) session.getAttribute("benutzerId");
    }
}
