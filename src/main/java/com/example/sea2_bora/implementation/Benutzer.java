package com.example.sea2_bora.implementation;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Benutzer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long benutzerId;              // Benutzer_ID / benutzerID

    private String benutzername;          // Benutzername / name
    private String email;          // Email-Adresse / email
    private String passwort;              // Passwort
    private String standort;              // standort

    @ElementCollection
    private List<String> bevorzugterStyle = new ArrayList<>(); // bevorzugterStyle : Liste

    // Platzhalter für Einstellungen (aus anderem Diagramm)
    // @OneToOne
    // private Einstellungen einstellungen;

    // Standardkonstruktor (für JPA)
    protected Benutzer() {}

    // UML-Konstruktor: benutzer(benutzer_id, benutzername, e-mail-adresse, passwort)
    public Benutzer(String benutzername, String email, String passwort, String standort) {
        this.benutzername = benutzername;
        this.email = email;
        this.passwort = passwort;
        this.standort = standort;
    }

    // UML-Getter

    public Long getBenutzerId() {
        return benutzerId;
    }

    public String getBenutzername() {
        return benutzername;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswort() {
        return passwort;
    }

    public String getStandort() {
        return standort;
    }

    public List<String> getBevorzugterStyle() {
        return bevorzugterStyle;
    }

    // UML-Methoden aus dem Kleidung/UML-Diagramm

    // + outfitEmpfehlungAnfordern(anlass : Zeichenkette, datum : Date) : Empfehlung
    public Object outfitEmpfehlungAnfordern(String anlass, java.util.Date datum) {
        // TODO: später mit echter Empfehlung-Klasse ersetzen
        return null;
    }

    // + wäscheErinnerungErhalten() : void
    public void waescheErinnerungErhalten() {
        // TODO: z.B. Notification auslösen / Flag setzen
    }

    // + KleidungsstückHinzufügen(Kleidungsstück) : void
    public void kleidungsstueckHinzufuegen(Object kleidungsstueck) {
        // TODO: Beziehung zu Kleidungsstück-Liste ergänzen
    }

    // + KleidungsstückEntfernen(Kleidungsstück) : void
    public void kleidungsstueckEntfernen(Object kleidungsstueck) {
        // TODO: Beziehung zu Kleidungsstück-Liste ergänzen
    }

    // + validiereEinstellungen() : Wahrheitswert
    public boolean validiereEinstellungen() {
        return email != null && !email.isBlank()
                && passwort != null && !passwort.isBlank();
    }

    // Setter, falls benötigt

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public void setStandort(String standort) {
        this.standort = standort;
    }

    public void setBevorzugterStyle(List<String> bevorzugterStyle) {
        this.bevorzugterStyle = bevorzugterStyle;
    }

    public void setBenutzerId(Long benutzerId) {

        this.benutzerId = benutzerId;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

