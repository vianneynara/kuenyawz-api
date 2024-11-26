package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePatchDto;
import dev.kons.kuenyawz.entities.Purchase;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {PurchaseItemMapper.class, TransactionMapper.class})
public interface PurchaseMapper {

    @Mapping(target = "latitude", source = "coordinate.latitude")
    @Mapping(target = "longitude", source = "coordinate.longitude")
	PurchaseDto toDto(Purchase entity);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "coordinate.latitude", source = "latitude")
    @Mapping(target = "coordinate.longitude", source = "longitude")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Purchase updateFromPatchDto(PurchasePatchDto dto, @MappingTarget Purchase entity);
}
