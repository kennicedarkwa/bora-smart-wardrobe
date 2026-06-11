package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Empfehlung;
import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.service.EmpfehlungService;
import org.springframework.web.bind.annotation.*;

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
    public Empfehlung getEmpfehlung(
            @RequestBody DailyForecast dailyForecast,
            @RequestParam String anlass) // NEU: Nimmt den Anlass aus dem Link entgegen(fatima alm.)

    {
        return empfehlungService.generiereEmpfehlung(dailyForecast,anlass);
    }
}
