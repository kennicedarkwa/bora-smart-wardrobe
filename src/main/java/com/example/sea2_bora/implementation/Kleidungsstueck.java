package com.example.sea2_bora.implementation;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import lombok.Data;

@Entity
@Data
public class Kleidungsstueck
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int temperaturIndex;
    private boolean istFuerRegenGeeignet;

    @Enumerated(EnumType.STRING)
    private MainCategory hauptkategorie;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Tags> tags = new ArrayList<>();

    private String bildURL;

    // Leerer Konstruktor für JPA
    public Kleidungsstueck()
    {
    }

    /**
     * Initialisiert ein neues Kleidungsstück
     */
    public Kleidungsstueck(String name, int temperaturIndex, boolean istFuerRegenGeeignet, MainCategory hauptkategorie, String bildURL)
    {
        this.name = name;
        this.temperaturIndex = temperaturIndex;
        this.istFuerRegenGeeignet = istFuerRegenGeeignet;
        this.hauptkategorie = hauptkategorie;
        this.bildURL = bildURL;
    }

    /**
     * Fügt dem Kleidungsstück einen Tag hinzu
     */
    public void addTag(Tags tag)
    {
        if (!this.tags.contains(tag))
        {
            this.tags.add(tag);
        }
    }

    /**
     * Entfernt einen Tag von einem Kleidungsstück
     */
    public void deleteTag(Tags tag)
    {
        this.tags.remove(tag);
    }
}
