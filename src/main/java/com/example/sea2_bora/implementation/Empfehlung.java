package com.example.sea2_bora.implementation;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private List<Kleidungsstueck> kleidungsstuecke = new ArrayList<>();

    public Empfehlung()
    {

    }

    public Empfehlung(LocalDate datum)
    {
        this.datum = datum;
    }
}
