package com.project.customer_experience.services;

import com.project.customer_experience.client.EspoCrmClient;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private EspoCrmClient espoCrmClient;

    @Autowired
    private WebClient webClientGrupoA;

    // Normalización para coincidir con los nombres en DBeaver (ej. "Ashley")
    private String normalizeClientId(String id) {
        return (id != null) ? id.trim() : null;
    }

    public Cart getOrCreateCart(String clientId) {
        String finalId = normalizeClientId(clientId);
        return cartRepository.findByClientId(finalId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setClientId(finalId);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public void addItemToCart(String clientId, CartItemDTO itemDto) {
        String finalId = normalizeClientId(clientId);

        ApiResponse<ProductResponseDTO> response = webClientGrupoA.get()
                .uri("/api/products/{id}", itemDto.productId())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ProductResponseDTO>>() {})
                .block();

        if (response == null || !response.success() || response.data() == null) {
            throw new RuntimeException("El producto no existe.");
        }

        Cart cart = getOrCreateCart(finalId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(itemDto.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + itemDto.quantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(itemDto.productId());
            newItem.setQuantity(itemDto.quantity());
            newItem.setStockQuantity(response.data().stockQuantity());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }
        cartRepository.save(cart);
    }

    @Transactional
    public OrderResponseDTO processCheckout(String username) {
        // 1. BUSCAR CARRITO LOCAL (DBeaver)
        String finalUsername = normalizeClientId(username);
        Cart cart = cartRepository.findByClientId(finalUsername)
                .orElseThrow(() -> new RuntimeException("No se encontró el carrito para: " + username));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // 2. CONSULTAR EL EMAIL EN ESPOCRM (DINÁMICO)
        // Se comunica con EspoCRM para obtener el email actual vinculado al usuario
        String emailDesdeCRM = espoCrmClient.getEmailByUsername(finalUsername);

        if (emailDesdeCRM == null || !emailDesdeCRM.contains("@")) {
            throw new RuntimeException("El usuario " + username + " no tiene un email válido en EspoCRM");
        }

        // 3. PREPARAR LA ORDEN
        List<OrderItemRequestDTO> itemsRequest = cart.getItems().stream()
                .map(item -> new OrderItemRequestDTO(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();

        // 4. ENVIAR AL GRUPO A CON EL EMAIL REAL DE ESPOCRM
        // El clientEmail en la respuesta será el que traiga el CRM en ese instante
        OrderRequestDTO orderRequest = new OrderRequestDTO(1L, emailDesdeCRM, itemsRequest);

        try {
            ApiResponse<OrderResponseDTO> responseWrapper = webClientGrupoA.post()
                    .uri("/api/orders")
                    .bodyValue(orderRequest)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<OrderResponseDTO>>() {})
                    .block();

            if (responseWrapper != null && responseWrapper.success()) {
                cart.getItems().clear();
                cartRepository.save(cart);
                // El resultado final mostrará el email sincronizado
                return responseWrapper.data();
            } else {
                throw new RuntimeException("Error en la respuesta del Grupo A: " +
                        (responseWrapper != null ? responseWrapper.message() : "Sin respuesta"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación: " + e.getMessage());
        }
    }

    @Transactional
    public void updateItemQuantity(String clientId, Long productId, int quantity) {
        String finalId = normalizeClientId(clientId);
        Cart cart = getOrCreateCart(finalId);
        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    cartRepository.save(cart);
                });
    }

    @Transactional
    public void removeItemFromCart(String clientId, Long productId) {
        String finalId = normalizeClientId(clientId);
        Cart cart = getOrCreateCart(finalId);
        if (cart.getItems().removeIf(item -> item.getProductId().equals(productId))) {
            cartRepository.save(cart);
        }
    }
}