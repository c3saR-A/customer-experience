package com.project.customer_experience.controllers;

import com.project.customer_experience.repositories.UserRepository;
import com.project.customer_experience.services.EspoCRMService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/crm")
public class CRMController {

    private final EspoCRMService espoCRMService;
    private final UserRepository userRepository;

    public CRMController(EspoCRMService espoCRMService, UserRepository userRepository){
        this.espoCRMService = espoCRMService;
        this.userRepository = userRepository;
    }

    @PostMapping("/sync")
    @PreAuthorize("hasRole('USER')")
    public Mono<ResponseEntity<String>> syncManual(@AuthenticationPrincipal UserDetails userDetails){

        return Mono.justOrEmpty(userRepository.findByUsername(userDetails.getUsername()))
                .flatMap(user -> {
                   return espoCRMService.syncUser(
                           user.getFirstname(),
                           user.getLastname(),
                           user.getEmail()
                   );
                })
                .map(response -> {
                    if("ERROR_SYNC".equals(response)) {
                        return ResponseEntity.internalServerError()
                                .body("Error al sincronizar con EspoCRM. Revisar logs");
                    }
                    return ResponseEntity.ok("Sincronización manual exitosa. Detalle: " + response);
                }).defaultIfEmpty(ResponseEntity.status(404).body("Usuario no encontrado en base de datos"));
    }
}

