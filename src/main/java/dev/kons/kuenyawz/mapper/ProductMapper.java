package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.product.ProductDto;
import dev.kons.kuenyawz.dtos.product.ProductPatchDto;
import dev.kons.kuenyawz.dtos.product.ProductPostDto;
import dev.kons.kuenyawz.entities.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	Product toEntity(ProductPostDto productPostDto);

	@Mapping(target = "images", ignore = true)
	ProductDto fromEntity(Product product);

	@Mapping(target = "version", ignore = true)
	@Mapping(target = "variants", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
	Product updateProductFromPatch(ProductPatchDto dto, @MappingTarget Product product);
}
