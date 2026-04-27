package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.MainCategory;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired; // Der neue Import!
import java.util.List;

@Service
public class KleidungsstueckService
{
    @Autowired
    private KleidungsstueckRepository _repository;

    /**
     * Erstellt ein Kleidungsstueck in der Datenbank
     *
     * @param kleidungsstueck
     */
    public void createKleidungsstueck(Kleidungsstueck kleidungsstueck)
    {
        _repository.save(kleidungsstueck);
    }

    /**
     * Gibt alle Kleidungsstuecke aus
     *
     * @return alle Kleidungssteucke
     */
    public List<Kleidungsstueck> getAll()
    {
        return _repository.findAll();
    }

    /**
     * Löscht ein bestimmtes Kleidungsstueck anhand der Id
     *
     * @param id
     */
    public void delete( Long id )
    {
        _repository.deleteById(id);
    }
}
