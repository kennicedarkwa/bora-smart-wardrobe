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
            KleidungsstueckRepository kleidungsstueckRepository
    ) {
        return args -> {
            if (benutzerRepository.count() == 0) {
                Benutzer u1 = new Benutzer("Max Mustermann", "abc@u1.com", "123", "HH");
                Benutzer u2 = new Benutzer("Erika Musterfrau", "abc@u2.com", "123", "HH");
                benutzerRepository.save(u1);
                benutzerRepository.save(u2);
            }

            if (kleidungsstueckRepository.count() == 0) {
                Kleidungsstueck k1 = new Kleidungsstueck("Regenjacke", 1, true, MainCategory.OBERTEIL, "/images/regenjacke.jpg");
                Kleidungsstueck k2 = new Kleidungsstueck("T-Shirt", 3, false, MainCategory.OBERTEIL, "/images/tshirt.jpg");
                Kleidungsstueck k3 = new Kleidungsstueck("Jeans", 2, false, MainCategory.UNTERTEIL, "/images/jeans.jpg");
                kleidungsstueckRepository.save(k1);
                kleidungsstueckRepository.save(k2);
                kleidungsstueckRepository.save(k3);
            }
        };
    }
}