package com.project.customer_experience.services;

import com.project.customer_experience.dto.*;
import com.project.customer_experience.dto.request.OrderItemRequestDTO;
import com.project.customer_experience.dto.request.OrderRequestDTO;
import com.project.customer_experience.dto.response.ApiResponse;
import com.project.customer_experience.dto.response.OrderResponseDTO;
import com.project.customer_experience.dto.response.ProductResponseDTO;
import com.project.customer_experience.entities.Cart;
import com.project.customer_experience.entities.CartItem;
import com.project.customer_experience.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private WebClient webClientGrupoA; // Inyecta el Bean que apunta al puerto 8082


    public Cart getOrCreateCart(String clientId) {
        return cartRepository.findByClientId(clientId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setClientId(clientId);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public void addItemToCart(String clientId, CartItemDTO itemDto) {

        ProductResponseDTO externalProduct = webClientGrupoA.get()
                .uri("/api/products/{id}", itemDto.productId())
                .retrieve()
                .bodyToMono(ProductResponseDTO.class)
                .block();

// Cambia tu 'if' actual por este:
        if (externalProduct == null ||
                externalProduct.data() == null ||
                externalProduct.data().stockQuantity() < itemDto.quantity()) {

            throw new RuntimeException("No hay stock suficiente en el inventario central del Grupo A.");
        }

        Cart cart = getOrCreateCart(clientId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(itemDto.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setStockQuantity(existingItem.get().getStockQuantity() + itemDto.quantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(itemDto.productId());
            newItem.setStockQuantity(itemDto.quantity());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }
        cartRepository.save(cart);
    }

    @Transactional
    public OrderResponseDTO processCheckout(String email) {
        // 1. Localizar el carrito del usuario (ej: "pollito@gmail.com")
        Cart cart = cartRepository.findByClientId(email)
                .orElseThrow(() -> new RuntimeException("No se encontró el carrito para: " + email));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // 2. Validación de Seguridad para el Formato de Email
        // Esto evita que el CommerceCore rechace la petición por validación @Email
        String emailValidado = (email != null && email.contains("@")) ? email : email + "@gmail.com";

        // 3. Preparar la lista de items para el Grupo A
        List<OrderItemRequestDTO> itemsRequest = cart.getItems().stream()
                .map(item -> new OrderItemRequestDTO(
                        item.getProductId(),
                        item.getStockQuantity() // Se envía como 'quantity' en el DTO
                ))
                .toList();

        // 4. Construir la solicitud (Request)
        // Usamos 1L como ID de cliente genérico para el sistema externo
        OrderRequestDTO orderRequest = new OrderRequestDTO(1L, emailValidado, itemsRequest);

        try {
            // 5. Llamada al Microservicio Externo (Puerto 8083)
            ApiResponse responseWrapper = webClientGrupoA.post()
                    .uri("/api/orders")
                    .bodyValue(orderRequest)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();

            // 6. Validación de Respuesta y Limpieza del Carrito
            if (responseWrapper != null && responseWrapper.success()) {

                // Si el Grupo A confirma éxito (success: true), procedemos a vaciar el carrito
                cart.getItems().clear();
                cartRepository.save(cart);

                // 7. Extraer la Orden de forma segura (Null-Safe)
                // Esto evita el error de "content() is null" que tenías antes
                OrderResponseDTO orderDetail = null;

                boolean tieneDatos = responseWrapper.data() != null &&
                        responseWrapper.data().content() != null &&
                        !responseWrapper.data().content().isEmpty();

                if (tieneDatos) {
                    // Tomamos el primer registro de la lista 'content'
                    orderDetail = responseWrapper.data().content().get(0);
                    System.out.println("Checkout exitoso en CommerceCore. ID Orden: " + orderDetail.id());
                } else {
                    // Si el Grupo A no devuelve el objeto en la lista, creamos una respuesta manual
                    System.out.println("Orden creada con éxito, pero sin detalles en la respuesta.");
                    orderDetail = new OrderResponseDTO(null, emailValidado, new BigDecimal("0.00"), "CREATED");
                }

                return orderDetail;

            } else {
                // Manejo de rechazo del servidor externo (ej: error 400 o 500)
                String mensaje = (responseWrapper != null) ? responseWrapper.message() : "Respuesta vacía";
                throw new RuntimeException("El CommerceCore rechazó la orden: " + mensaje);
            }

        } catch (Exception e) {
            // En caso de fallo de red, el carrito NO se limpia para que el usuario no pierda sus productos
            throw new RuntimeException("Error crítico en la comunicación con el checkout: " + e.getMessage());
        }
    }

    @Transactional
    public void updateItemQuantity(String clientId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cantidad no permitida");
        }

        Cart cart = getOrCreateCart(clientId);

        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setStockQuantity(quantity);
                    cartRepository.save(cart);
                });
    }

    @Transactional
    public void removeItemFromCart(String clientId, Long productId) {
        Cart cart = getOrCreateCart(clientId);
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        if (removed) {
            cartRepository.save(cart);
        }
    }
}
