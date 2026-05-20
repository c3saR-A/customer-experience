package com.project.customer_experience.consumer;

import com.project.customer_experience.dto.OrderEventDTO;
import com.project.customer_experience.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderNotificationConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;

    public OrderNotificationConsumer(SimpMessagingTemplate messagingTemplate, EmailService emailService) {
        this.messagingTemplate = messagingTemplate;
        this.emailService = emailService;
    }

    @RabbitListener(queues = "notifications.queue")
    public void handleOrderConfirmation(OrderEventDTO event) {
        // 1. Notificación en tiempo real via WebSocket
        String destination = "/topic/orders/" + event.clientId();
        messagingTemplate.convertAndSend(destination, event);
        System.out.println("WebSocket: Notificación enviada al cliente " + event.clientId());

        // 2. Envío de correo formal
        emailService.sendOrderConfirmationEmail(event);
        System.out.println("Email: Confirmación enviada a " + event.clientEmail());
    }
}