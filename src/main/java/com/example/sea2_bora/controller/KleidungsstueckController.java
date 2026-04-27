package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.service.KleidungsstueckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kleidung")
public class KleidungsstueckController
{
    @Autowired
    private KleidungsstueckService _service;

    /**
     * Alle Kleidungsstuecke anzeigen lassen
     *
     * @return alle Kleidungsstuecke
     */
    @GetMapping // (GET http://localhost:8080/api/kleidung)
    public List<Kleidungsstueck> getAllKleidungsstueck()
    {
        return _service.getAll();
    }

    /**
     * Ein neues Kleidungsstueck anlegen
     */
    @PostMapping // (POST http://localhost:8080/api/kleidung)
    public String addKleidungsstueck(@RequestBody Kleidungsstueck kleidungsstueck)
    {
        _service.createKleidungsstueck(kleidungsstueck);
        return "Kleidungsstück " + kleidungsstueck.get_name() + " wurde erfolgreich gespeichert!";
    }

    /**
     * Löscht ein Kleidungsstueck anhand seiner ID
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    public void deleteKleidung(@PathVariable Long id)
    {
        _service.delete(id);
    }
}
