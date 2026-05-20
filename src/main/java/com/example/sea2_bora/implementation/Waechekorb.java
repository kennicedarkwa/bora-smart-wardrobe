package com.example.sea2_bora.implementation;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Waechekorb {


//    Waechekorb und Kleidungsstueck: Many-to-Many (eine Liste items im Waechekorb).
//    Repositories sind Spring-Data-Interfaces; Services verwenden die Repositories.
//    Controller exponiert REST-Endpunkte und ruft WaechekorbService auf.
//    IdsRequest ist das DTO für Multi-Select-Operationen (JSON: {"ids":[1,2,3]}).
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // optionaler Name / Beschreibung des Korbs
    private String name;

    // Beziehung zu Kleidungsstueck (ManyToMany, JoinTable)
    @ManyToMany
    @JoinTable(
            name = "waechekorb_kleidungsstueck",
            joinColumns = @JoinColumn(name = "waechekorb_id"),
            inverseJoinColumns = @JoinColumn(name = "kleidungsstueck_id")
    )
    private List<Kleidungsstueck> items = new ArrayList<>();

    public Waechekorb() {}

    public Waechekorb(String name) {
        this.name = name;
    }

    public void addItem(Kleidungsstueck k) {
        if (k != null && !items.contains(k)) {
            items.add(k);
        }
    }
//
//    // NEU: Filter nach Temperatur + Regen
//    public List<Kleidungsstueck> filterByWeather(int temperaturIndex, boolean regen) {
//        return items.stream()
//                .filter(k -> k.getTemperaturIndex() == temperaturIndex)
//                .filter(k -> !regen || k.isIstFuerRegenGeeignet())
//                .toList();
//    }
//
//    // NEU: Alle Items die nicht im Wäschekorb sind + Wetter-Filter
//    public static List<Kleidungsstueck> filterAvailableByWeather
//    (List<Kleidungsstueck> allItems, int temperaturIndex, boolean regen) {
//        return allItems.stream()
//                .filter(k -> !k.isIstImWaeschekorb())
//                .filter(k -> k.getTemperaturIndex() == temperaturIndex)
//                .filter(k -> !regen || k.isIstFuerRegenGeeignet())
//                .toList();
//    }

    public void addItems(Iterable<Kleidungsstueck> ks) {
        ks.forEach(this::addItem);
    }

    public void removeItem(Kleidungsstueck k) {
        items.remove(k);
    }

    public void removeItemsByIds(Iterable<Kleidungsstueck> ks) {
        ks.forEach(items::remove);
    }

    public void clear() {
        items.clear();
    }
}