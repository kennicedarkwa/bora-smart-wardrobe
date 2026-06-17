package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.repository.BenutzerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BenutzerService {

    private final BenutzerRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;

    public BenutzerService(BenutzerRepository repository,
                           PasswordEncoder passwordEncoder,
                           HttpSession session) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.session = session;
    }

    // REGISTRIERUNG
    public boolean registrieren(String benutzername, String email, String passwort) {
        if (emailPruefen(email)) return false;

        String hashed = passwordEncoder.encode(passwort);
        Benutzer benutzer = new Benutzer(benutzername, email, hashed, "unbekannt");
        repository.save(benutzer);
        return true;
    }

    // LOGIN
    public boolean login(String email, String passwort) {
        Optional<Benutzer> gefunden = repository.findByEmail(email);

        if (gefunden.isEmpty()) return false;

        Benutzer benutzer = gefunden.get();

        // ⭐ WICHTIG: Passwortprüfung für Klartext + BCrypt
        if (!passwortPruefen(passwort, benutzer.getPasswort())) {
            return false;
        }

        session.setAttribute("benutzerId", benutzer.getBenutzerId());
        return true;
    }

    // BENUTZER PER EMAIL FINDEN
    public Benutzer findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    // BENUTZER PER ID FINDEN
    public Benutzer findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    // ⭐ VERBESSERTE PASSWORTPRÜFUNG – Unterstützt alle BCrypt-Varianten ($2a, $2b, $2y)
    public boolean passwortPruefen(String eingegeben, String gespeichert) {
        if (gespeichert == null) return false;

        // BCrypt-Hashes starten immer mit $2 und sind exakt 60 Zeichen lang
        if (gespeichert.startsWith("$2") && gespeichert.length() == 60) {
            return passwordEncoder.matches(eingegeben, gespeichert);
        }

        // Fall 2: Passwort ist Klartext (alte Testdaten)
        return gespeichert.equals(eingegeben);
    }

    // AKTUELLER BENUTZER
    public Benutzer getAktuellerNutzer() {
        Long id = (Long) session.getAttribute("benutzerId");

        if (id == null) return null;

        return repository.findById(id).orElse(null);
    }

    // LOGOUT
    public void logout() {
        session.invalidate();
    }

    // EMAIL PRÜFEN
    public boolean emailPruefen(String email) {
        return repository.findByEmail(email).isPresent();
    }

    // ⭐ VERBESSERTES DATEN-AKTUALISIEREN – Schützt vor NULL-Werten und E-Mail-Duplikaten
    public boolean datenAktualisieren(Long id, String neuerBenutzername, String neueEmail) {
        Benutzer benutzer = repository.findById(id).orElse(null);
        if (benutzer == null) return false;

        // Nur aktualisieren, wenn der Wert im Request auch wirklich mitgeschickt wurde!
        if (neuerBenutzername != null && !neuerBenutzername.trim().isEmpty()) {
            benutzer.setBenutzername(neuerBenutzername);
        }

        if (neueEmail != null && !neueEmail.trim().isEmpty()) {
            // Sicherheitscheck: Gehört die E-Mail vielleicht schon einem ANDEREN Nutzer?
            Optional<Benutzer> bestehenderNutzer = repository.findByEmail(neueEmail);
            if (bestehenderNutzer.isPresent() && !bestehenderNutzer.get().getBenutzerId().equals(id)) {
                // Abbrechen, da die E-Mail schon vergeben ist
                return false;
            }
            benutzer.setEmail(neueEmail);
        }

        repository.save(benutzer);
        return true;
    }

    // PASSWORT ÄNDERN (Mit Verschlüsselung!)
    public void passwortAktualisieren(Long id, String neuesPasswort) {
        Benutzer benutzer = repository.findById(id).orElseThrow();
        // Hier passiert die Magie: Das neue Passwort wird gehashed!
        String hashed = passwordEncoder.encode(neuesPasswort);
        benutzer.setPasswort(hashed);
        repository.save(benutzer);
    }

    // ACCOUNT LÖSCHEN (Mit Auflösung der Tabellen-Relationen)
    public void accountLoeschen(Long id) {
        Benutzer benutzer = repository.findById(id).orElse(null);
        if (benutzer != null) {
            // WICHTIG: Relationen auflösen, um Foreign-Key Constraint Errors in der Datenbank zu vermeiden!
            benutzer.getKleiderschrank().clear();
            benutzer.getOutfits().clear();
            repository.save(benutzer); // Speichern löscht die Einträge aus der Join-Tabelle

            // Jetzt kann der Benutzer sicher gelöscht werden
            repository.delete(benutzer);
        }
    }
}
