package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductPatchDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {

	@Transactional(readOnly = true)
	List<ProductDto> getAllAccounts();

	@Transactional
	ProductDto createProduct(ProductDto productDto);

	@Transactional(readOnly = true)
	ProductDto getProduct(long productId);

	@Transactional(readOnly = true)
	ProductDto getProduct(String productName);

	@Transactional(readOnly = true)
	List<ProductDto> getProductsByCategory(String category);

	@Transactional
	ProductDto patchProduct(Long productId, ProductPatchDto productPatchDto);

	@Transactional
	void deleteProduct(long productId);
}
