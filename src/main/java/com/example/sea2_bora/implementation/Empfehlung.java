package com.example.sea2_bora.implementation;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Data
public class Empfehlung
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate datum;

    private String begruendung; // ← neu Kennice

    @ManyToMany
    @JoinTable(
            name = "empfehlung_kleidungsstuecke",
            joinColumns = @JoinColumn(name = "empfehlung_id"),
            inverseJoinColumns = @JoinColumn(name = "kleidungsstueck_id")
    )
    private List<Kleidungsstueck> kleidungsstuecke = new ArrayList<>();



    public Empfehlung()
    {

    }

    public Empfehlung(LocalDate datum)
    {
        this.datum = datum;
    }



    @Transient // Wird nicht in der DB gespeichert, geht aber ans Frontend raus!
    public Map<String, List<Kleidungsstueck>> getOptionenNachKategorie() {
        if (this.kleidungsstuecke == null) return Map.of();
        return this.kleidungsstuecke.stream()
                .filter(k -> k.getHauptkategorie() != null)
                .collect(Collectors.groupingBy(k -> k.getHauptkategorie().name().toUpperCase()));
    }
}
