package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Waechekorb;
import com.example.sea2_bora.controller.IdsRequest;
import com.example.sea2_bora.service.WaechekorbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/waechekorb")
public class WaechekorbController {

    @Autowired
    private WaechekorbService service;

    @PostMapping
    public ResponseEntity<Waechekorb> create(@RequestParam(required = false) String name) {
        Waechekorb created = service.create(name);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Waechekorb>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Waechekorb> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Single add
    @PostMapping("/{id}/items/{itemId}")
    public ResponseEntity<Waechekorb> addItem(@PathVariable Long id, @PathVariable Long itemId) {
        return ResponseEntity.ok(service.addItem(id, itemId));
    }

    // Multi add
    @PostMapping("/{id}/items")
    public ResponseEntity<Waechekorb> addItems(@PathVariable Long id, @RequestBody IdsRequest ids) {
        return ResponseEntity.ok(service.addItems(id, ids.getIds()));
    }

    // Single remove
    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Waechekorb> removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        return ResponseEntity.ok(service.removeItem(id, itemId));
    }

    // Multi remove
    @DeleteMapping("/{id}/items")
    public ResponseEntity<Waechekorb> removeItems(@PathVariable Long id, @RequestBody IdsRequest ids) {
        return ResponseEntity.ok(service.removeItems(id, ids.getIds()));
    }

    // Clear basket
    @DeleteMapping("/{id}/clear")
    public ResponseEntity<Waechekorb> clear(@PathVariable Long id) {
        return ResponseEntity.ok(service.clear(id));
    }

    @PostMapping("/items")
    public ResponseEntity<Waechekorb> addItems(@RequestBody IdsRequest ids) {
        return ResponseEntity.ok(service.addItemsToDefaultBasket(ids.getIds()));
    }
}