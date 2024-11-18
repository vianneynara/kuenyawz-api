package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Product;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * This class stores specifications for product entity queries. It is better to implement this kind
 * of specifications rather than manually writing queries in the repository.
 * <br>
 * <br>
 * How does it work? Well, every static method here excluding {@link #withFilters(String, String, Boolean)} is a method that
 * returns a lambda expression that takes three parameters: root, query, and criteriaBuilder:
 * <ul>
 *     <li>root: The root of the entity that is being queried ({@link Product}).</li>
 *     <li>query: The query that is being executed.</li>
 *     <li>criteriaBuilder: The criteria builder that is used to build the query.</li>
 * </ul>
 * Method {@link #withFilters(String, String, Boolean)} will combine the specification methods and return it as a full
 * specification.
 */
@Component
public class ProductSpecification {
	/**
	 * Combine all specifications.
	 *
	 * @param category     {@link String}
	 * @param keyword      {@link String}
	 * @param availability {@link Boolean}
	 * @return {@link Specification<Product>}
	 */
	public static Specification<Product> withFilters(String category, String keyword, Boolean availability) {
		return Specification
			.where(withCategory(category))
			.and(withKeywordLike(keyword))
			.and(withAvailability(availability));
	}

	/**
	 * Combine all specifications but with more advanced parameters.
	 *
	 * @param category     {@link String}
	 * @param keyword      {@link String}
	 * @param availability {@link Boolean}
	 * @param orderBy      {@link String}
	 * @param isAscending  {@link Boolean}
	 * @param randomize    {@link Boolean}
	 * @param productIdNot {@link Long}
	 * @return {@link Specification<Product>}
	 */
	public static Specification<Product> withFilters(
		String category,
		String keyword,
		Boolean availability,
		String orderBy,
		Boolean isAscending,
		Boolean randomize,
		Long productIdNot
	) {
		return (root, query, criteriaBuilder) -> {
			Predicate finalPredicate = Specification
				.where(withCategory(category))
				.and(withKeywordLike(keyword))
				.and(withAvailability(availability))
				.and(withProductIdNot(productIdNot))
				.toPredicate(root, query, criteriaBuilder);
			// Order By
			if (query != null && StringUtils.hasText(orderBy)) {
				try {
					Path<?> orderPath = root.get(orderBy);
					Order order = (isAscending == null || isAscending)
						? criteriaBuilder.asc(orderPath)
						: criteriaBuilder.desc(orderPath);
					query.orderBy(order);
				} catch (IllegalArgumentException ignored) {
				}
			}
			// Randomize
			if (query != null && Boolean.TRUE.equals(randomize)) {
				query.orderBy(criteriaBuilder.asc(criteriaBuilder.function("random", Double.class)));
			}

			return finalPredicate;
		};
	}

	/**
	 * Filter {@link Product} category with category enum.
	 */
	private static Specification<Product> withCategory(String category) {
		return ((root, query, criteriaBuilder) -> {
			if (!StringUtils.hasText(category)) {
				return null;
			}
			try {
				Product.Category productCategory = Product.Category.valueOf(category.trim().toUpperCase());
				return criteriaBuilder.equal(root.get("category"), productCategory);
			} catch (IllegalArgumentException e) {
				return null;
			}
		});
	}

	/**
	 * Filter {@link Product} name with case-insensitive keyword.
	 * Done by converting the keyword and name to lowercase.
	 */
	private static Specification<Product> withKeywordLike(String keyword) {
		return ((root, query, criteriaBuilder) -> {
			if (!StringUtils.hasText(keyword)) {
				return null;
			}
			final String queryKeyword = "%" + keyword.trim().toLowerCase() + "%";
			return criteriaBuilder.like(
				criteriaBuilder.lower(root.get("name")),
				queryKeyword.toLowerCase()
			);
		});
	}

	/**
	 * Filter {@link Product} availability with boolean value.
	 */
	private static Specification<Product> withAvailability(Boolean availability) {
		return ((root, query, criteriaBuilder) -> {
			if (availability == null) {
				return null;
			}
			return criteriaBuilder.equal(root.get("available"), availability);
		});
	}

	private static Specification<Product> withProductIdNot(Long productId) {
		return ((root, query, criteriaBuilder) -> {
			if (productId == null) {
				return null;
			}
			return criteriaBuilder.notEqual(root.get("productId"), productId);
		});
	}
}
