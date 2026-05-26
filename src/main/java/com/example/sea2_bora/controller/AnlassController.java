package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.Tags;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController

@RequestMapping("/api/anlass")
public class AnlassController {

    @GetMapping
    public List<Tags> getAllAnlaesse() {

        return Arrays.asList(Tags.values());

    }
}
