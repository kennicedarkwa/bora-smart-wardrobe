package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.repository.BenutzerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BenutzerService {

    // + alleBenutzer: List<Benutzer>
    private final BenutzerRepository repository;

    // + aktueller Benutzer: Benutzer
    private Benutzer aktuellerBenutzer;

    public BenutzerService(BenutzerRepository repository) {
        this.repository = repository;
    }

    // + registrieren(Benutzername, Email-Adresse, Passwort): boolean
    public boolean registrieren(String benutzername, String emailAdresse, String passwort) {
        if (emailPruefen(emailAdresse)) {
            return false; // Email schon vergeben
        }
        Benutzer benutzer = new Benutzer(benutzername, emailAdresse, passwort, "unbekannt");
        repository.save(benutzer);
        return true;
    }

    // + login(Benutzer_ID): boolean
    public boolean login(Long benutzerId) {
        Optional<Benutzer> gefunden = repository.findById(benutzerId);
        if (gefunden.isPresent()) {
            aktuellerBenutzer = gefunden.get();
            return true;
        }
        return false;
    }

    // + logout(): void
    public void logout() {
        aktuellerBenutzer = null;
    }

    // + emailPruefen(email): boolean
    public boolean emailPruefen(String email) {
        return repository.findByEmail(email).isPresent();
    }

    // + getAktuellerNutzer(): Benutzer
    public Benutzer getAktuellerNutzer() {
        return aktuellerBenutzer;
    }

    // + aenderePasswort(altesPasswort, neuesPasswort)
    public boolean aenderePasswort(String altesPasswort, String neuesPasswort) {
        if (aktuellerBenutzer == null) return false;
        if (!aktuellerBenutzer.getPasswort().equals(altesPasswort)) return false;

        aktuellerBenutzer.setPasswort(neuesPasswort);
        repository.save(aktuellerBenutzer);
        return true;
    }

    // Hilfsmethode, falls du alleBenutzer aus UML explizit brauchst
    public List<Benutzer> getAlleBenutzer() {
        return repository.findAll();
    }
}