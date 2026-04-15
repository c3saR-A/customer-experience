package com.project.customer_experience.services;

import com.project.customer_experience.dto.UserProfileDTO;
import com.project.customer_experience.entities.User;
import com.project.customer_experience.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileDTO getAuthenticatedUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new UserProfileDTO(
                user.getUsername(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname()
        );
    }
}
