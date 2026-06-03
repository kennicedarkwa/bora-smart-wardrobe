package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Einstellungen;
import com.example.sea2_bora.service.EinstellungenService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/einstellungen")
public class EinstellungenController {

    private final EinstellungenService service;

    public EinstellungenController(EinstellungenService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Einstellungen lade(@PathVariable Long id) {
        return service.ladeEinstellungen(id);
    }

    @PutMapping("/{id}")
    public Einstellungen speichern(@PathVariable Long id, @RequestBody Einstellungen e) {
        e.setEinstellungsID(id);
        return service.speichereEinstellungen(e);
    }

    @PostMapping("/{id}/standardwerte")
    public Einstellungen standardwerte(@PathVariable Long id) {
        return service.setzeStandardwerte(id);
    }

    @PostMapping("/{id}/erinnerung/aktivieren")
    public void aktiviere(@PathVariable Long id) {
        service.aktiviereErinnerung(id);
    }

    @PostMapping("/{id}/erinnerung/deaktivieren")
    public void deaktiviere(@PathVariable Long id) {
        service.deaktiviereErinnerung(id);
    }
}
