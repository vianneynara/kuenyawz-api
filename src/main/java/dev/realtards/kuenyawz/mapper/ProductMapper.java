package dev.realtards.kuenyawz.mapper;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductPatchDto;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.entities.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	Product toEntity(ProductPostDto productPostDto);

	ProductDto fromEntity(Product product);

	@BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
	void updateProductFromPatch(ProductPatchDto dto, @MappingTarget Product product);
}
