package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Empfehlung;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.MainCategory;
import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.implementation.wetter.Precipitation;
import com.example.sea2_bora.implementation.wetter.PrecipitationType;
import com.example.sea2_bora.implementation.wetter.Temperature;
import com.example.sea2_bora.repository.EmpfehlungRepository;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testklasse für EmpfehlungService.
 *
 * EmpfehlungService filtert Kleidungsstücke nach Temperatur-Index und Regen-Eignung.
 * Mockito ersetzt Repository-Abhängigkeiten, damit kein Datenbankzugriff erfolgt.
 */
@ExtendWith(MockitoExtension.class)
class EmpfehlungServiceTest {

    @Mock
    private EmpfehlungRepository empfehlungRepository;

    @Mock
    private KleidungsstueckRepository kleidungsstueckRepository;

    @InjectMocks
    private EmpfehlungService empfehlungService;

    // Temperaturindex 3 entspricht 16-22°C (Durchschnitt)
    private DailyForecast forecastKeinRegen;
    private DailyForecast forecastMitRegen;

    private Kleidungsstueck shirtPassend;    // tempIndex=3, kein Regen nötig
    private Kleidungsstueck jackeRegen;      // tempIndex=3, regengeeignet
    private Kleidungsstueck winterjacke;     // tempIndex=1, passt nicht
    private Kleidungsstueck shirtImKorb;    // tempIndex=3, aber im Wäschekorb

    @BeforeEach
    void setUp() {
        // 18-20°C → avg 19 → Index 3
        Temperature temp = new Temperature(18, 20);
        forecastKeinRegen = new DailyForecast("2025-07-01", temp, 5,
                new Precipitation(PrecipitationType.CLEAR, 10)); // 10% → kein Regen (< 50%)
        forecastMitRegen = new DailyForecast("2025-07-02", temp, 2,
                new Precipitation(PrecipitationType.RAIN, 80));  // 80% → Regen

        shirtPassend = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        jackeRegen = new Kleidungsstueck("Regenjacke", 3, true, MainCategory.OBERTEIL, "");
        winterjacke = new Kleidungsstueck("Winterjacke", 1, false, MainCategory.OBERTEIL, "");
        shirtImKorb = new Kleidungsstueck("Schmutziges Shirt", 3, false, MainCategory.OBERTEIL, "");
    }

    // --- generiereEmpfehlung() ohne Regen ---

    @Test
    void generiereEmpfehlung_ohneRegen_gibtPassendeKleidungZurueck() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtPassend, jackeRegen, winterjacke));
        Empfehlung gespeichert = new Empfehlung(LocalDate.now());
        gespeichert.getKleidungsstuecke().addAll(List.of(shirtPassend, jackeRegen));
        when(empfehlungRepository.save(any(Empfehlung.class))).thenReturn(gespeichert);

        Empfehlung result = empfehlungService.generiereEmpfehlung(forecastKeinRegen);

        assertNotNull(result);
        // Winterjacke (Index 1) soll nicht enthalten sein
        assertFalse(result.getKleidungsstuecke().contains(winterjacke));
    }

    @Test
    void generiereEmpfehlung_ohneRegen_filtert_winterjackeHeraus() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtPassend, winterjacke));
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen);

        Empfehlung gespeichert = captor.getValue();
        assertTrue(gespeichert.getKleidungsstuecke().contains(shirtPassend));
        assertFalse(gespeichert.getKleidungsstuecke().contains(winterjacke));
    }

    // --- generiereEmpfehlung() mit Regen ---

    @Test
    void generiereEmpfehlung_mitRegen_gibtNurRegengeeigneteKleidungZurueck() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtPassend, jackeRegen));
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastMitRegen);

        Empfehlung gespeichert = captor.getValue();
        assertFalse(gespeichert.getKleidungsstuecke().contains(shirtPassend));  // nicht regengeeignet
        assertTrue(gespeichert.getKleidungsstuecke().contains(jackeRegen));
    }

    // --- Wäschekorb-Filter ---

    @Test
    void generiereEmpfehlung_itemImWaeschekorb_wirdNichtEmpfohlen() {
        // istImWaeschekorb ist bei Kleidungsstueck über ein Feld gesetzt,
        // das wir per Mockito nicht direkt setzen können, da kein Setter existiert.
        // Wir prüfen stattdessen, dass nur Items mit istImWaeschekorb==false enthalten sind.
        // shirtImKorb hat istImWaeschekorb=false per Konstruktor (default boolean=false),
        // daher nutzen wir einen Spy um das Verhalten zu simulieren.
        Kleidungsstueck spyShirt = spy(shirtImKorb);
        doReturn(true).when(spyShirt).isIstImWaeschekorb();

        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtPassend, spyShirt));
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen);

        Empfehlung gespeichert = captor.getValue();
        assertFalse(gespeichert.getKleidungsstuecke().contains(spyShirt));
        assertTrue(gespeichert.getKleidungsstuecke().contains(shirtPassend));
    }

    // --- Speicherung ---

    @Test
    void generiereEmpfehlung_ruftsave_genauEinmal_auf() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of());
        when(empfehlungRepository.save(any(Empfehlung.class))).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen);

        verify(empfehlungRepository, times(1)).save(any(Empfehlung.class));
    }

    @Test
    void generiereEmpfehlung_leereKleiderliste_gibtEmpfehlungMitLeereListe() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of());
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen);

        assertTrue(captor.getValue().getKleidungsstuecke().isEmpty());
    }

    @Test
    void generiereEmpfehlung_datumIstHeute() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of());
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen);

        assertEquals(LocalDate.now(), captor.getValue().getDatum());
    }
}