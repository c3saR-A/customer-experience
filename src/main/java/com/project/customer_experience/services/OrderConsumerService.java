package com.project.customer_experience.services;

import com.project.customer_experience.dto.OrderEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumerService {
    // conectarse a lo de emial
    // @Autowired ServicioEmail;

    // conectrase a las noti con websocket
    // @Autowired NotificacionWebSocket;

    @RabbitListener(queues = "orders.queues")
    public void receiveOrderEvent(OrderEventDTO orderEvent){

        //Log en consola
        System.out.println("Orden recibida: " + orderEvent.orderId());
        System.out.println("Cliente Id: " + orderEvent.clientId());
        System.out.println("Email: " + orderEvent.clientEmail());
        System.out.println("Total: " + orderEvent.total());
        System.out.println("Productos: " + orderEvent.products());

//      Enviar el correo con Mailtrap
//      emailService.sendOrderConfirmation(orderEvent);
//
//      Notificar por WebSocket al cliente
//      notificationHandler.notifyClient(orderEvent.getClientId(), "Tu orden ha sido confirmada");
    }
}
