package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Waechekorb;
import com.example.sea2_bora.controller.IdsRequest;
import com.example.sea2_bora.service.WaechekorbService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@CrossOrigin
@RequestMapping("/api/waechekorb")
public class WaechekorbController {

    @Autowired
    private WaechekorbService service;

    // POST /api/waechekorb -> Holt den Wäschekorb des eingeloggten Benutzers
    @PostMapping
    public ResponseEntity<Waechekorb> getOrCreate(HttpSession session) {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(service.getWaechekorbForUser(benutzerId));
    }

    // GET /api/waechekorb -> Holt den Wäschekorb des eingeloggten Benutzers
    @GetMapping
    public ResponseEntity<Waechekorb> get(HttpSession session) {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(service.getWaechekorbForUser(benutzerId));
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

    // POST /api/waechekorb/items/{itemId} -> Fügt eigenes Item zum Wäschekorb hinzu
    @PostMapping("/items/{itemId}")
    public ResponseEntity<Waechekorb> addItem(@PathVariable Long itemId, HttpSession session) {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(service.addItemForUser(benutzerId, itemId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/waechekorb/items -> Fügt mehrere eigene Items zum Wäschekorb hinzu
    @PostMapping("/items")
    public ResponseEntity<Waechekorb> addItems(@RequestBody IdsRequest ids, HttpSession session) {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(service.addItemsForUser(benutzerId, ids.getIds()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/waechekorb/items/{itemId} -> Entfernt eigenes Item aus dem Wäschekorb
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Waechekorb> removeItem(@PathVariable Long itemId, HttpSession session) {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(service.removeItemForUser(benutzerId, itemId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/waechekorb/items -> Entfernt mehrere eigene Items aus dem Wäschekorb
    @DeleteMapping("/items")
    public ResponseEntity<Waechekorb> removeItems(@RequestBody IdsRequest ids, HttpSession session) {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(service.removeItemsForUser(benutzerId, ids.getIds()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/waechekorb/clear -> Leert den Wäschekorb des eingeloggten Benutzers
    @DeleteMapping("/clear")
    public ResponseEntity<Waechekorb> clear(HttpSession session) {
        Long benutzerId = getBenutzerId(session);
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(service.clearForUser(benutzerId));
    }

    private Long getBenutzerId(HttpSession session) {
        return (Long) session.getAttribute("benutzerId");
    }
}
