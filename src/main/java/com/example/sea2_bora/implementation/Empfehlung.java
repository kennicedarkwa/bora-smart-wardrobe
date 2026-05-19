package com.example.sea2_bora.implementation;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Empfehlung
{
    private Long id;
    private LocalDate datum;

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
