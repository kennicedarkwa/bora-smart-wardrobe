package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Einstellungen;
import com.example.sea2_bora.repository.EinstellungenRepository;
import org.springframework.stereotype.Service;

@Service
public class EinstellungenService {

    private final EinstellungenRepository repository;

    public EinstellungenService(EinstellungenRepository repository) {
        this.repository = repository;
    }

    public Einstellungen ladeEinstellungen(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Einstellungen speichereEinstellungen(Einstellungen e) {
        return repository.save(e);
    }

    public Einstellungen setzeStandardwerte(Long id) {
        Einstellungen e = ladeEinstellungen(id);
        if (e != null) {
            e.setzeStandardwerte();
            repository.save(e);
        }
        return e;
    }

    public void aktiviereErinnerung(Long id) {
        Einstellungen e = ladeEinstellungen(id);
        if (e != null) {
            e.aktiviereErinnerung();
            repository.save(e);
        }
    }

    public void deaktiviereErinnerung(Long id) {
        Einstellungen e = ladeEinstellungen(id);
        if (e != null) {
            e.deaktiviereErinnerung();
            repository.save(e);
        }
    }
}
