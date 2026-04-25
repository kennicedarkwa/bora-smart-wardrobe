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

    // UML: registrieren(Benutzername, Email-Adresse, Passwort)

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

    // UML: login(Benutzer_ID)

    @PostMapping("/login/{id}")
    public ResponseEntity<String> login(@PathVariable Long id) {

        boolean erfolgreich = benutzerService.login(id);

        if (erfolgreich) {
            return ResponseEntity.ok("Login erfolgreich");
        } else {
            return ResponseEntity.badRequest().body("Benutzer nicht gefunden");
        }
    }

    // UML: logout()

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        benutzerService.logout();
        return ResponseEntity.ok("Logout erfolgreich");
    }

    // UML: getAktuellerNutzer()

    @GetMapping("/aktueller")
    public ResponseEntity<Benutzer> aktuellerBenutzer() {
        Benutzer benutzer = benutzerService.getAktuellerNutzer();

        if (benutzer == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(benutzer);
    }

    // UML: aenderePasswort(altesPasswort, neuesPasswort)

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

    // Zusatz: Email prüfen (UML: emailPruefen)

    @GetMapping("/email-pruefen")
    public ResponseEntity<Boolean> emailPruefen(@RequestParam String email) {
        return ResponseEntity.ok(benutzerService.emailPruefen(email));
    }
}