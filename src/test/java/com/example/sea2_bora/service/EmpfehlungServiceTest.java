package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Empfehlung;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.MainCategory;
import com.example.sea2_bora.implementation.Tags;
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
 * GEÄNDERT (v11): generiereEmpfehlung() nimmt jetzt zusätzlich einen
 * anlassString (z.B. "ALLTAG") entgegen und filtert Kleidungsstücke nach
 * ihrem Tags-Enum. Alle Tests wurden um diesen Parameter ergänzt.
 */
@ExtendWith(MockitoExtension.class)
class EmpfehlungServiceTest {

    @Mock
    private EmpfehlungRepository empfehlungRepository;

    @Mock
    private KleidungsstueckRepository kleidungsstueckRepository;

    @InjectMocks
    private EmpfehlungService empfehlungService;

    // 18-20°C → avg 19 → Index 3
    private DailyForecast forecastKeinRegen;
    private DailyForecast forecastMitRegen;

    // Index 3, ALLTAG-Tag, kein Regen nötig
    private Kleidungsstueck shirtAlltag;
    // Index 3, ALLTAG-Tag, regengeeignet
    private Kleidungsstueck jackeRegenAlltag;
    // Index 1, ALLTAG-Tag – passt temperaturmäßig nicht
    private Kleidungsstueck winterjacke;
    // Index 3, FITNESS-Tag – passt nicht zum Anlass ALLTAG
    private Kleidungsstueck sportshirt;
    // Index 3, ALLTAG-Tag – aber im Wäschekorb
    private Kleidungsstueck shirtImKorb;

    @BeforeEach
    void setUp() {
        Temperature temp = new Temperature(18, 20);
        forecastKeinRegen = new DailyForecast("2025-07-01", temp, 5,
                new Precipitation(PrecipitationType.CLEAR, 10));
        forecastMitRegen = new DailyForecast("2025-07-02", temp, 2,
                new Precipitation(PrecipitationType.RAIN, 80));

        shirtAlltag = new Kleidungsstueck("Shirt", 3, false, MainCategory.OBERTEIL, "");
        shirtAlltag.addTag(Tags.ALLTAG);

        jackeRegenAlltag = new Kleidungsstueck("Regenjacke", 3, true, MainCategory.OBERTEIL, "");
        jackeRegenAlltag.addTag(Tags.ALLTAG);

        winterjacke = new Kleidungsstueck("Winterjacke", 1, false, MainCategory.OBERTEIL, "");
        winterjacke.addTag(Tags.ALLTAG);

        sportshirt = new Kleidungsstueck("Sportshirt", 3, false, MainCategory.OBERTEIL, "");
        sportshirt.addTag(Tags.FITNESS);

        shirtImKorb = new Kleidungsstueck("Schmutziges Shirt", 3, false, MainCategory.OBERTEIL, "");
        shirtImKorb.addTag(Tags.ALLTAG);
    }

    // -----------------------------------------------------------------------
    // Anlass-Filter (NEU v11)
    // -----------------------------------------------------------------------

    @Test
    void generiereEmpfehlung_anlass_filtert_falschenTag() {
        // Sportshirt hat nur FITNESS → darf bei Anlass ALLTAG NICHT erscheinen
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtAlltag, sportshirt));
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen, "ALLTAG");

        Empfehlung gespeichert = captor.getValue();
        assertTrue(gespeichert.getKleidungsstuecke().contains(shirtAlltag));
        assertFalse(gespeichert.getKleidungsstuecke().contains(sportshirt));
    }

    @Test
    void generiereEmpfehlung_anlass_FITNESS_gibtNurFitnessKleidung() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtAlltag, sportshirt));
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen, "FITNESS");

        Empfehlung gespeichert = captor.getValue();
        assertFalse(gespeichert.getKleidungsstuecke().contains(shirtAlltag));
        assertTrue(gespeichert.getKleidungsstuecke().contains(sportshirt));
    }

    @Test
    void generiereEmpfehlung_ungueltigerAnlass_wirftException() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtAlltag));

        assertThrows(IllegalArgumentException.class, () ->
                empfehlungService.generiereEmpfehlung(forecastKeinRegen, "UNGUELTIG")
        );
    }

    @Test
    void generiereEmpfehlung_anlassKleingeschrieben_wirdNormalisiert() {
        // Tags.valueOf() wird mit toUpperCase() aufgerufen → "alltag" soll funktionieren
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtAlltag));
        when(empfehlungRepository.save(any(Empfehlung.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() ->
                empfehlungService.generiereEmpfehlung(forecastKeinRegen, "alltag")
        );
    }

    // -----------------------------------------------------------------------
    // Temperatur- und Regen-Filter (bestehend, mit anlass-Parameter ergänzt)
    // -----------------------------------------------------------------------

    @Test
    void generiereEmpfehlung_ohneRegen_gibtPassendeKleidungZurueck() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtAlltag, jackeRegenAlltag, winterjacke));
        when(empfehlungRepository.save(any(Empfehlung.class))).thenAnswer(inv -> inv.getArgument(0));

        Empfehlung result = empfehlungService.generiereEmpfehlung(forecastKeinRegen, "ALLTAG");

        assertNotNull(result);
        assertFalse(result.getKleidungsstuecke().contains(winterjacke));
    }

    @Test
    void generiereEmpfehlung_ohneRegen_filtert_winterjackeHeraus() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtAlltag, winterjacke));
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen, "ALLTAG");

        Empfehlung gespeichert = captor.getValue();
        assertTrue(gespeichert.getKleidungsstuecke().contains(shirtAlltag));
        assertFalse(gespeichert.getKleidungsstuecke().contains(winterjacke));
    }

    @Test
    void generiereEmpfehlung_mitRegen_gibtNurRegengeeigneteKleidungZurueck() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtAlltag, jackeRegenAlltag));
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastMitRegen, "ALLTAG");

        Empfehlung gespeichert = captor.getValue();
        assertFalse(gespeichert.getKleidungsstuecke().contains(shirtAlltag));
        assertTrue(gespeichert.getKleidungsstuecke().contains(jackeRegenAlltag));
    }

    // -----------------------------------------------------------------------
    // Wäschekorb-Filter
    // -----------------------------------------------------------------------

    @Test
    void generiereEmpfehlung_itemImWaeschekorb_wirdNichtEmpfohlen() {
        Kleidungsstueck spyShirt = spy(shirtImKorb);
        doReturn(true).when(spyShirt).getIstImWaeschekorb();

        when(kleidungsstueckRepository.findAll()).thenReturn(List.of(shirtAlltag, spyShirt));
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen, "ALLTAG");

        Empfehlung gespeichert = captor.getValue();
        assertFalse(gespeichert.getKleidungsstuecke().contains(spyShirt));
        assertTrue(gespeichert.getKleidungsstuecke().contains(shirtAlltag));
    }

    // -----------------------------------------------------------------------
    // Begruendung (NEU v11 – gesetzt von Kennice)
    // -----------------------------------------------------------------------

    @Test
    void generiereEmpfehlung_begruendungIstGesetzt() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of());
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen, "ALLTAG");

        assertNotNull(captor.getValue().getBegruendung());
        assertFalse(captor.getValue().getBegruendung().isBlank());
    }

    @Test
    void generiereEmpfehlung_mitRegen_begruendungEnthaeltRegenHinweis() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of());
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastMitRegen, "ALLTAG");

        assertTrue(captor.getValue().getBegruendung().contains("Regen"));
    }

    // -----------------------------------------------------------------------
    // Speicherung
    // -----------------------------------------------------------------------

    @Test
    void generiereEmpfehlung_ruftsave_genauEinmal_auf() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of());
        when(empfehlungRepository.save(any(Empfehlung.class))).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen, "ALLTAG");

        verify(empfehlungRepository, times(1)).save(any(Empfehlung.class));
    }

    @Test
    void generiereEmpfehlung_leereKleiderliste_gibtEmpfehlungMitLeereListe() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of());
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen, "ALLTAG");

        assertTrue(captor.getValue().getKleidungsstuecke().isEmpty());
    }

    @Test
    void generiereEmpfehlung_datumIstHeute() {
        when(kleidungsstueckRepository.findAll()).thenReturn(List.of());
        ArgumentCaptor<Empfehlung> captor = ArgumentCaptor.forClass(Empfehlung.class);
        when(empfehlungRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        empfehlungService.generiereEmpfehlung(forecastKeinRegen, "ALLTAG");

        assertEquals(LocalDate.now(), captor.getValue().getDatum());
    }
}