package dev.realtards.kuenyawz.mapper;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemDto;
import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.entities.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

	CartItemDto fromEntity(CartItem cartItem);

	default CartItemDto fromEntity(CartItem cartItem, ProductDto productDto, Long selectedVariantId) {
		CartItemDto dto = fromEntity(cartItem);
		dto.setProduct(productDto);
		dto.setSelectedVariantId(selectedVariantId);
		return dto;
	}
}
