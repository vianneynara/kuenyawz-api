package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPostDto;
import dev.realtards.kuenyawz.entities.CartItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemService {

    @Transactional
    CartItemDto createCartItem(CartItemPostDto cartItemPostDto);

    @Transactional(readOnly = true)
    List<CartItemDto> getCartItems(Long accountId);

    @Transactional
    CartItemDto patchCartItem(Long cartItemId, CartItemPatchDto cartItemPatchDto);

    @Transactional
    void deleteCartItem(Long cartItemId);
}
