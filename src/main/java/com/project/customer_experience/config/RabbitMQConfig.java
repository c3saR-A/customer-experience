package com.project.customer_experience.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
feature/email-service
    public Jackson2JsonMessageConverter messageConverter() {

    }
}
