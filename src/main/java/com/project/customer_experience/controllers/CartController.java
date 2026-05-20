package com.project.customer_experience.controllers;

import com.project.customer_experience.dto.CartItemDTO;
import com.project.customer_experience.dto.response.OrderResponseDTO;
import com.project.customer_experience.entities.Cart;
import com.project.customer_experience.services.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Principal principal) {
        return ResponseEntity.ok(cartService.getOrCreateCart(principal.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<Void> addItem(Principal principal, @Valid @RequestBody CartItemDTO dto) {
        cartService.addItemToCart(principal.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Void> updateItem(Principal principal, @PathVariable Long productId, @RequestBody CartItemDTO dto) {
        cartService.updateItemQuantity(principal.getName(), productId, dto.quantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(Principal principal, @PathVariable Long productId) {
        cartService.removeItemFromCart(principal.getName(), productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(java.security.Principal principal) {
        String usernameFromToken = principal.getName();

        OrderResponseDTO response = cartService.processCheckout(usernameFromToken);
        return ResponseEntity.ok(response);
    }
}