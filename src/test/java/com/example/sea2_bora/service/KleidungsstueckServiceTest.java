package com.example.sea2_bora.service;

import com.example.sea2_bora.DTO.KleidungsstueckDTO;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.MainCategory;
import com.example.sea2_bora.implementation.Tags;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * GEÄNDERT (v11):
 * - KleidungsstueckService braucht jetzt JdbcTemplate im Konstruktor
 *   → @Mock JdbcTemplate muss vorhanden sein, sonst schlägt @InjectMocks fehl.
 * - delete() ruft jetzt VOR deleteById() noch jdbcTemplate.update() auf.
 * - neue Methode anlegen(KleidungsstueckDTO, MultipartFile) → neue Tests.
 */
@ExtendWith(MockitoExtension.class)
class KleidungsstueckServiceTest {

    @Mock
    private KleidungsstueckRepository repository;

    // NEU (v11): ohne diesen Mock schlägt @InjectMocks fehl
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private KleidungsstueckService service;

    private Kleidungsstueck testStueck;

    @BeforeEach
    void setUp() {
        testStueck = new Kleidungsstueck("Weißes T-Shirt", 3, false, MainCategory.OBERTEIL, "");
    }

    // -----------------------------------------------------------------------
    // createKleidungsstueck()
    // -----------------------------------------------------------------------

    @Test
    void createKleidungsstueck_ruftSaveAuf() {
        service.createKleidungsstueck(testStueck);
        verify(repository, times(1)).save(testStueck);
    }

    @Test
    void createKleidungsstueck_speichertGenauDiesesObjekt() {
        service.createKleidungsstueck(testStueck);

        ArgumentCaptor<Kleidungsstueck> captor = ArgumentCaptor.forClass(Kleidungsstueck.class);
        verify(repository).save(captor.capture());
        assertEquals("Weißes T-Shirt", captor.getValue().getName());
        assertEquals(MainCategory.OBERTEIL, captor.getValue().getHauptkategorie());
    }

    @Test
    void createKleidungsstueck_mitTags_wirdKorrektGespeichert() {
        testStueck.addTag(Tags.ALLTAG);
        testStueck.addTag(Tags.FITNESS);

        service.createKleidungsstueck(testStueck);

        ArgumentCaptor<Kleidungsstueck> captor = ArgumentCaptor.forClass(Kleidungsstueck.class);
        verify(repository).save(captor.capture());
        assertTrue(captor.getValue().getTags().contains(Tags.ALLTAG));
        assertTrue(captor.getValue().getTags().contains(Tags.FITNESS));
    }

    // -----------------------------------------------------------------------
    // getAll()
    // -----------------------------------------------------------------------

    @Test
    void getAll_gibtAlleZurueck() {
        Kleidungsstueck zweites = new Kleidungsstueck("Jeans", 2, false, MainCategory.UNTERTEIL, "");
        when(repository.findAll()).thenReturn(List.of(testStueck, zweites));

        assertEquals(2, service.getAll().size());
    }

    @Test
    void getAll_leereDatenbank_gibtLeereListeZurueck() {
        when(repository.findAll()).thenReturn(List.of());
        assertTrue(service.getAll().isEmpty());
    }

    @Test
    void getAll_gibtExaktDieListeAusRepositoryZurueck() {
        when(repository.findAll()).thenReturn(List.of(testStueck));
        assertTrue(service.getAll().contains(testStueck));
    }

    // -----------------------------------------------------------------------
    // delete() – NEU (v11): jdbcTemplate.update() muss VOR deleteById() laufen
    // -----------------------------------------------------------------------

    @Test
    void delete_ruftDeleteByIdAuf() {
        service.delete(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void delete_ruftJdbcTemplateUpdateVorDeleteByIdAuf() {
        service.delete(5L);

        // Zuerst Bild-Tabelle bereinigen
        verify(jdbcTemplate, times(1))
                .update("DELETE FROM stored_images WHERE kleidungsstueck_id = ?", 5L);
        // Danach Kleidungsstück löschen
        verify(repository, times(1)).deleteById(5L);

        // Reihenfolge sicherstellen
        org.mockito.InOrder order = inOrder(jdbcTemplate, repository);
        order.verify(jdbcTemplate).update(anyString(), eq(5L));
        order.verify(repository).deleteById(5L);
    }

    @Test
    void delete_mitVerschiedenenIds_ruftJeweiligeIdAuf() {
        service.delete(42L);
        verify(repository, times(1)).deleteById(42L);
        verify(repository, never()).deleteById(1L);
    }

    @Test
    void delete_nichtExistierendeId_wirftKeineException() {
        doNothing().when(repository).deleteById(999L);
        assertDoesNotThrow(() -> service.delete(999L));
    }

    // -----------------------------------------------------------------------
    // anlegen(KleidungsstueckDTO, MultipartFile) – NEU in v11
    // -----------------------------------------------------------------------

    @Test
    void anlegen_ohneBild_bildURLIstNull() {
        KleidungsstueckDTO dto = new KleidungsstueckDTO(
                "Jeans", 2, false, MainCategory.UNTERTEIL, List.of(Tags.ALLTAG)
        );
        Kleidungsstueck gespeichert = new Kleidungsstueck("Jeans", 2, false, MainCategory.UNTERTEIL, null);
        when(repository.save(any(Kleidungsstueck.class))).thenReturn(gespeichert);

        Kleidungsstueck result = service.anlegen(dto, null);

        assertNotNull(result);
        assertNull(result.getBildURL());
        verify(repository, times(1)).save(any(Kleidungsstueck.class));
    }

    @Test
    void anlegen_mitLeeremBild_bildURLIstNull() {
        KleidungsstueckDTO dto = new KleidungsstueckDTO(
                "Jacke", 1, true, MainCategory.OBERTEIL, List.of()
        );
        MultipartFile leeresBild = mock(MultipartFile.class);
        when(leeresBild.isEmpty()).thenReturn(true);

        Kleidungsstueck gespeichert = new Kleidungsstueck("Jacke", 1, true, MainCategory.OBERTEIL, null);
        when(repository.save(any(Kleidungsstueck.class))).thenReturn(gespeichert);

        Kleidungsstueck result = service.anlegen(dto, leeresBild);

        assertNull(result.getBildURL());
    }

    @Test
    void anlegen_speichertKorrekteFelder() {
        KleidungsstueckDTO dto = new KleidungsstueckDTO(
                "Poloshirt", 3, false, MainCategory.OBERTEIL, List.of(Tags.ALLTAG, Tags.ELEGANT)
        );
        ArgumentCaptor<Kleidungsstueck> captor = ArgumentCaptor.forClass(Kleidungsstueck.class);
        when(repository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.anlegen(dto, null);

        Kleidungsstueck gespeichert = captor.getValue();
        assertEquals("Poloshirt", gespeichert.getName());
        assertEquals(3, gespeichert.getTemperaturIndex());
        assertFalse(gespeichert.isIstFuerRegenGeeignet());
        assertEquals(MainCategory.OBERTEIL, gespeichert.getHauptkategorie());
        assertTrue(gespeichert.getTags().contains(Tags.ALLTAG));
        assertTrue(gespeichert.getTags().contains(Tags.ELEGANT));
    }

    @Test
    void anlegen_mitBild_wirftRuntimeExceptionBeiIoFehler() throws Exception {
        KleidungsstueckDTO dto = new KleidungsstueckDTO(
                "Fehler-Shirt", 3, false, MainCategory.OBERTEIL, List.of()
        );
        MultipartFile bild = mock(MultipartFile.class);
        when(bild.isEmpty()).thenReturn(false);
        when(bild.getOriginalFilename()).thenReturn("test.jpg");
        when(bild.getBytes()).thenThrow(new IOException("Disk voll"));

        assertThrows(RuntimeException.class, () -> service.anlegen(dto, bild));
    }

    @Test
    void anlegen_ruftSaveGenauEinmalAuf() {
        KleidungsstueckDTO dto = new KleidungsstueckDTO(
                "Hose", 2, false, MainCategory.UNTERTEIL, List.of()
        );
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.anlegen(dto, null);

        verify(repository, times(1)).save(any(Kleidungsstueck.class));
    }
}