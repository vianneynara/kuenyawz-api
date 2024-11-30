package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.purchase.PurchaseItemDto;
import dev.kons.kuenyawz.entities.PurchaseItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VariantMapper.class, ProductMapper.class})
public interface PurchaseItemMapper {

	@Mapping(target = "variant", source = "variant")
	@Mapping(target = "product", source = "variant.product")
	PurchaseItemDto toDto(PurchaseItem entity);
}
