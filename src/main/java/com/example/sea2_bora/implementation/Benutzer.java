package com.example.sea2_bora.implementation;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Benutzer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long benutzerId;

    private String benutzername;
    private String email;
    private String passwort;
    private String standort;

    @ElementCollection
    private List<String> bevorzugterStyle = new ArrayList<>();

    // Digitaler Kleiderschrank
    @ManyToMany
    private List<Kleidungsstueck> kleiderschrank = new ArrayList<>();

    // Outfits des Benutzers (angepasst für die bidirektionale Verknüpfung)
    @OneToMany(mappedBy = "benutzer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Outfit> outfits = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "einstellungen_id")
    private Einstellungen einstellungen;

    // Flag für Wäsche-Erinnerung
    private boolean waescheErinnerung = false;

    protected Benutzer() {}

    public Benutzer(String benutzername, String email, String passwort, String standort) {
        this.benutzername = benutzername;
        this.email = email;
        this.passwort = passwort;
        this.standort = standort;
        this.einstellungen = new Einstellungen(standort);
    }

    // UML: outfitEmpfehlungAnfordern(anlass, datum)
    public Object outfitEmpfehlungAnfordern(String anlass, Date datum) {
        // Empfehlungssystem kommt später
        return null;
    }

    // UML: wäscheErinnerungErhalten()
    public void waescheErinnerungErhalten() {
        this.waescheErinnerung = true;
    }

    // UML: kleidungsstückHinzufügen
    public void kleidungsstueckHinzufuegen(Kleidungsstueck ks) {
        if (!kleiderschrank.contains(ks)) {
            kleiderschrank.add(ks);
        }
    }

    // UML: kleidungsstückEntfernen
    public void kleidungsstueckEntfernen(Kleidungsstueck ks) {
        kleiderschrank.remove(ks);
    }

    // UML: validiereEinstellungen()
    public boolean validiereEinstellungen() {
        return email != null && !email.isBlank()
                && passwort != null && !passwort.isBlank();
    }

    // Getter & Setter …

    public Long getBenutzerId() { return benutzerId; }
    public String getBenutzername() { return benutzername; }
    public String getEmail() { return email; }
    public String getPasswort() { return passwort; }
    public String getStandort() { return standort; }
    public List<String> getBevorzugterStyle() { return bevorzugterStyle; }
    public List<Kleidungsstueck> getKleiderschrank() { return kleiderschrank; }
    public List<Outfit> getOutfits() { return outfits; }
    public boolean isWaescheErinnerung() { return waescheErinnerung; }

    public void setPasswort(String passwort) { this.passwort = passwort; }
    public void setStandort(String standort) { this.standort = standort; }
    public void setBevorzugterStyle(List<String> bevorzugterStyle) { this.bevorzugterStyle = bevorzugterStyle; }
    public void setBenutzerId(Long benutzerId) { this.benutzerId = benutzerId; }
    public void setBenutzername(String benutzername) { this.benutzername = benutzername; }
    public void setEmail(String email) { this.email = email; }
}
