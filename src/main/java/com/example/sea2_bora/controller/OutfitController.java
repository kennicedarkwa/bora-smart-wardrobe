package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Outfit;
import com.example.sea2_bora.service.OutfitService;
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

    @GetMapping
    public List<Outfit> getAllOutfits() {
        return service.getAllOutfits();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Outfit> getOutfit(@PathVariable Long id) {
        return service.getOutfitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Outfit> createOutfit(@RequestBody Outfit outfit) {
        return ResponseEntity.ok(service.createOutfit(outfit));
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
