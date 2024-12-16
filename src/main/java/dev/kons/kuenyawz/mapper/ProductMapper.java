package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.product.ProductDto;
import dev.kons.kuenyawz.dtos.product.ProductPatchDto;
import dev.kons.kuenyawz.dtos.product.ProductPostDto;
import dev.kons.kuenyawz.dtos.product.VariantDto;
import dev.kons.kuenyawz.entities.Product;
import dev.kons.kuenyawz.entities.Variant;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {VariantMapper.class})
public interface ProductMapper {
	Product toEntity(ProductPostDto productPostDto);

	@Mapping(target = "images", ignore = true)
    @Mapping(target = "variants", qualifiedByName = "variantsToSortedList")
	ProductDto fromEntity(Product product);

	@Mapping(target = "version", ignore = true)
	@Mapping(target = "variants", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
	Product updateProductFromPatch(ProductPatchDto dto, @MappingTarget Product product);

    @Named("variantsToSortedList")
    default List<VariantDto> variantsToSortedList(Set<Variant> variants) {
        if (variants == null) {
            return null;
        }
        VariantMapper variantMapper = Mappers.getMapper(VariantMapper.class);
        return variants.stream()
            .map(variantMapper::fromEntity)
            .sorted(Comparator.comparing(VariantDto::getVariantId))
            .collect(Collectors.toList());
    }
}
