package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePatchDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePostDto;
import dev.kons.kuenyawz.entities.Purchase;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public class PurchaseServiceImpl implements PurchaseService {
	@Override
	public Page<PurchaseDto> findAll(PurchaseSearchCriteria criteria) {
		return null;
	}

	@Override
	public Page<PurchaseDto> findAll(Long accountId, PurchaseSearchCriteria criteria) {
		return null;
	}

	@Override
	public PurchaseDto findById(Long purchaseId) {
		return null;
	}

	@Override
	public PurchaseDto findByTransactionId(Long transactionId) {
		return null;
	}

	@Override
	public PurchaseDto create(PurchasePostDto purchasePostDto) {
		return null;
	}

	@Override
	public PurchaseDto patch(Long purchaseId, PurchasePatchDto purchasePatchDto) {
		return null;
	}

	@Override
	public PurchaseDto cancel(Long purchaseId) {
		return null;
	}

	@Override
	public PurchaseDto confirm(Long purchaseId) {
		return null;
	}

	@Override
	public PurchaseDto changeFee(Long purchaseId, BigDecimal fee) {
		return null;
	}

	@Override
	public PurchaseDto changeStatus(Long purchaseId, Purchase.PurchaseStatus status) {
		return null;
	}
}
