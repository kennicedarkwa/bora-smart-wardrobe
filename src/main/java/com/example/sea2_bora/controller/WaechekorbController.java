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



    // POST /api/waechekorb -> Holt oder erstellt den Singleton-Korb
    @PostMapping
    public ResponseEntity<Waechekorb> getOrCreate() {
        return ResponseEntity.ok(service.getSingletonWaechekorb());
    }

    // GET /api/waechekorb -> Holt den Singleton-Korb
    @GetMapping
    public ResponseEntity<Waechekorb> get() {
        return ResponseEntity.ok(service.getSingletonWaechekorb());
    }

    // DELETE /api/waechekorb -> Ist jetzt deaktiviert
    @DeleteMapping
    public ResponseEntity<Void> delete() {
        // service.delete() wirft eine Exception, um Löschen zu verhindern
        try {
            service.delete();
            return ResponseEntity.noContent().build();
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(405).build(); // 405 Method Not Allowed
        }
    }

    // POST /api/waechekorb/items/{itemId} -> Fügt Item zum Singleton hinzu
    @PostMapping("/items/{itemId}")
    public ResponseEntity<Waechekorb> addItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.addItem(itemId));
    }

    // POST /api/waechekorb/items -> Fügt mehrere Items zum Singleton hinzu
    @PostMapping("/items")
    public ResponseEntity<Waechekorb> addItems(@RequestBody IdsRequest ids) {
        return ResponseEntity.ok(service.addItems(ids.getIds()));
    }

    // DELETE /api/waechekorb/items/{itemId} -> Entfernt Item aus Singleton
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Waechekorb> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.removeItem(itemId));
    }

    // DELETE /api/waechekorb/items -> Entfernt mehrere Items aus Singleton
    @DeleteMapping("/items")
    public ResponseEntity<Waechekorb> removeItems(@RequestBody IdsRequest ids) {
        return ResponseEntity.ok(service.removeItems(ids.getIds()));
    }

    // DELETE /api/waechekorb/clear -> Leert den Singleton-Korb
    @DeleteMapping("/clear")
    public ResponseEntity<Waechekorb> clear() {
        return ResponseEntity.ok(service.clear());
    }


//    @PostMapping
//    public ResponseEntity<Waechekorb> create(@RequestParam(required = false) String name) {
//        Waechekorb created = service.create(name);
//        return ResponseEntity.ok(created);
//    }

//
//    @GetMapping
//    public ResponseEntity<List<Waechekorb>> getAll() {
//        return ResponseEntity.ok(service.getAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Waechekorb> getById(@PathVariable Long id) {
//        return service.getById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        service.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // Single add
//    @PostMapping("/{id}/items/{itemId}")
//    public ResponseEntity<Waechekorb> addItem(@PathVariable Long id, @PathVariable Long itemId) {
//        return ResponseEntity.ok(service.addItem(id, itemId));
//    }
//
//    // Multi add
//    @PostMapping("/{id}/items")
//    public ResponseEntity<Waechekorb> addItems(@PathVariable Long id, @RequestBody IdsRequest ids) {
//        return ResponseEntity.ok(service.addItems(id, ids.getIds()));
//    }
//
//    // Single remove
//    @DeleteMapping("/{id}/items/{itemId}")
//    public ResponseEntity<Waechekorb> removeItem(@PathVariable Long id, @PathVariable Long itemId) {
//        return ResponseEntity.ok(service.removeItem(id, itemId));
//    }
//
//    // Multi remove
//    @DeleteMapping("/{id}/items")
//    public ResponseEntity<Waechekorb> removeItems(@PathVariable Long id, @RequestBody IdsRequest ids) {
//        return ResponseEntity.ok(service.removeItems(id, ids.getIds()));
//    }
//
//    // Clear basket
//    @DeleteMapping("/{id}/clear")
//    public ResponseEntity<Waechekorb> clear(@PathVariable Long id) {
//        return ResponseEntity.ok(service.clear(id));
//    }
//
//    @PostMapping("/items")
//    public ResponseEntity<Waechekorb> addItems(@RequestBody IdsRequest ids) {
//        return ResponseEntity.ok(service.addItemsToDefaultBasket(ids.getIds()));
//    }



}