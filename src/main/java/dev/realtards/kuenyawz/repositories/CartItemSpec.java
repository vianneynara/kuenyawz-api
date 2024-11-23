package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.entities.CartItem;
import dev.realtards.kuenyawz.entities.Variant;
import dev.realtards.kuenyawz.exceptions.SpecificationException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CartItemSpec {
	/**
	 * Finds CartItems that have the same account id as the requested account id.
	 *
	 * @param accountId {@link Long} Account id to be queried.
	 * @return Specification of CartItem.
	 */
	public static Specification<CartItem> withAccountId(Long accountId) {
		return (root, query, criteriaBuilder) -> {
			if (accountId == null) {
				return null;
			}
			return criteriaBuilder.equal(root.get("account").get("accountId"), accountId);
		};
	}

	public static Specification<CartItem> withSameProductAsVariantId(Long variantId) {
		return (root, query, criteriaBuilder) -> {
			if (variantId == null) {
				return null;
			}
			if (query == null) {
				throw new SpecificationException("Query is null for " + CartItem.class.getSimpleName());
			}
			Join<CartItem, Variant> variantJoin = root.join("variant");

			Subquery<Long> productSubquery = query.subquery(Long.class);
			Root<Variant> subRoot = productSubquery.from(Variant.class);
			productSubquery.select(subRoot.get("product").get("productId"))
				.where(criteriaBuilder.equal(subRoot.get("variantId"), variantId));

			return criteriaBuilder.equal(variantJoin.get("product").get("productId"),
				productSubquery);
		};
	}

	/**
	 * Queries CartItems that an account have that inherits from the same product of the requested
	 * variant id.
	 * <br>
	 * <br>
	 * This is done by basically joining CartItem with Variant and Account.
	 * After that, a subquery is created to get the product id of the requested variant id
	 * where each product have the same variant id as the requested variant id.
	 * <br>
	 * <br>
	 * Lastly, sets predicates to check if the product id of the variant id is the same as the product id
	 * and if the account id is the same as the requested account id.
	 *
	 * @param variantId {@link Long} Variant id to be queried.
	 * @param accountId {@link Long} Account id to be queried.
	 * @return
	 */
	public static Specification<CartItem> withSameProductAsVariantIdAndAccountId(Long variantId, Long accountId) {
		return (root, query, criteriaBuilder) -> {
			if (variantId == null) {
				return null;
			}

			if (query == null) {
				throw new SpecificationException("Query is null for " + CartItem.class.getSimpleName());
			}
			Join<CartItem, Variant> variantJoin = root.join("variant");
			Join<CartItem, Account> accountJoin = root.join("account");

			Subquery<Long> productSubquery = query.subquery(Long.class);
			Root<Variant> productVariantRoot = productSubquery.from(Variant.class);
			productSubquery.select(productVariantRoot.get("product").get("productId"))
				.where(criteriaBuilder.equal(productVariantRoot.get("variantId"), variantId));

			return criteriaBuilder.and(
				criteriaBuilder.equal(variantJoin.get("product").get("productId"), productSubquery),
				criteriaBuilder.equal(accountJoin.get("accountId"), accountId)
			);
		};
	}
}
