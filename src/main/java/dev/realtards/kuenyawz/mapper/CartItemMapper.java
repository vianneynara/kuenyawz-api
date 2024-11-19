package dev.realtards.kuenyawz.mapper;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPostDto;
import dev.realtards.kuenyawz.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    CartItem toEntity(CartItemPostDto cartItemPostDto);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "account", ignore = true)
    CartItem updateCartItemFromPatch(CartItemPatchDto cartItemPatchDto, @MappingTarget CartItem cartItem);
}
