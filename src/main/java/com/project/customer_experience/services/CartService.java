package com.project.customer_experience.services;

import com.project.customer_experience.dto.*;
import com.project.customer_experience.dto.request.OrderItemRequestDTO;
import com.project.customer_experience.dto.request.OrderRequestDTO;
import com.project.customer_experience.dto.response.ApiResponse;
import com.project.customer_experience.dto.response.OrderResponseDTO;
import com.project.customer_experience.dto.response.ProductResponseDTO;
import com.project.customer_experience.entities.Cart;
import com.project.customer_experience.entities.CartItem;
import com.project.customer_experience.entities.User;
import com.project.customer_experience.repositories.CartRepository;
import com.project.customer_experience.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private WebClient webClientGrupoA; // Inyeccion de Bean que apunta al puerto 8083

    @Autowired
    private UserRepository userRepository;

    private String getNumericClientId(String username) {
        return userRepository.findByUsername(username)
                .map(user -> user.getId().toString()) // Si existe, extrae el ID y hazlo String
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el username: " + username));
    }

    public Cart getOrCreateCart(String username) {
        String numericId = getNumericClientId(username);
        return cartRepository.findByClientId(numericId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setClientId(numericId);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public void addItemToCart(String clientId, CartItemDTO itemDto) {

        ProductResponseDTO externalProduct = webClientGrupoA.get()
                .uri("/api/products/{id}", itemDto.productId())
                .retrieve()
                .onStatus(status -> status.value() == 404, clientResponse -> Mono.empty())
                .bodyToMono(ProductResponseDTO.class)
                .block();

        if (externalProduct == null || externalProduct.data() == null) {
            throw new RuntimeException("El producto solicitado no existe en el inventario central del Grupo A.");
        }

        if (externalProduct.data().stockQuantity() < itemDto.quantity()) {
            throw new RuntimeException("No hay stock suficiente. Stock disponible: " + externalProduct.data().stockQuantity());
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
    public OrderResponseDTO processCheckout(String username) {
        // Buscamos el usuario en la BD
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el username: " + username));

        // obtención de datos (Id, email)
        String numericId = user.getId().toString();
        String email = user.getEmail();

        // Localizar el carrito usando Id
        Cart cart = cartRepository.findByClientId(numericId)
                .orElseThrow(() -> new RuntimeException("No se encontró el carrito para el usuario ID: " + numericId));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Construir items consultando al Grupo A
        List<OrderItemRequestDTO> itemsRequest = cart.getItems().stream()
                .map(item -> {
                    ProductResponseDTO prod = webClientGrupoA.get()
                            .uri("/api/products/{id}", item.getProductId())
                            .retrieve()
                            .onStatus(status -> status.value() == 404, res -> Mono.empty())
                            .bodyToMono(ProductResponseDTO.class)
                            .block();

                    if (prod == null || prod.data() == null) {
                        throw new RuntimeException("Un producto en tu carrito ya no está disponible.");
                    }

                    return new OrderItemRequestDTO(
                            item.getProductId(),
                            item.getStockQuantity()
                    );
                })
                .toList();

        // Construir solicitud usando los datos extraídos del usuario de tu BD
        Long customerIdLong = user.getId();

        // FIX: Se envía 'Email' en lugar de 'username', es lo esperado por la API
        OrderRequestDTO orderRequest = new OrderRequestDTO(customerIdLong, email, itemsRequest);

        try {
            // WebClient ahora sabe exactamente la estructura gracias a los tipos genéricos
            ApiResponse<OrderResponseDTO> responseWrapper = webClientGrupoA.post()
                    .uri("/api/orders")
                    .bodyValue(orderRequest)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<ApiResponse<OrderResponseDTO>>() {})
                    .block();
            // Log para ver que llega desde la API
            //System.out.println(">>> MAPEO CRUDO DE RESPONSE: " + responseWrapper);

            if (responseWrapper != null && responseWrapper.success()) {

                OrderResponseDTO orderDetail = responseWrapper.data();

                if (orderDetail == null) {
                    throw new IllegalStateException("CommerceCore confirmó la creación, pero 'data' viene nulo.");
                }

                System.out.println("Checkout exitoso en CommerceCore. ID Orden: " + orderDetail.id());

                // Limpieza segura del carrito
                cart.getItems().clear();
                cartRepository.save(cart);

                return orderDetail;

            } else {
                String mensaje = (responseWrapper != null) ? responseWrapper.message() : "Respuesta vacía";
                throw new RuntimeException("El CommerceCore rechazó la orden: " + mensaje);
            }

        } catch (Exception e) {
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
