package com.example.sea2_bora.service;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.Waechekorb;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import com.example.sea2_bora.repository.WaechekorbRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class WaechekorbService {

    @Autowired
    private WaechekorbRepository waechekorbRepository;

    @Autowired
    private KleidungsstueckRepository kleidungsstueckRepository;

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

}