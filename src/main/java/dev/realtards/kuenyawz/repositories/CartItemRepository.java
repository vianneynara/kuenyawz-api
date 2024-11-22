package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByAccount_AccountId(Long accountId);

    Page<CartItem> findAll(Long accountId, Pageable pageRequest);

    int countCartItemByAccount_AccountId(Long accountId);

    void deleteByAccount_AccountId(Long accountId);
}
