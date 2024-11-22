package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByAccountId(long accountId);

    Page<CartItem> findAll(Long accountId, PageRequest pageRequest);

    int countCartItemByAccountId(long accountId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.account.accountId = :accountId")
    void deleteByAccount_AccountId(@Param("accountId") Long accountId);
}
