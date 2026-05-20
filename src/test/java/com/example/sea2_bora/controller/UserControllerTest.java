package com.example.sea2_bora.controller;

import com.example.sea2_bora.implementation.AppUser;
import com.example.sea2_bora.repository.AppUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_shouldReturn200() throws Exception {

        String json = """
                {
                    "username": "ehsan",
                    "password": "1234"
                }
                """;

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk());
    }

    @Test
    void login_shouldReturn200_whenCredentialsAreCorrect() throws Exception {

        AppUser user = new AppUser("ehsan", "1234");

        userRepository.save(user);

        String json = """
                {
                    "username": "ehsan",
                    "password": "1234"
                }
                """;

        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk());
    }

    @Test
    void login_shouldReturn401_whenPasswordIsWrong() throws Exception {

        AppUser user = new AppUser("ehsan", "1234");

        userRepository.save(user);

        String json = """
                {
                    "username": "ehsan",
                    "password": "wrong"
                }
                """;

        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isUnauthorized());
    }
}