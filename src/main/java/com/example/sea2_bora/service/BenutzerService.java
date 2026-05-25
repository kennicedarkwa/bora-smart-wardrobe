package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.repository.BenutzerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BenutzerService {

    private final BenutzerRepository repository;
    private Benutzer aktuellerBenutzer;

    public BenutzerService(BenutzerRepository repository) {
        this.repository = repository;
    }

    public boolean registrieren(String benutzername, String email, String passwort) {
        if (emailPruefen(email)) return false;

        Benutzer benutzer = new Benutzer(benutzername, email, passwort, "unbekannt");
        repository.save(benutzer);
        return true;
    }

    public boolean login(Long benutzerId) {
        return repository.findById(benutzerId)
                .map(b -> { aktuellerBenutzer = b; return true; })
                .orElse(false);
    }

    public void logout() {
        aktuellerBenutzer = null;
    }

    public boolean emailPruefen(String email) {
        return repository.findByEmail(email).isPresent();
    }

    public Benutzer getAktuellerNutzer() {
        return aktuellerBenutzer;
    }

    public boolean aenderePasswort(String alt, String neu) {
        if (aktuellerBenutzer == null) return false;
        if (!aktuellerBenutzer.getPasswort().equals(alt)) return false;

        aktuellerBenutzer.setPasswort(neu);
        repository.save(aktuellerBenutzer);
        return true;
    }

    public List<Benutzer> getAlleBenutzer() {
        return repository.findAll();
    }
}
