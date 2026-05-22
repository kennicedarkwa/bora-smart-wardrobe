package com.example.sea2_bora.implementation;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Outfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outfitID;

    private String name;

    @Enumerated(EnumType.STRING)
    private Tags anlass;   // statt String

    @ManyToMany
    private List<Kleidungsstueck> bestandteile = new ArrayList<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Tags> tags = new ArrayList<>();

    public Outfit() {}

    public Outfit(String name, Tags anlass, List<Kleidungsstueck> bestandteile, List<Tags> tags) {
        this.name = name;
        this.anlass = anlass;
        this.bestandteile = bestandteile;
        this.tags = tags;
    }

    public static Outfit outfitErstellen(String name, Tags anlass, List<Kleidungsstueck> bestandteile, List<Tags> tags) {
        return new Outfit(name, anlass, bestandteile, tags);
    }

    public void outfitBearbeiten(String neuerName, Tags neuerAnlass, List<Kleidungsstueck> neueBestandteile, List<Tags> neueTags) {
        if (neuerName != null) this.name = neuerName;
        if (neuerAnlass != null) this.anlass = neuerAnlass;
        if (neueBestandteile != null) this.bestandteile = neueBestandteile;
        if (neueTags != null) this.tags = neueTags;
    }

    public boolean outfitLoeschen() {
        return true;
    }
}
