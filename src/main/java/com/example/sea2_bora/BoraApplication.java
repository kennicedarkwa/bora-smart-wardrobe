package com.example.sea2_bora;

import com.example.sea2_bora.implementation.Benutzer;
import com.example.sea2_bora.implementation.Kleidungsstueck;
import com.example.sea2_bora.implementation.MainCategory;
import com.example.sea2_bora.repository.BenutzerRepository;
import com.example.sea2_bora.repository.KleidungsstueckRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BoraApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoraApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(
            BenutzerRepository benutzerRepository,
            KleidungsstueckRepository kleidungsstueckRepository,
            com.example.sea2_bora.repository.WaechekorbRepository waechekorbRepository
    ) {
        return args -> {

            // --- Benutzer nur anlegen, wenn DB leer ist ---
            if (benutzerRepository.count() == 0) {
                Benutzer u1 = new Benutzer("Max Mustermann", "abc@u1.com", "123", "HH");
                Benutzer u2 = new Benutzer("Erika Musterfrau", "abc@u2.com", "123", "HH");
                benutzerRepository.save(u1);
                benutzerRepository.save(u2);
            }

            // --- Kleidungsstücke nur anlegen, wenn DB leer ist ---
            if (kleidungsstueckRepository.count() == 0) {
                Kleidungsstueck k1 = new Kleidungsstueck("Regenjacke", 1, true, MainCategory.OBERTEIL, "/images/regenjacke.jpg");
                Kleidungsstueck k2 = new Kleidungsstueck("T-Shirt", 3, false, MainCategory.OBERTEIL, "/images/tshirt.jpg");
                Kleidungsstueck k3 = new Kleidungsstueck("Jeans", 2, false, MainCategory.UNTERTEIL, "/images/jeans.jpg");
                kleidungsstueckRepository.save(k1);
                kleidungsstueckRepository.save(k2);
                kleidungsstueckRepository.save(k3);
            }

            // --- Wäschekorb laden oder erstellen ---
            com.example.sea2_bora.implementation.Waechekorb korb =
                    waechekorbRepository.findFirstByOrderByIdAsc()
                            .orElseGet(() -> waechekorbRepository.save(
                                    new com.example.sea2_bora.implementation.Waechekorb("Standard-Wäschekorb")
                            ));

            // ❗❗❗ PROBLEMSTELLE:
            // korb.getItems() ist LAZY geladen.
            // Sobald echte Daten in der DB sind, führt dieser Zugriff zum Crash.
            // Deshalb bleibt der gesamte Block auskommentiert.

            /*
//            if (korb.getItems().)
//                java.util.List<Kleidungsstueck> alleKlamotten = kleidungsstueckRepository.findAll();
//                if (!alleKlamotten.isEmpty()) {
//                    korb.addItem(alleKlamotten.get(0));
//                    if (alleKlamotten.size() > 1) {
//                        korb.addItem(alleKlamotten.get(1));
//                    }
//                    waechekorbRepository.save(korb);
//                }
//            }
            */

            // --- Test-Socken aus dem Remote-Code ---
            // Dieser Block bleibt AUSKOMMENTIERT, damit dein Projekt nicht crasht.
            /*
            if (korb.getItems().isEmpty()) { //Test Socken für Dashboard WaschErinnerung Test
                for (int i = 1; i <= 12; i++) {
                    Kleidungsstueck testSocke = new Kleidungsstueck(
                            "Test-Socke " + i,
                            1,
                            false,
                            MainCategory.UNTERTEIL,
                            "/images/socke.jpg"
                    );

                    kleidungsstueckRepository.save(testSocke);
                    korb.addItem(testSocke);
                }

                waechekorbRepository.save(korb);
                System.out.println("🤖 SEEDER: 12 Test-Socken erfolgreich in den Wäschekorb gelegt!");
            }
            */

            // Alles andere bleibt unverändert.
        };
    }
}
