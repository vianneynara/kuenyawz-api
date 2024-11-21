package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
