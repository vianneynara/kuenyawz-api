package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.purchase.TransactionDto;
import dev.kons.kuenyawz.dtos.purchase.TransactionPatchDto;
import dev.kons.kuenyawz.dtos.purchase.TransactionPostDto;
import org.springframework.data.domain.Page;

public class TransactionServiceImpl implements TransactionService {
	@Override
	public Page<TransactionDto> findAll(TransactionSearchCriteria criteria) {
		return null;
	}

	@Override
	public Page<TransactionDto> findAll(Long accountId, TransactionSearchCriteria criteria) {
		return null;
	}

	@Override
	public TransactionDto findById(Long transactionId) {
		return null;
	}

	@Override
	public TransactionDto findByInvoiceId(String invoiceId) {
		return null;
	}

	@Override
	public Page<TransactionDto> findByPurchaseId(Long purchaseId) {
		return null;
	}

	@Override
	public TransactionDto create(TransactionPostDto transactionPostDto) {
		return null;
	}

	@Override
	public TransactionDto patch(Long transactionId, TransactionPatchDto transactionPatchDto) {
		return null;
	}
}
