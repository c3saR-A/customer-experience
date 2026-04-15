package com.project.customer_experience.controllers;

import com.project.customer_experience.dto.LoginRequestDTO;
import com.project.customer_experience.dto.LoginResponseDTO;
import com.project.customer_experience.dto.UserRegistrationDTO;
import com.project.customer_experience.entities.User;
import com.project.customer_experience.services.AuthService;
import com.project.customer_experience.services.JWTService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public AuthController(AuthService authService,
                          AuthenticationManager authenticationManager,
                          JWTService jwtService){
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDTO registrationDTO){
        try{
            User user = authService.registerUser(registrationDTO);
            return ResponseEntity.ok("Usuario registrado y sincronizado: " + user.getUsername());
        } catch (RuntimeException error){
            return ResponseEntity.badRequest().body(error.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        Authentication authenticacion = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.username(),
                        loginRequestDTO.password()
                )
        );

            String accessToken = jwtService.generateAccessToken(authenticacion);
            String refreshToken = jwtService.generateRefreshToken(authenticacion);

            LoginResponseDTO response = new LoginResponseDTO(
                    accessToken,
                    refreshToken,
                    loginRequestDTO.username()
            );


        return ResponseEntity.ok(response);
    }
}
