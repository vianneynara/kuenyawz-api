package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductPatchDto;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {

	@Transactional(readOnly = true)
	List<ProductDto> getAllProducts();

	@Transactional
	ProductDto createProduct(ProductPostDto productDto);

	@Transactional(readOnly = true)
	ProductDto getProduct(long productId);

	@Transactional(readOnly = true)
	List<ProductDto> getAllProductByKeyword(String keyword);

	@Transactional(readOnly = true)
	List<ProductDto> getProductsByCategory(String category);

	@Transactional
	void deleteProduct(long productId);

	@Transactional
	ProductDto patchProduct(Long productId, ProductPatchDto productPatchDto);
}
