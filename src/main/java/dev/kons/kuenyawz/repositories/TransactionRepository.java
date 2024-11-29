package dev.kons.kuenyawz.repositories;

import dev.kons.kuenyawz.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

	Optional<Transaction> findByReferenceId(String referenceId);

	List<Transaction> findByPurchase_PurchaseId(Long purchaseId);
}
