package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import dev.realtards.kuenyawz.repositories.ProductRepository;
import dev.realtards.kuenyawz.repositories.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommenderServiceImpl implements RecommenderService {

	private final ProductService productService;
	private final ProductRepository productRepository;

	@Override
	public List<ProductDto> getRecommendsOfProduct(Long productId, Object addRandom) {
		if (!productService.existsById(productId)) {
			throw new ResourceNotFoundException("Product not found");
		}
		if (productRepository.count() < 3) {
			throw new ResourceNotFoundException("Not enough products to recommend");
		}
		Specification<Product> spec = ProductSpecification.withFilters(
			null, null, null,
			null, null, true, productId);
		List<Product> products = productRepository.findAll(spec, PageRequest.of(0, 2)).toList();

		return products.stream().map(productService::convertToDto).toList();
	}
}
