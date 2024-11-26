package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.entities.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PurchaseItemMapper.class, TransactionMapper.class})
public interface PurchaseMapper {

    @Mapping(target = "latitude", source = "coordinate.latitude")
    @Mapping(target = "longitude", source = "coordinate.longitude")
	PurchaseDto toDto(Purchase entity);
}
