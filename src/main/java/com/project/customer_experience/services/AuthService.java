package com.project.customer_experience.services;

import com.project.customer_experience.dto.UserRegistrationDTO;
import com.project.customer_experience.entities.User;
import com.project.customer_experience.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EspoCRMService espoCRMService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       EspoCRMService espoCRMService,
                       BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.espoCRMService = espoCRMService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserRegistrationDTO userRegistrationDTO){
        // verificación de usuario ya existente
        if (userRepository.findByUsername(userRegistrationDTO.username()).isPresent()){
            throw new RuntimeException("El nombre de usuario " + userRegistrationDTO.username() + " ya está en uso");
        }
        // verificación de email ya existente
        if (userRepository.findByEmail(userRegistrationDTO.email()).isPresent()){
            throw new RuntimeException("El email " + userRegistrationDTO.email() + " ya está registrado");
        }

        // Mapeo y encripción de contra
        User newUser = new User();
        newUser.setUsername(userRegistrationDTO.username());
        newUser.setEmail(userRegistrationDTO.email());
        newUser.setFirstname(userRegistrationDTO.firstname());
        newUser.setLastname(userRegistrationDTO.lastname());
        newUser.setPassword(passwordEncoder.encode(userRegistrationDTO.password()));

        // Guardar en Base
        User savedUser = userRepository.save(newUser);

        // Sincronizar con EspoCRM
//        espoCRMService.createContact(userRegistrationDTO).subscribe(responde -> {
//            System.out.println("Si jalo el EspoCRM: " + responde);
//        });

        espoCRMService.syncUser(savedUser.getFirstname(), savedUser.getLastname(), savedUser.getEmail())
                .doOnSuccess(res -> System.out.println("Respuesta de Espo: " + res))
                .doOnError(err -> System.err.println("Error en Espo: " + err.getMessage()))
                .subscribe();

        return savedUser;

    }
}
