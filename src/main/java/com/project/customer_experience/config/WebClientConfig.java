package com.project.customer_experience.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient espoWebClient(
            WebClient.Builder builder,
            @Value("${espocrm.base-url}") String espocrmBaseUrl,
            @Value("${espocrm.api-key}") String espocrmApiKey){

        return builder
                .baseUrl(espocrmBaseUrl)
                .defaultHeader("X-Api-Key", espocrmApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
