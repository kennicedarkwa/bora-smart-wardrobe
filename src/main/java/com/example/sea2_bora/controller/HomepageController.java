package com.example.sea2_bora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    @GetMapping("/")
    public String homepage() {
        return "homepage/homepage.html";
    }
}
