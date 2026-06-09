package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Einstellungen;
import com.example.sea2_bora.repository.EinstellungenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EinstellungenServiceTest {

    @Mock
    private EinstellungenRepository repository;

    @InjectMocks
    private EinstellungenService service;

    private Einstellungen einstellungen;

    @BeforeEach
    void setUp() {
        einstellungen = new Einstellungen("Hamburg");
        einstellungen.setEinstellungsID(1L);
    }

    // ladeEinstellungen()

    @Test
    void ladeEinstellungen_vorhandeId_gibtEinstellungenZurueck() {
        when(repository.findById(1L)).thenReturn(Optional.of(einstellungen));

        Einstellungen result = service.ladeEinstellungen(1L);

        assertNotNull(result);
        assertEquals(1L, result.getEinstellungsID());
    }

    @Test
    void ladeEinstellungen_unbekannteId_gibtNullZurueck() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertNull(service.ladeEinstellungen(99L));
    }

    // speichereEinstellungen()

    @Test
    void speichereEinstellungen_ruftSaveAuf_undGibtGespeichertesZurueck() {
        when(repository.save(einstellungen)).thenReturn(einstellungen);

        Einstellungen result = service.speichereEinstellungen(einstellungen);

        assertNotNull(result);
        verify(repository, times(1)).save(einstellungen);
    }

    // setzeStandardwerte()

    @Test
    void setzeStandardwerte_vorhandeneId_setzt_defaults_und_speichert() {
        einstellungen.setEinfacheAnsichtAktiv(true);
        einstellungen.setEmpfehlungAktiv(false);
        when(repository.findById(1L)).thenReturn(Optional.of(einstellungen));
        when(repository.save(einstellungen)).thenReturn(einstellungen);

        Einstellungen result = service.setzeStandardwerte(1L);

        assertFalse(result.isEinfacheAnsichtAktiv());
        assertTrue(result.isEmpfehlungAktiv());
        verify(repository).save(einstellungen);
    }

    @Test
    void setzeStandardwerte_unbekannteId_gibtNullZurueck_ohneException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertNull(service.setzeStandardwerte(99L));
        verify(repository, never()).save(any());
    }

    // aktiviereErinnerung()

    @Test
    void aktiviereErinnerung_vorhandeneId_setzt_flag_auf_true() {
        assertFalse(einstellungen.isWaescheErinnerungAktiv());
        when(repository.findById(1L)).thenReturn(Optional.of(einstellungen));

        service.aktiviereErinnerung(1L);

        assertTrue(einstellungen.isWaescheErinnerungAktiv());
        verify(repository).save(einstellungen);
    }

    @Test
    void aktiviereErinnerung_unbekannteId_machNichts() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.aktiviereErinnerung(99L));
        verify(repository, never()).save(any());
    }

    // deaktiviereErinnerung()

    @Test
    void deaktiviereErinnerung_vorhandeneId_setzt_flag_auf_false() {
        einstellungen.aktiviereErinnerung();
        assertTrue(einstellungen.isWaescheErinnerungAktiv());
        when(repository.findById(1L)).thenReturn(Optional.of(einstellungen));

        service.deaktiviereErinnerung(1L);

        assertFalse(einstellungen.isWaescheErinnerungAktiv());
        verify(repository).save(einstellungen);
    }

    @Test
    void deaktiviereErinnerung_unbekannteId_machNichts() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.deaktiviereErinnerung(99L));
        verify(repository, never()).save(any());
    }
}