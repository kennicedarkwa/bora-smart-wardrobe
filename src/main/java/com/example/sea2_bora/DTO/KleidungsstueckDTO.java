package com.example.sea2_bora.DTO;

import com.example.sea2_bora.implementation.MainCategory;
import com.example.sea2_bora.implementation.Tags;
import java.util.List;

// Ein record ist eine kompakte Datenklasse (perfekt für DTOs).
// Sie ist unveränderlich und erzeugt automatisch Getter, equals, hashCode, toString.
public record KleidungsstueckDTO(
        String name,
        int temperaturIndex,
        boolean istFuerRegenGeeignet,
        MainCategory hauptkategorie,
        List<Tags> tags
) {}
