package com.project.customer_experience.controllers;

import com.project.customer_experience.dto.UserRegistrationDTO;
import com.project.customer_experience.entities.User;
import com.project.customer_experience.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDTO registrationDTO){
        try{
            User user = authService.registerUser(registrationDTO);
            return ResponseEntity.ok("Usuario registrado: " + user.getUsername());
        } catch (RuntimeException error){
            return ResponseEntity.badRequest().body(error.getMessage());
        }
    }
}
