package com.project.customer_experience.client;

import com.project.customer_experience.dto.EspoUserDTO;
import org.springframework.beans.factory.annotation.Value;import org.springframework.core.ParameterizedTypeReference;import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;import java.util.List;import java.util.Map;

@Component
public class EspoCrmClient {

    private final WebClient webClientEspo;

    public EspoCrmClient(
            WebClient.Builder builder,
            @Value("${espocrm.base-url}") String baseUrl,
            @Value("${espocrm.api-key}") String apiKey) {

        this.webClientEspo = builder
                .baseUrl(baseUrl)
                .defaultHeader("X-Api-Key", apiKey)
                .build();
    }

    public String getEmailByUsername(String username) {
        try {
            var response = webClientEspo.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/Contact") // <--- CAMBIAR DE /User A /Contact
                            // Usamos un filtro de búsqueda por el nombre que aparece en el CRM
                            .queryParam("select", "emailAddress")
                            .queryParam("where[0][type]", "contains")
                            .queryParam("where[0][attribute]", "name")
                            .queryParam("where[0][value]", username)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            System.out.println("Respuesta de EspoCRM (Contactos): " + response);

            if (response != null && response.containsKey("list")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("list");
                if (!list.isEmpty()) {
                    // Retorna ashley13@gmail.com dinámicamente
                    return (String) list.get(0).get("emailAddress");
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}