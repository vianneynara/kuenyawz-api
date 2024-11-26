package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.entities.Purchase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

	PurchaseDto toDto(Purchase entity);
}
