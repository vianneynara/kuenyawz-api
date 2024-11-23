package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.cartItem.CartItemDto;
import dev.kons.kuenyawz.dtos.product.ProductDto;
import dev.kons.kuenyawz.entities.CartItem;
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
