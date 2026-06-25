package com.example.sea2_bora.implementation;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
public class Kleidungsstueck
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Min(1) //Kennice
    @Max(5)//Kennice
    // ÄNDERUNG: int zu Integer
    private Integer temperaturIndex;

    // ÄNDERUNG: boolean zu Boolean
    private Boolean istFuerRegenGeeignet;

    // ÄNDERUNG: boolean zu Boolean
    private Boolean istImWaeschekorb = false;

    @NotNull //Kennice
    @Enumerated(EnumType.STRING)
    @Getter
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
     * ÄNDERUNG: Auch hier Integer und Boolean im Konstruktor verwenden!
     */
    public Kleidungsstueck(String name, Integer temperaturIndex, Boolean istFuerRegenGeeignet, MainCategory hauptkategorie, String bildURL)
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

