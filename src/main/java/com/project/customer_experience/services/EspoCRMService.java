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

    // registro y sincronización
    public Mono<String> syncUser(String firstName, String lastName, String email ){
        // datos de EspoCRM para crear un Contacto
        Map<String, Object> contactData = Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "emailAddress", email,
                "description", "Sincronización de Cliente - Sistema Customer Experience"
        );

        return webClient.post()
                .uri("/contact")
                .bodyValue(contactData)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(error -> {
                    System.err.println("Error: Problemas al sincroniza con EspoCRM: " + error.getMessage());
                    return Mono.just("ERROR_SYNC");
                });
    }
}

