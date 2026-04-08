package com.project.customer_experience.services;

import com.project.customer_experience.dto.CartItemDTO;
import com.project.customer_experience.entities.Cart;
import com.project.customer_experience.entities.CartItem;
import com.project.customer_experience.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

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
        Cart cart = getOrCreateCart(clientId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(itemDto.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + itemDto.quantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(itemDto.productId());
            newItem.setQuantity(itemDto.quantity());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }
        cartRepository.save(cart);
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
                    item.setQuantity(quantity);
                    cartRepository.save(cart);
                });
    }

    @Transactional
    public void removeItemFromCart(String clientId, Long productId) {
        Cart cart = getOrCreateCart(clientId);

        // RemoveIf busca el producto y lo saca de la lista
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (removed) {
            cartRepository.save(cart);
        }
    }
}
