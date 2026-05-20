package com.project.customer_experience.services;

import com.project.customer_experience.dto.OrderEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumerService {

    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    public OrderConsumerService(EmailService emailService, SimpMessagingTemplate messagingTemplate) {
        this.emailService = emailService;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "notifications.queue")
    public void receiveOrderEvent(OrderEventDTO orderEvent){

        //Log en consola
//        System.out.println("Orden recibida: " + orderEvent.orderId());
//        System.out.println("Cliente Id: " + orderEvent.clientId());
//        System.out.println("Email: " + orderEvent.clientEmail());
//        System.out.println("Total: " + orderEvent.total());
//        System.out.println("Productos: " + orderEvent.products());

//      Enviar el correo con Mailtrap
        emailService.sendOrderConfirmationEmail(orderEvent);

        String destination = "/topic/orders/" + orderEvent.clientId();
        messagingTemplate.convertAndSend(destination, "¡Tu orden #" + orderEvent.orderId() + " ha sido confirmada!");

//        System.out.println("Correo enviado a " + orderEvent.clientEmail() + " y notificación enviada a " + destination);
    }
}
