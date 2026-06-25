package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.Waechekorb;
import com.example.sea2_bora.repository.BenutzerRepository;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import com.example.sea2_bora.repository.WaechekorbRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WaechekorbService {

    @Autowired
    private WaechekorbRepository waechekorbRepository;

    @Autowired
    private KleidungsstueckRepository kleidungsstueckRepository;

    @Autowired
    private BenutzerRepository benutzerRepository;

    public Waechekorb create(String name) {
        Waechekorb k = new Waechekorb(name);
        return waechekorbRepository.save(k);
    }
    /**     * Holt den einzigen Wäschekorb im System.     * Wenn keiner existiert, wird er erstellt.     * @return Der Singleton-Wäschekorb.     */
    public Waechekorb getSingletonWaechekorb() {
        // findFirst ist effizienter als findAll().get(0)
        return waechekorbRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    Waechekorb neuerKorb = new Waechekorb("Standard-Wäschekorb");
                    return waechekorbRepository.save(neuerKorb);
                });
    }




    @Transactional(readOnly = true)
    public Waechekorb getWaechekorbForUser(Long benutzerId) {
        Benutzer benutzer = getBenutzerOderFehler(benutzerId);
        Waechekorb korb = new Waechekorb("Wäschekorb von " + benutzer.getBenutzername());
        korb.setId(benutzer.getBenutzerId());
        korb.setItems(benutzer.getKleiderschrank()
                .stream()
                .filter(Kleidungsstueck::getIstImWaeschekorb)
                .toList());
        return korb;
    }

    @Transactional
    public Waechekorb addItemForUser(Long benutzerId, Long itemId) {
        Kleidungsstueck item = findOwnedKleidungsstueck(benutzerId, itemId);
        item.setIstImWaeschekorb(true);
        kleidungsstueckRepository.save(item);
        return getWaechekorbForUser(benutzerId);
    }

    @Transactional
    public Waechekorb addItemsForUser(Long benutzerId, List<Long> itemIds) {
        for (Long id : itemIds) {
            Kleidungsstueck item = findOwnedKleidungsstueck(benutzerId, id);
            item.setIstImWaeschekorb(true);
            kleidungsstueckRepository.save(item);
        }
        return getWaechekorbForUser(benutzerId);
    }

    @Transactional
    public Waechekorb removeItemForUser(Long benutzerId, Long itemId) {
        Kleidungsstueck item = findOwnedKleidungsstueck(benutzerId, itemId);
        item.setIstImWaeschekorb(false);
        kleidungsstueckRepository.save(item);
        return getWaechekorbForUser(benutzerId);
    }

    @Transactional
    public Waechekorb removeItemsForUser(Long benutzerId, List<Long> itemIds) {
        for (Long id : itemIds) {
            Kleidungsstueck item = findOwnedKleidungsstueck(benutzerId, id);
            item.setIstImWaeschekorb(false);
            kleidungsstueckRepository.save(item);
        }
        return getWaechekorbForUser(benutzerId);
    }

    @Transactional
    public Waechekorb clearForUser(Long benutzerId) {
        Benutzer benutzer = getBenutzerOderFehler(benutzerId);
        for (Kleidungsstueck item : new ArrayList<>(benutzer.getKleiderschrank())) {
            if (item.getIstImWaeschekorb()) {
                item.setIstImWaeschekorb(false);
                kleidungsstueckRepository.save(item);
            }
        }
        return getWaechekorbForUser(benutzerId);
    }

    /**     * Das Löschen des einzigen Korbes wird verhindert.     * Wirft eine Exception, um Missbrauch zu vermeiden.     */
    public void delete() {
        throw new UnsupportedOperationException("Der Standard-Wäschekorb kann nicht gelöscht werden.");
    }

    public Waechekorb addItem(Long itemId) {
        Waechekorb basket = getSingletonWaechekorb();
        Kleidungsstueck item = kleidungsstueckRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        basket.addItem(item);
        return waechekorbRepository.save(basket);
    }

    public Waechekorb addItems(List<Long> itemIds) {
        Waechekorb basket = getSingletonWaechekorb();
        for (Long id : itemIds) {
            kleidungsstueckRepository.findById(id).ifPresent(basket::addItem);
        }
        return waechekorbRepository.save(basket);
    }

    public Waechekorb removeItem(Long itemId) {
        Waechekorb basket = getSingletonWaechekorb();
        kleidungsstueckRepository.findById(itemId).ifPresent(basket::removeItem);
        return waechekorbRepository.save(basket);
    }

    public Waechekorb removeItems(List<Long> itemIds) {
        Waechekorb basket = getSingletonWaechekorb();
        for (Long id : itemIds) {
            kleidungsstueckRepository.findById(id).ifPresent(basket::removeItem);
        }
        return waechekorbRepository.save(basket);
    }

    public Waechekorb clear() {
        Waechekorb basket = getSingletonWaechekorb();
        basket.clear();
        return waechekorbRepository.save(basket);
    }

//
//    //  Verfügbare Kleidung nach Wetter filtern (wie in EmpfehlungService)
//    public List<Kleidungsstueck> getAvailableByWeather(int temperaturIndex, boolean regen) {
//        List<Kleidungsstueck> allItems = kleidungsstueckRepository.findAll();
//        return Waechekorb.filterAvailableByWeather(allItems, temperaturIndex, regen);
//    }
//
//    // Items im Korb nach Wetter filtern
//    public List<Kleidungsstueck> getBasketItemsByWeather(Long basketId, int temperaturIndex, boolean regen) {
//        Waechekorb basket = waechekorbRepository.findById(basketId).orElseThrow(() -> new RuntimeException("Basket not found"));
//        return basket.filterByWeather(temperaturIndex, regen);
//    }


//    public List<Waechekorb> getAll() {
//        return waechekorbRepository.findAll();
//    }
//
//    public Optional<Waechekorb> getById(Long id) {
//        return waechekorbRepository.findById(id);
//    }
//
//    public void delete(Long id) {
//        waechekorbRepository.deleteById(id);
//    }
//
//    public Waechekorb addItem(Long basketId, Long itemId) {
//        Waechekorb basket = waechekorbRepository.findById(basketId).orElseThrow(() -> new RuntimeException("Basket not found"));
//        Kleidungsstueck item = kleidungsstueckRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));
//        basket.addItem(item);
//        return waechekorbRepository.save(basket);
//    }
//
//    public Waechekorb addItems(Long basketId, List<Long> itemIds) {
//        Waechekorb basket = waechekorbRepository.findById(basketId).orElseThrow(() -> new RuntimeException("Basket not found"));
//        for (Long id : itemIds) {
//            kleidungsstueckRepository.findById(id).ifPresent(basket::addItem);
//        }
//        return waechekorbRepository.save(basket);
//    }
//
//    public Waechekorb removeItem(Long basketId, Long itemId) {
//        Waechekorb basket = waechekorbRepository.findById(basketId).orElseThrow(() -> new RuntimeException("Basket not found"));
//        kleidungsstueckRepository.findById(itemId).ifPresent(basket::removeItem);
//        return waechekorbRepository.save(basket);
//    }
//
//    public Waechekorb removeItems(Long basketId, List<Long> itemIds) {
//        Waechekorb basket = waechekorbRepository.findById(basketId).orElseThrow(() -> new RuntimeException("Basket not found"));
//        for (Long id : itemIds) {
//            kleidungsstueckRepository.findById(id).ifPresent(basket::removeItem);
//        }
//        return waechekorbRepository.save(basket);
//    }
//
//    public Waechekorb clear(Long basketId) {
//        Waechekorb basket = waechekorbRepository.findById(basketId).orElseThrow(() -> new RuntimeException("Basket not found"));
//        basket.clear();
//        return waechekorbRepository.save(basket);
//    }
//
//    private Waechekorb getDefaultBasket() {
//        return waechekorbRepository.findAll().stream()
//                .findFirst()
//                .orElseGet(() -> waechekorbRepository.save(new Waechekorb("Default")));
//    }
//
//    public Waechekorb addItemsToDefaultBasket(List<Long> itemIds) {
//        Waechekorb basket = getDefaultBasket();
//        for (Long id : itemIds) {
//            Kleidungsstueck item = kleidungsstueckRepository.findById(id)
//
//                    .orElseThrow(() -> new RuntimeException("Item not found: " + id));
//
//            basket.addItem(item);
//        }
//        return waechekorbRepository.save(basket);
//    }

    private Benutzer getBenutzerOderFehler(Long benutzerId) {
        if (benutzerId == null) {
            throw new IllegalArgumentException("Nicht eingeloggt");
        }
        return benutzerRepository.findById(benutzerId)
                .orElseThrow(() -> new NoSuchElementException("Benutzer nicht gefunden: " + benutzerId));
    }

    private Kleidungsstueck findOwnedKleidungsstueck(Long benutzerId, Long itemId) {
        return getBenutzerOderFehler(benutzerId)
                .getKleiderschrank()
                .stream()
                .filter(item -> item.getId() != null && item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Kleidungsstück gehört nicht zu diesem Benutzer: " + itemId));
    }

}
