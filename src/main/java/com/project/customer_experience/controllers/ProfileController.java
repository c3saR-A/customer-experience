package com.project.customer_experience.controllers;

import com.project.customer_experience.dto.UserProfileDTO;
import com.project.customer_experience.services.AuthService;
import com.project.customer_experience.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile(){
        return ResponseEntity.ok(userService.getAuthenticatedUserProfile());
    }
}
