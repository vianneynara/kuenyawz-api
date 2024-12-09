package dev.kons.kuenyawz.repositories;

import dev.kons.kuenyawz.constants.PaymentType;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.entities.CartItem;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.entities.Transaction;
import dev.kons.kuenyawz.exceptions.SpecificationException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class PurchaseSpec {
	/**
	 * Joins table Purchase with its Transaction table to get the related account id.\
	 *
	 * @param accountId {@link Long} Account id to be queried.
	 * @return Specification of Purchase.
	 */
	public static Specification<Purchase> withAccountId1(Long accountId) {
		return (root, query, cb) -> {
			if (accountId == null)
				return null;

			Join<Purchase, Transaction> transactionJoin = root.join("transactions");
			Join<Transaction, Account> accountJoin = transactionJoin.join("account");

			return cb.equal(accountJoin.get("accountId"), accountId);
		};
	}

	/**
	 * Uses subquery to get the related account id from the Transaction table.
	 *
	 * @param accountId {@link Long} Account id to be queried.
	 * @return Specification of Purchase.
	 */
	public static Specification<Purchase> withAccountId2(Long accountId) {
		return (root, query, cb) -> {
			if (accountId == null)
				return null;
			if (query == null)
				throw new SpecificationException("Query is null for " + CartItem.class.getSimpleName());

			Subquery<Long> subquery = query.subquery(Long.class);
			Root<Transaction> transactionRoot = subquery.from(Transaction.class);
			subquery.select(transactionRoot.get("purchase").get("purchaseId"))
				.where(cb.equal(transactionRoot.get("account").get("accountId"), accountId));

			return cb.exists(subquery);
		};
	}

	public static Specification<Purchase> withStatus(Purchase.PurchaseStatus status) {
		return (root, query, cb) -> {
			if (status == null)
				return null;

			return cb.equal(root.get("status"), status.getStatus());
		};
	}

	public static Specification<Purchase> withStatuses(List<Purchase.PurchaseStatus> statuses) {
		return (root, query, cb) -> {
			if (statuses == null || statuses.isEmpty())
				return null;

			return root.get("status").in(statuses);
		};
	}

	public static Specification<Purchase> withPaymentType(PaymentType paymentType) {
		return (root, query, cb) -> {
			if (paymentType == null)
				return null;

			return cb.equal(root.get("paymentType"), paymentType);
		};
	}

	public static Specification<Purchase> withDateAfter(LocalDate after) {
		return (root, query, cb) -> {
			if (after == null)
				return null;

			return cb.greaterThanOrEqualTo(root.get("createdAt"), after);
		};
	}

	public static Specification<Purchase> withDateBefore(LocalDate before) {
		return (root, query, cb) -> {
			if (before == null)
				return null;

			return cb.lessThanOrEqualTo(root.get("createdAt"), before);
		};
	}

	public static Specification<Purchase> withDateBetween(LocalDate from, LocalDate to) {
		return (root, query, cb) -> {
			if (from == null || to == null)
				return null;

			return cb.between(root.get("createdAt"), from, to);
		};
	}
}
