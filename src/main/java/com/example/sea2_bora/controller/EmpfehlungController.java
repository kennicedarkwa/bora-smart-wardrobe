package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Empfehlung;
import com.example.sea2_bora.implementation.wetter.DailyForecast;
import com.example.sea2_bora.service.EmpfehlungService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empfehlung")
public class EmpfehlungController
{
    private final EmpfehlungService empfehlungService;

    public EmpfehlungController( EmpfehlungService empfehlungService)
    {
        this.empfehlungService = empfehlungService;
    }

    @PostMapping
    public Empfehlung getEmpfehlung(@RequestBody DailyForecast dailyForecast)
    {
        return empfehlungService.generiereEmpfehlung(dailyForecast);
    }
}
