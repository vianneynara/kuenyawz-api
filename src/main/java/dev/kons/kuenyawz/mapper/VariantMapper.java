package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.product.VariantDto;
import dev.kons.kuenyawz.dtos.product.VariantPatchDto;
import dev.kons.kuenyawz.dtos.product.VariantPostDto;
import dev.kons.kuenyawz.entities.Variant;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VariantMapper {

	Variant toEntity(VariantPostDto variantPostDto);

	VariantDto fromEntity(Variant variant);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "product", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
	Variant updateVariantFromPatch(VariantPatchDto dto, @MappingTarget Variant variant);
}
