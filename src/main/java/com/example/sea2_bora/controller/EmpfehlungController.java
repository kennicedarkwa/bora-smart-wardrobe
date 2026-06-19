package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Empfehlung;
import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.service.EmpfehlungService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/empfehlung")
@CrossOrigin // Wichtig, damit das Frontend zugreifen darf!(fatima alm.)
public class EmpfehlungController
{
    private final EmpfehlungService empfehlungService;

    public EmpfehlungController( EmpfehlungService empfehlungService)
    {
        this.empfehlungService = empfehlungService;
    }

    @PostMapping
    public ResponseEntity<Empfehlung> getEmpfehlung(
            @RequestBody DailyForecast dailyForecast,
            @RequestParam(defaultValue = "ALLTAG") String anlass,
            HttpSession session) // NEU: Nimmt den Anlass aus dem Link entgegen(fatima alm.)

    {
        Long benutzerId = (Long) session.getAttribute("benutzerId");
        if (benutzerId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            return ResponseEntity.ok(empfehlungService.generiereEmpfehlung(dailyForecast, anlass, benutzerId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
