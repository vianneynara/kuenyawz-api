package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.product.ProductDto;
import dev.kons.kuenyawz.dtos.product.ProductPatchDto;
import dev.kons.kuenyawz.dtos.product.ProductPostDto;
import dev.kons.kuenyawz.dtos.product.VariantDto;
import dev.kons.kuenyawz.entities.Product;
import dev.kons.kuenyawz.services.logic.ImageStorageService;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Comparator;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	Product toEntity(ProductPostDto productPostDto);

	@Mapping(target = "images", ignore = true)
	ProductDto fromEntity(Product product);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "images", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
	Product updateProductFromPatch(ProductPatchDto dto, @MappingTarget Product product);

	/**
	 * This is used to fill the images after mapping the entity to dto.
	 *
	 * @param product Source
	 * @param productDto Target
	 * @param imageStorageService Image storage service that will be injected by MapStruct
	 */
    @AfterMapping
    default void enrichProductDto(
        Product product,
        @MappingTarget ProductDto productDto,
        @Context ImageStorageService imageStorageService
    ) {
        if (productDto.getVariants() == null) {
           productDto.setVariants(new ArrayList<>());
        }
        productDto.getVariants().sort(Comparator.comparing(VariantDto::getVariantId));
        productDto.setImages(imageStorageService.getImageUrls(product));
    }
}
