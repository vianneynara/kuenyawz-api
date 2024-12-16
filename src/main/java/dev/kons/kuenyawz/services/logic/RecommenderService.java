package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.product.ProductDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RecommenderService {
	/**
	 * Get recommended products for a given product, it is limited up to 3 products.
	 *
	 * @param productId {@link Long} the product id
	 * @return {@link List} of {@link ProductDto} recommended products
	 * @apiNote It currently only mocks the function, it will be implemented in the future.
	 */
	List<ProductDto> getRecommendsOfProduct(Long productId, Boolean addRandom);

	/**
	 * Trigger the Apriori algorithm to generate the frequent item sets.
	 */
	void generateApriori();

	/**
	 * Clear all the Apriori recommendations.
	 */
	@Transactional
	void clearAprioriRecommendations();
}
