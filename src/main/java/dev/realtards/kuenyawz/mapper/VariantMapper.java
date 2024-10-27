package dev.realtards.kuenyawz.mapper;

import dev.realtards.kuenyawz.dtos.product.VariantDto;
import dev.realtards.kuenyawz.dtos.product.VariantPatchDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.entities.Variant;
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
	void updateVariantFromPatch(VariantPatchDto dto, @MappingTarget Variant variant);
}
