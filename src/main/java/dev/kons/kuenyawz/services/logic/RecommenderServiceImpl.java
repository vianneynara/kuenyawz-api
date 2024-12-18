package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.product.ProductDto;
import dev.kons.kuenyawz.entities.Apriori;
import dev.kons.kuenyawz.entities.Product;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.exceptions.IllegalOperationException;
import dev.kons.kuenyawz.exceptions.ResourceNotFoundException;
import dev.kons.kuenyawz.repositories.AprioriRepository;
import dev.kons.kuenyawz.repositories.ProductRepository;
import dev.kons.kuenyawz.repositories.ProductSpec;
import dev.kons.kuenyawz.services.entity.ProductService;
import dev.kons.kuenyawz.services.entity.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommenderServiceImpl implements RecommenderService {

	private final ProductService productService;
	private final ProductRepository productRepository;
	private final AprioriService aprioriService;
	private final PurchaseService purchaseService;
	private final AprioriRepository aprioriRepository;

	@Override
	public List<ProductDto> getRecommendsOfProduct(Long productId, Boolean addRandom) {
		return newRecommender(productId);
	}

	@Override
	public void generateApriori() {
		clearAprioriRecommendations();

		Map<Long, Set<Long>> purchaseData = gatherPurchaseData();
		var ruleSets = aprioriService.findAllFrequentSetOfItems(purchaseData);

		for (Map.Entry<Long, Set<Long>> entry : ruleSets.entrySet()) {
			Long productId = entry.getKey();
			Set<Long> recommendedIds = entry.getValue();

			Apriori apriori = new Apriori();
			apriori.setProductId(productId);

			Iterator<Long> iterator = recommendedIds.iterator();
			apriori.setRecommended1(iterator.hasNext() ? iterator.next() : addOneRandom(productId, recommendedIds));
			apriori.setRecommended2(iterator.hasNext() ? iterator.next() : addOneRandom(productId, recommendedIds));
			apriori.setRecommended3(iterator.hasNext() ? iterator.next() : addOneRandom(productId, recommendedIds));


			aprioriRepository.save(apriori);
		}
	}

	@Override
	public void clearAprioriRecommendations() {
		try {
			aprioriRepository.deleteAll();
		} catch (Exception e) {
			throw new IllegalOperationException("Failed to delete Apriori recommendations");
		}
	}

	private Map<Long, Set<Long>> gatherPurchaseData() {
		List<Purchase> purchases = purchaseService.getAprioriNeeds();
		return convertToAprioriSource(purchases);
	}

	public Map<Long, Set<Long>> convertToAprioriSource(List<Purchase> purchases) {
		Map<Long, Set<Long>> purchaseIdAndProductIds = new HashMap<>();

		// Create set of product ids for each purchase and put it to the map
		for (Purchase purchase : purchases) {
			Set<Long> productIds = purchase.getPurchaseItems().stream()
				.map(purchaseItem -> purchaseItem.getVariant().getProduct().getProductId())
				.collect(Collectors.toSet());
			purchaseIdAndProductIds.put(purchase.getPurchaseId(), productIds);
		}

		return purchaseIdAndProductIds;
	}

	private List<ProductDto> newRecommender(Long productId) {
		Optional<Apriori> result = aprioriRepository.findByProductId(productId);
		if (result.isEmpty()) {
			return oldRecommender(productId, true);
		} else {
			final var apriori = result.get();
			List<ProductDto> recommendations = new ArrayList<>();

			Optional.ofNullable(apriori.getRecommended1())
				.map(productService::getProduct)
				.filter(ProductDto::isAvailable)
				.ifPresent(recommendations::add);

			Optional.ofNullable(apriori.getRecommended2())
				.map(productService::getProduct)
				.filter(ProductDto::isAvailable)
				.ifPresent(recommendations::add);

			Optional.ofNullable(apriori.getRecommended3())
				.map(productService::getProduct)
				.filter(ProductDto::isAvailable)
				.ifPresent(recommendations::add);

			return recommendations.isEmpty()
				? oldRecommender(productId, true)
				: recommendations;
		}
	}

	private List<ProductDto> oldRecommender(Long productId, Boolean addRandom) {
		if (!productService.existsById(productId)) {
			throw new ResourceNotFoundException("Product not found");
		}
		if (productRepository.count() < 4) {
			throw new IllegalOperationException("Not enough products to recommend");
		}
		addRandom = (addRandom != null && addRandom);
		Specification<Product> spec = ProductSpec.withFilters(
			null, null, true,
			null, null, true, productId);
		List<Product> products = productRepository.findAll(spec,
			addRandom ? PageRequest.of(0, 3) : PageRequest.of(0, 1)
		).toList();

		return products.stream().map(productService::convertToDto).toList();
	}

	private Long addOneRandom(Long productId, Set<Long> excludeSet) {
		excludeSet.add(productId);

		List<Long> allProductIds = productRepository.findAllAvailableIds();
		List<Long> filteredProductIds = allProductIds.stream()
				.filter(id -> !excludeSet.contains(id))
				.collect(Collectors.toList());

		return filteredProductIds.get(new Random().nextInt(filteredProductIds.size()));
	}
}
