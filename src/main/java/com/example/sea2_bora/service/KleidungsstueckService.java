package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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
}