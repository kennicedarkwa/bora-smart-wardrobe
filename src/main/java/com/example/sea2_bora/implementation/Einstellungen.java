package com.example.sea2_bora.implementation;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Einstellungen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long einstellungsID;

    private boolean einfacheAnsichtAktiv;
    private String standardStandort;
    private boolean pushBenachrichtigungAktiv;
    private boolean waescheErinnerungAktiv;
    private Date erinnerungsZeitpunkt;
    private boolean empfehlungAktiv;

    @OneToOne(mappedBy = "einstellungen")
    private Benutzer benutzer;

    protected Einstellungen() {}

    public Einstellungen(String standardStandort) {
        this.standardStandort = standardStandort;
        setzeStandardwerte();
    }

    // UML: ladeEinstellungen()
    public Einstellungen ladeEinstellungen() {
        return this;
    }

    // UML: speichereEinstellungen()
    public void speichereEinstellungen() {
        // Wird im Service gespeichert
    }

    // UML: setzeStandardwerte()
    public void setzeStandardwerte() {
        this.einfacheAnsichtAktiv = false;
        this.pushBenachrichtigungAktiv = true;
        this.waescheErinnerungAktiv = false;
        this.empfehlungAktiv = true;
        this.erinnerungsZeitpunkt = new Date();
    }

    // UML: aktiviereErinnerung()
    public void aktiviereErinnerung() {
        this.waescheErinnerungAktiv = true;
    }

    // UML: deaktiviereErinnerung()
    public void deaktiviereErinnerung() {
        this.waescheErinnerungAktiv = false;
    }

    // Getter & Setter …

    public Long getEinstellungsID() { return einstellungsID; }
    public boolean isEinfacheAnsichtAktiv() { return einfacheAnsichtAktiv; }
    public String getStandardStandort() { return standardStandort; }
    public boolean isPushBenachrichtigungAktiv() { return pushBenachrichtigungAktiv; }
    public boolean isWaescheErinnerungAktiv() { return waescheErinnerungAktiv; }
    public Date getErinnerungsZeitpunkt() { return erinnerungsZeitpunkt; }
    public boolean isEmpfehlungAktiv() { return empfehlungAktiv; }

    public void setStandardStandort(String standardStandort) { this.standardStandort = standardStandort; }
    public void setEinfacheAnsichtAktiv(boolean einfacheAnsichtAktiv) { this.einfacheAnsichtAktiv = einfacheAnsichtAktiv; }
    public void setPushBenachrichtigungAktiv(boolean pushBenachrichtigungAktiv) { this.pushBenachrichtigungAktiv = pushBenachrichtigungAktiv; }
    public void setWaescheErinnerungAktiv(boolean waescheErinnerungAktiv) { this.waescheErinnerungAktiv = waescheErinnerungAktiv; }
    public void setErinnerungsZeitpunkt(Date erinnerungsZeitpunkt) { this.erinnerungsZeitpunkt = erinnerungsZeitpunkt; }
    public void setEmpfehlungAktiv(boolean empfehlungAktiv) { this.empfehlungAktiv = empfehlungAktiv; }

    public void setEinstellungsID(Long id) {
        this.einstellungsID = id;
    }
}
