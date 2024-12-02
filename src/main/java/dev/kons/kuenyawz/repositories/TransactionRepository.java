package dev.kons.kuenyawz.repositories;

import dev.kons.kuenyawz.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

	Optional<Transaction> findByReferenceId(String referenceId);

	Page<Transaction> findByPurchase_PurchaseId(Long purchaseId, Pageable pageable);

	List<Transaction> findByPurchase_PurchaseIdAndAccount_AccountId(Long purchaseId, Long accountId);
}
