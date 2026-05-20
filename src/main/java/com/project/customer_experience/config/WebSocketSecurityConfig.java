package com.project.customer_experience.config;

import com.project.customer_experience.services.JWTService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class WebSocketSecurityConfig implements ChannelInterceptor {

    private final JWTService jwtService;

    public WebSocketSecurityConfig(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Solo validamos cuando el cliente intenta conectar
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtService.validateToken(token)) {
                    String username = jwtService.getUsernameFromToken(token);

                    // Creamos una autenticación simple para que Spring sepa quién es el usuario
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(username, null, null);
                    accessor.setUser(auth);
                } else {
                    throw new RuntimeException("Token JWT inválido");
                }
            } else {
                throw new RuntimeException("No se proporcionó token de autorización");
            }
        }
        return message;
    }
}