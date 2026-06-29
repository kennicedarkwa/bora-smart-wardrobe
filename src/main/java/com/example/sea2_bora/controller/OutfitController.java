package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Outfit;
import com.example.sea2_bora.service.OutfitService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outfits")
public class OutfitController {

    private final OutfitService service;

    public OutfitController(OutfitService service) {
        this.service = service;
    }

    // Geändert: Liest die Session aus und zeigt nur eigene Favoriten
    @GetMapping
    public ResponseEntity<?> getAllOutfits(HttpSession session) {
        Long benutzerId = (Long) session.getAttribute("benutzerId");

        if (benutzerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nicht eingeloggt");
        }

        List<Outfit> userOutfits = service.getAllOutfitsForUser(benutzerId);
        return ResponseEntity.ok(userOutfits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Outfit> getOutfit(@PathVariable Long id) {
        return service.getOutfitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Geändert: Nimmt die Session an, um das Outfit dem richtigen User zuzuweisen
    @PostMapping
    public ResponseEntity<?> createOutfit(@RequestBody Outfit outfit, HttpSession session) {
        Long benutzerId = (Long) session.getAttribute("benutzerId");

        if (benutzerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nicht eingeloggt");
        }

        return ResponseEntity.ok(service.createOutfit(outfit, benutzerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Outfit> updateOutfit(@PathVariable Long id, @RequestBody Outfit neueDaten) {
        Outfit updated = service.updateOutfit(id, neueDaten);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOutfit(@PathVariable Long id) {
        boolean deleted = service.deleteOutfit(id);
        if (deleted) return ResponseEntity.ok("Outfit gelöscht");
        return ResponseEntity.notFound().build();
    }
}