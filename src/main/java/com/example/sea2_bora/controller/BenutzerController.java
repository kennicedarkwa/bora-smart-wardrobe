package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.service.BenutzerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/benutzer")
public class BenutzerController {

    private final BenutzerService benutzerService;

    public BenutzerController(BenutzerService benutzerService) {
        this.benutzerService = benutzerService;
    }

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

    // NEU: Login über Email + Passwort
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String email,
            @RequestParam String passwort) {

        boolean erfolgreich = benutzerService.login(email, passwort);

        if (erfolgreich) {
            return ResponseEntity.ok("Login erfolgreich");
        } else {
            return ResponseEntity.badRequest().body("Login fehlgeschlagen");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        benutzerService.logout();
        return ResponseEntity.ok("Logout erfolgreich");
    }

    @GetMapping("/aktueller")
    public ResponseEntity<Benutzer> aktuellerBenutzer() {
        Benutzer benutzer = benutzerService.getAktuellerNutzer();

        if (benutzer == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(benutzer);
    }

    @PostMapping("/passwort-aendern")
    public ResponseEntity<String> passwortAendern(
            @RequestParam String altesPasswort,
            @RequestParam String neuesPasswort) {

        boolean erfolgreich = benutzerService.aenderePasswort(altesPasswort, neuesPasswort);

        if (erfolgreich) {
            return ResponseEntity.ok("Passwort erfolgreich geändert");
        } else {
            return ResponseEntity.badRequest().body("Passwort konnte nicht geändert werden");
        }
    }

    @GetMapping("/email-pruefen")
    public ResponseEntity<Boolean> emailPruefen(@RequestParam String email) {
        return ResponseEntity.ok(benutzerService.emailPruefen(email));
    }
}
