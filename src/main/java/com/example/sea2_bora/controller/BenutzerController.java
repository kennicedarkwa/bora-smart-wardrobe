package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.service.BenutzerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/benutzer")
public class BenutzerController {

    private final BenutzerService benutzerService;

    public BenutzerController(BenutzerService benutzerService) {
        this.benutzerService = benutzerService;
    }

    // REGISTRIERUNG
    @PostMapping("/registrieren")
    public ResponseEntity<String> registrieren(@RequestBody Benutzer benutzer) {

        boolean erfolgreich = benutzerService.registrieren(
                benutzer.getBenutzername(),
                benutzer.getEmail(),
                benutzer.getPasswort()
        );

        if (erfolgreich) {
            return ResponseEntity.ok("Registrierung erfolgreich");
        } else {
            return ResponseEntity.badRequest().body("Email bereits vergeben");
        }
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String email,
            @RequestParam String passwort,
            HttpSession session) {

        Benutzer benutzer = benutzerService.findByEmail(email);

        if (benutzer == null) {
            return ResponseEntity.badRequest().body("Benutzer existiert nicht");
        }

        if (!benutzerService.passwortPruefen(passwort, benutzer.getPasswort())) {
            return ResponseEntity.badRequest().body("Passwort falsch");
        }

        // Benutzer in Session speichern
        session.setAttribute("benutzerId", benutzer.getBenutzerId());

        return ResponseEntity.ok("Login erfolgreich");
    }

    // AKTUELLER BENUTZER
    @GetMapping("/aktueller")
    public ResponseEntity<?> aktuellerBenutzer(HttpSession session) {

        Long id = (Long) session.getAttribute("benutzerId");

        if (id == null) {
            return ResponseEntity.status(401).body("Nicht eingeloggt");
        }

        Benutzer benutzer = benutzerService.findById(id);

        return ResponseEntity.ok(benutzer);
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout erfolgreich");
    }
}
