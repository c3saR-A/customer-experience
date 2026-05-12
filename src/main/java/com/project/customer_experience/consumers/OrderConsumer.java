package com.project.customer_experience.consumers;

import com.project.customer_experience.dto.OrderEventDTO;
import com.project.customer_experience.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate; // para WebSockets
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate; // El "motor" de WebSockets

    // Ahora el constructor recibe ambas cosas
    public OrderConsumer(EmailService emailService, SimpMessagingTemplate messagingTemplate) {
        this.emailService = emailService;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "notifications.queue")
    public void receiveOrderEvent(OrderEventDTO event) {
        // 1. Mensaje en consola para que veas que funciona
        System.out.println("¡Evento recibido! Procesando orden: " + event.getOrderId());

        // 2. Envía el Correo
        emailService.sendOrderConfirmationEmail(event);

        // 3. Envía la Notificación en tiempo real (WebSocket)
        // Se envía al canal específico del cliente: /topic/orders/{clientId}
        String destination = "/topic/orders/" + event.getClientId();
        messagingTemplate.convertAndSend(destination, "¡Tu orden #" + event.getOrderId() + " ha sido confirmada!");

        System.out.println("Correo enviado a " + event.getClientEmail() + " y notificación enviada a " + destination);
    }
}