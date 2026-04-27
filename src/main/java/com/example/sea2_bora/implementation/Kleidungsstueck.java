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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long _id;

    private String _name;
    private int _temperaturIndex; // Der Index für welche Temperatur das Kleidungsstück geeignet ist.
    private boolean _istFuerRegenGeeignet;

    //Die "Hauptkategorie": Nur eine Kategorie möglich (zb. Oberteil oder Unterteil etc.)
    @Enumerated(EnumType.STRING)
    private MainCategory _hauptkategorie;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Tags> _tags = new ArrayList<>();

    private String _bildURL; // später für die Bilder


    // Leerer Konstruktor für JPA ( !Muss drinnen sein!)
    public Kleidungsstueck()
    {
    }

    /**
     * Initialisiert ein neues Kleidungsstück
     *
     * @param name der gewünschte Name
     * @param temperaturIndex der Temperatur-Index, zu welchen das Kleidungsstück geeignet ist
     * @param istfuerRegenGeeignet true -> ees ist für Regen geeignet , false -> es ist nicht für Regen geeignet
     * @param hauptkategorie die Hauptkategorie in dem sich das Kleidungsstück einordnen lässt
     * @param bildURL die URL des Bildes
     */
    public Kleidungsstueck(String name, int temperaturIndex, boolean istfuerRegenGeeignet, MainCategory hauptkategorie, String bildURL)
    {
        _name = name;
        _temperaturIndex = temperaturIndex;
        _istFuerRegenGeeignet = istfuerRegenGeeignet;
        _hauptkategorie = hauptkategorie;
        _bildURL = bildURL;
    }

    /**
     * Fügt dem Kleidungsstück einen Tag hinzu
     *
     * @param tag der gewünschte Tag
     */
    public void addTag(Tags tag)
    {
        if(!_tags.contains(tag))
        {
            _tags.add(tag);
        }
    }

    /**
     * Entfernt einen Tag von einem Kleidungsstück
     *
     * @param tag der gewünschte Tag
     */
    public void deleteTag(Tags tag)
    {
        _tags.remove(tag);
    }

}
