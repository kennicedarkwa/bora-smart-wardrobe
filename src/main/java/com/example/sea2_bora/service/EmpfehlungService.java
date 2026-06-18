package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Empfehlung;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.Tags;
import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.repository.EmpfehlungRepository;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpfehlungService
{
    private final EmpfehlungRepository empfehlungRepository;
    private final KleidungsstueckRepository kleidungsstueckRepository;

    public EmpfehlungService(EmpfehlungRepository empfehlungRepository, KleidungsstueckRepository kleidungsstueckRepository)
    {
        this.empfehlungRepository = empfehlungRepository;
        this.kleidungsstueckRepository = kleidungsstueckRepository;
    }
    //  GEÄNDERT von fatima alm. : Nimmt jetzt zusätzlich den gewählten Anlass als Text entgegen
    public Empfehlung generiereEmpfehlung(DailyForecast dailyForecast, String anlassString)
    {

        System.out.println("DEBUG: Empfange Min: " + dailyForecast.getTemperature().getMin_celsius());
        System.out.println("DEBUG: Empfange Max: " + dailyForecast.getTemperature().getMax_celsius());
        System.out.println("DEBUG: Berechneter Index ist: " + dailyForecast.getTemperature().getTemperatureIndex());

        int wetterTemperaturIndex = dailyForecast.getTemperature().getTemperatureIndex();
        boolean regen = dailyForecast.getPrecipitation().isLikely();

// NEU von fatima alm. : Wandelt den Text (z.B. "FITNESS") in das echte Java-Enum um
        Tags gewaehlterAnlassTag = Tags.valueOf(anlassString.toUpperCase());

        List<Kleidungsstueck> alleKlamotten = kleidungsstueckRepository.findAll();


        List<Kleidungsstueck> passendeKlamotten = alleKlamotten.stream()
                .filter(k -> !k.isIstImWaeschekorb()) // Nicht im Wäschekorb
                .filter(k -> k.getTemperaturIndex() == wetterTemperaturIndex) // MUSS zum Temperatur-Index passen!
                .filter(k -> !regen || k.isIstFuerRegenGeeignet()) // Regen-Filter
                .filter(k -> k.getTags() == null
                        || k.getTags().isEmpty()
                        || k.getTags().contains(gewaehlterAnlassTag)) // ✅ NEU: Wenn keine Tags da sind, TROTZDEM anzeigen!
                .toList();

        Empfehlung neueEmpfehlung = new Empfehlung(LocalDate.now());

        neueEmpfehlung.getKleidungsstuecke().addAll(passendeKlamotten);

        // ← neu Kennice
        String begruendung = "Heute " + dailyForecast.getTemperature().getAverageCelsius() + "°C"
                + (regen ? " & Regen – regengeeignete Kleidung empfohlen." : " – passende Kleidung für heute.");
        neueEmpfehlung.setBegruendung(begruendung);

        return empfehlungRepository.save(neueEmpfehlung);
    }


}
