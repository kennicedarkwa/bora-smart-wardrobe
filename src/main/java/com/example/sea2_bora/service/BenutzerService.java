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

    // ⭐ NEUE PASSWORTPRÜFUNG – unterstützt Klartext + BCrypt
    public boolean passwortPruefen(String eingegeben, String gespeichert) {

        // Fall 1: Passwort ist verschlüsselt (BCrypt)
        if (gespeichert.startsWith("$2a$")) {
            return passwordEncoder.matches(eingegeben, gespeichert);
        }

        // Fall 2: Passwort ist Klartext (Testdaten)
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

    // DATEN AKTUALISIEREN
    public boolean datenAktualisieren(Long id, String neuerBenutzername, String neueEmail) {
        Benutzer benutzer = repository.findById(id).orElse(null);
        if (benutzer == null) return false;

        benutzer.setBenutzername(neuerBenutzername);
        benutzer.setEmail(neueEmail);
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
