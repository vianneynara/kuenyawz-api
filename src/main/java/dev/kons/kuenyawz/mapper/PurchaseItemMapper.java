package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.purchase.PurchaseItemDto;
import dev.kons.kuenyawz.entities.PurchaseItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VariantMapper.class})
public interface PurchaseItemMapper {

	@Mapping(target = "variantDto", source = "variant")
	PurchaseItemDto toDto(PurchaseItem entity);
}
