package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.service.KleidungsstueckService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;  // Kennice
import java.util.List;

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
     * Ein neues Kleidungsstueck anlegen
     */
    @PostMapping //Kennice -> @Valid
    public String addKleidungsstueck(@Valid @RequestBody Kleidungsstueck kleidungsstueck)
    {
        service.createKleidungsstueck(kleidungsstueck);
        // .getName() greift jetzt perfekt auf die korrigierte Entity zu!
        return "Kleidungsstück " + kleidungsstueck.getName() + " wurde erfolgreich gespeichert!";
    }

    /**
     * Löscht ein Kleidungsstueck anhand seiner ID
     */
    @DeleteMapping("/{id}")
    public void deleteKleidung(@PathVariable Long id)
    {
        service.delete(id);
    }
}
