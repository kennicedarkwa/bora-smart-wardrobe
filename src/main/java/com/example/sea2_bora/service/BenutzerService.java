package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.repository.BenutzerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BenutzerService {

    private final BenutzerRepository repository;
    private final PasswordEncoder passwordEncoder;

    private Benutzer aktuellerBenutzer;

    public BenutzerService(BenutzerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registrieren(String benutzername, String email, String passwort) {
        if (emailPruefen(email)) return false;

        String hashed = passwordEncoder.encode(passwort);
        Benutzer benutzer = new Benutzer(benutzername, email, hashed, "unbekannt");
        repository.save(benutzer);
        return true;
    }

    // NEU: Login über Email + Passwort
    public boolean login(String email, String passwort) {
        Optional<Benutzer> gefunden = repository.findByEmail(email);

        if (gefunden.isPresent()) {
            Benutzer b = gefunden.get();
            if (passwordEncoder.matches(passwort, b.getPasswort())) {
                aktuellerBenutzer = b;
                return true;
            }
        }
        return false;
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
        if (!passwordEncoder.matches(alt, aktuellerBenutzer.getPasswort())) return false;

        String hashedNeu = passwordEncoder.encode(neu);
        aktuellerBenutzer.setPasswort(hashedNeu);
        repository.save(aktuellerBenutzer);
        return true;
    }

    public List<Benutzer> getAlleBenutzer() {
        return repository.findAll();
    }
}
