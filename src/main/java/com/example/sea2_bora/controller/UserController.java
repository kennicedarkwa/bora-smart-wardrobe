package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.AppUser;
import com.example.sea2_bora.repository.AppUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AppUserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AppUser user) {

        userRepository.save(user);

        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AppUser loginUser) {

        Optional<AppUser> user =
                userRepository.findByUsername(loginUser.getUsername());

        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }

        if (!user.get().getPassword().equals(loginUser.getPassword())) {
            return ResponseEntity.status(401).body("Wrong password");
        }

        return ResponseEntity.ok("Login successful");
    }
}