package dev.kons.kuenyawz.repositories;

import dev.kons.kuenyawz.constants.PaymentType;
import dev.kons.kuenyawz.entities.Transaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TransactionSpec {

	public static Specification<Transaction> withAccountId(Long accountId) {
		return (root, query, cb) -> {
			if (accountId == null)
				return null;

			return cb.equal(root.get("account").get("accountId"), accountId);
		};
	}

	public static Specification<Transaction> withStatus(Transaction.TransactionStatus status) {
		return (root, query, cb) -> {
			if (status == null)
				return null;

			return cb.equal(root.get("status"), status);
		};
	}

	public static Specification<Transaction> withPurchaseId(Long purchaseId) {
		return (root, query, cb) -> {
			if (purchaseId == null)
				return null;

			return cb.equal(root.get("purchase").get("purchaseId"), purchaseId);
		};
	}

	public static Specification<Transaction> withInvoiceId(String referenceId) {
		return (root, query, cb) -> {
			if (referenceId == null)
				return null;

			return cb.equal(root.get("referenceId"), referenceId);
		};
	}

	public static Specification<Transaction> withPaymentType(PaymentType paymentType) {
		return (root, query, cb) -> {
			if (paymentType == null)
				return null;

			return cb.equal(root.get("paymentType"), paymentType);
		};
	}

	public static Specification<Transaction> withDateAfter(LocalDate date) {
		return (root, query, cb) -> {
			if (date == null)
				return null;

			return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
		};
	}

	public static Specification<Transaction> withDateBefore(LocalDate date) {
		return (root, query, cb) -> {
			if (date == null)
				return null;

			return cb.lessThanOrEqualTo(root.get("createdAt"), date);
		};
	}

	public static Specification<Transaction> withDateBetween(LocalDate from, LocalDate to) {
		return (root, query, cb) -> {
			if (from == null || to == null)
				return null;

			return cb.between(root.get("createdAt"), from, to);
		};
	}
}
