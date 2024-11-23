package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.product.ProductDto;
import dev.kons.kuenyawz.entities.Product;
import dev.kons.kuenyawz.exceptions.IllegalOperationException;
import dev.kons.kuenyawz.exceptions.ResourceNotFoundException;
import dev.kons.kuenyawz.repositories.ProductRepository;
import dev.kons.kuenyawz.repositories.ProductSpec;
import dev.kons.kuenyawz.services.entity.ProductService;
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
	public List<ProductDto> getRecommendsOfProduct(Long productId, Boolean addRandom) {
		if (!productService.existsById(productId)) {
			throw new ResourceNotFoundException("Product not found");
		}
		if (productRepository.count() < 4) {
			throw new IllegalOperationException("Not enough products to recommend");
		}
		addRandom = (addRandom != null && addRandom);
		Specification<Product> spec = ProductSpec.withFilters(
			null, null, null,
			null, null, true, productId);
		List<Product> products = productRepository.findAll(spec,
			addRandom ? PageRequest.of(0, 3) : PageRequest.of(0, 1)
		).toList();

		return products.stream().map(productService::convertToDto).toList();
	}
}
