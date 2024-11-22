package dev.realtards.kuenyawz.mapper;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPostDto;
import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.entities.CartItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItem toEntity(CartItemPostDto cartItemPostDto);

    CartItemDto fromEntity(CartItem cartItem, ProductDto productDto);

    CartItem updateCartItemFromPatch(CartItem cartItem, CartItemPatchDto cartItemPatchDto);
}
