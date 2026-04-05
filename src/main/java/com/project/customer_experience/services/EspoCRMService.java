package com.project.customer_experience.services;

import com.project.customer_experience.dto.UserRegistrationDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class EspoCRMService {


    private final WebClient webClient;

    public EspoCRMService(WebClient espoWebClient){
        this.webClient = espoWebClient;
    }


    public Mono<String> createContact(UserRegistrationDTO userRegistrationDto){
        // datos de EspoCRM para crear un Contacto
        Map<String, Object> contactData = Map.of(
                "firstName", userRegistrationDto.getFirstname(),
                "lastName", userRegistrationDto.getLastname(),
                "emailAddress", userRegistrationDto.getEmail(),
                "description", "Cliente registrado desde Spring Boot Customer Experience App"
        );

        return webClient.post()
                .uri("/Contact")
                .bodyValue(contactData)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(error -> {
                    System.err.println("Error se daño el jasware al sincroniza con EspoCRM: " + error.getMessage());
                    return Mono.just("ERROR");
                });
    }
}

