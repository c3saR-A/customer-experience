package com.project.customer_experience.repositories;

import com.project.customer_experience.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByClientId(String clientId);
}