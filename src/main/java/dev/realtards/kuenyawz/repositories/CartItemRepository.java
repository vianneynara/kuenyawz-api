package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, JpaSpecificationExecutor<CartItem> {

    List<CartItem> findAllByAccount_AccountId(Long accountId);

    Page<CartItem> findAllByAccount_AccountId(Long accountId, Pageable pageRequest);

    int countCartItemByAccount_AccountId(Long accountId);

    int deleteByAccount_AccountId(Long accountId);

    int deleteByCartItemIdAndAccount_AccountId(Long cartItemId, Long accountId);
}
