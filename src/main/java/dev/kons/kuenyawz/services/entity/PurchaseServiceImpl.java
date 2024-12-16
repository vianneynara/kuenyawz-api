package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.constants.PaymentType;
import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePatchDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePostDto;
import dev.kons.kuenyawz.entities.Coordinate;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.entities.PurchaseItem;
import dev.kons.kuenyawz.entities.Variant;
import dev.kons.kuenyawz.exceptions.IllegalOperationException;
import dev.kons.kuenyawz.exceptions.UnauthorizedException;
import dev.kons.kuenyawz.mapper.PurchaseMapper;
import dev.kons.kuenyawz.repositories.PurchaseRepository;
import dev.kons.kuenyawz.repositories.PurchaseSpec;
import dev.kons.kuenyawz.services.logic.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {

	private final PurchaseRepository purchaseRepository;
	private final PurchaseMapper purchaseMapper;
	private final TransactionService transactionService;
	private final ApplicationProperties properties;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final VariantService variantService;

	/// The caching is specifically targeted for admin
	@Override
	@Cacheable(
		value = "purchasesCache",
		key = "'admin_' + T(java.util.Objects).hash(" +
			"    #criteria.statuses, " +
			"    #criteria.paymentType, " +
			"    #criteria.from, " +
			"    #criteria.to, " +
			"    #criteria.orderBy, " +
			"    #criteria.page, " +
			"    #criteria.pageSize, " +
			"    #criteria.isAscending" +
			")",
		condition = "#criteria.page != null && #criteria.pageSize != null"
	)
	public Page<PurchaseDto> findAll(PurchaseSearchCriteria criteria) {
		log.info("Fetching purchases for admin of page: {}, pageSize: {}", criteria.getPage(), criteria.getPageSize());

		AuthService.validateIsAdmin();
		return findAllHelper(criteria);
	}

	/// The caching is specifically targeted for user
	@Override
	@Cacheable(
		value = "purchasesCache",
		key = "'user_' + #accountId + '_' + T(java.util.Objects).hash(" +
			"    #criteria.statuses, " +
			"    #criteria.paymentType, " +
			"    #criteria.from, " +
			"    #criteria.to, " +
			"    #criteria.orderBy, " +
			"    #criteria.page, " +
			"    #criteria.pageSize, " +
			"    #criteria.isAscending" +
			")",
		condition = "#criteria.page != null && #criteria.pageSize != null"
	)
	public Page<PurchaseDto> findAll(Long accountId, PurchaseSearchCriteria criteria) {
		log.info("Fetching purchases for account of page: {}, pageSize: {}", criteria.getPage(), criteria.getPageSize());

		AuthService.validateMatchesId(accountId);
		criteria.setAccountId(accountId);
		return findAllHelper(criteria);
	}

	private Page<PurchaseDto> findAllHelper(PurchaseSearchCriteria criteria) {
		Specification<Purchase> spec = PurchaseSpec.withAccountId1(criteria.getAccountId())
			.and(PurchaseSpec.withStatuses(criteria.getStatuses()))
			.and(PurchaseSpec.withPaymentType(criteria.getPaymentType()));

		if (criteria.getFrom() != null && criteria.getTo() != null) {
			spec = spec.and(PurchaseSpec.withDateBetween(criteria.getFrom(), criteria.getTo()));
		} else if (criteria.getFrom() != null) {
			spec = spec.and(PurchaseSpec.withDateAfter(criteria.getFrom()));
		} else if (criteria.getTo() != null) {
			spec = spec.and(PurchaseSpec.withDateBefore(criteria.getTo()));
		}

		Sort sorter = Sort.by(criteria.getIsAscending()
			? Sort.Order.asc(criteria.getOrderBy())
			: Sort.Order.desc(criteria.getOrderBy())
		);
		Pageable pageable = criteria.getPageable(sorter);
		return purchaseRepository.findAll(spec, pageable).map(this::convertToDto);
	}

	@Override
	public List<Purchase> getAprioriNeeds() {
		Specification<Purchase> spec = PurchaseSpec
			.withStatus(Purchase.PurchaseStatus.CONFIRMED)
			.or(PurchaseSpec.withStatus(Purchase.PurchaseStatus.DELIVERED));

		return purchaseRepository.findAll(spec);
	}

	@Override
	public PurchaseDto findById(Long purchaseId) {
		final Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
		validateAccess(purchase);
		return convertToDto(purchase);
	}

	@Override
	public Purchase getById(Long purchaseId) {
		return purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
	}


	@Override
	public PurchaseDto findByTransactionId(Long transactionId) {
		final Purchase purchase = transactionService.getById(transactionId).getPurchase();
		validateAccess(purchase);
		return convertToDto(purchase);
	}

	@Override
	public PurchaseDto findByInvoiceId(String invoiceId) {
		final Purchase purchase = transactionService.getByInvoiceId(invoiceId).getPurchase();
		validateAccess(purchase);
		return convertToDto(purchase);
	}

	@Override
	public Purchase create(PurchasePostDto purchasePostDto) {
		if (!AuthService.isAuthenticatedUser())
			throw new UnauthorizedException("You are not authorized to perform this action");
		double distance = Coordinate.of(purchasePostDto.getLatitude(), purchasePostDto.getLongitude())
			.calculateDistance(properties.vendor().getLatitude(), properties.vendor().getLongitude());
		if (distance > 30.0) {
			throw new IllegalArgumentException("Coordinate should not exceed 30 km from the vendor: " + distance);
		}

		Purchase purchase = buildPurchaseFromDto(purchasePostDto);
		Purchase savedPurchase = purchaseRepository.save(purchase);

		// Create transactions for the purchase

		return savedPurchase;
	}

	private Purchase buildPurchaseFromDto(PurchasePostDto purchasePostDto) {
		Purchase purchase = Purchase.builder()
			.fullAddress(purchasePostDto.getFullAddress())
			.paymentType(PaymentType.fromString(purchasePostDto.getPaymentType()))
			.coordinate(Coordinate.of(purchasePostDto.getLatitude(), purchasePostDto.getLongitude()))
			.eventDate(LocalDate.parse(purchasePostDto.getEventDate(), dateTimeFormatter))
			.paymentType(PaymentType.fromString(purchasePostDto.getPaymentType()))
			.deliveryOption(Purchase.DeliveryOption.fromString(purchasePostDto.getDeliveryOption()))
			.status(Purchase.PurchaseStatus.PENDING)
			.build();

		// Calculate delivery fee if not provided
		if (purchasePostDto.getDeliveryFee() != null) {
			purchase.setDeliveryFee(BigDecimal.valueOf(purchasePostDto.getDeliveryFee()));
		} else {
			BigDecimal deliveryFee = purchase.getDeliveryOption() == Purchase.DeliveryOption.DELIVERY
				? calcDeliveryFee(purchase)
				: BigDecimal.ZERO;
			purchase.setDeliveryFee(deliveryFee);
		}

		List<PurchaseItem> purchaseItems = purchasePostDto.getPurchaseItems().stream()
			.map(dto -> {
					Variant variant = variantService.getVariantById(dto.getVariantId());
					VariantService.validateQuantityConsistent(variant, dto.getQuantity());

					PurchaseItem purchaseItem = PurchaseItem.builder()
						.note(dto.getNote())
						.quantity(dto.getQuantity())
						.boughtPrice(variant.getPrice())
						.variant(variant)
						.purchase(purchase)
						.build();

					return purchaseItem;
				}
			).toList();

		purchase.setPurchaseItems(purchaseItems);
		return purchase;
	}

	public BigDecimal getTotalPriceWIthFee(Purchase purchase) {
		return purchase.getTotalPrice().add(calcDeliveryFee(purchase));
	}

	public BigDecimal calcDeliveryFee(Purchase purchase) {
		final var vendorLat = properties.vendor().getLatitude();
		final var vendorLong = properties.vendor().getLongitude();

		int distanceInKm = (int) Math.floor(purchase.getCoordinate().calculateDistance(vendorLat, vendorLong));

		return BigDecimal.valueOf(distanceInKm)
			.multiply(BigDecimal.valueOf(properties.vendor().getFeePerKm()));
	}

	@Override
	public PurchaseDto patch(Long purchaseId, PurchasePatchDto purchasePatchDto) {
		AuthService.validateIsAdmin();
		Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

		purchase = purchaseMapper.updateFromPatchDto(purchasePatchDto, purchase);
		Purchase saved = purchaseRepository.save(purchase);
		return convertToDto(saved);
	}

	@Override
	public PurchaseDto cancel(Long purchaseId) {
		AuthService.validateIsAdmin();
		final Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

		// Cancels transactions of the purchase
		transactionService.cancelAllOf(purchaseId);

		purchase.setStatus(Purchase.PurchaseStatus.CANCELLED);
		Purchase saved = purchaseRepository.save(purchase);
		return convertToDto(saved);
	}

	@Override
	public PurchaseDto confirm(Long purchaseId) {
		final Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
		if (purchase.isConfirmed())
			throw new IllegalOperationException("Purchase has already been confirmed");
		purchase.setStatus(Purchase.PurchaseStatus.CONFIRMED);
		Purchase saved = purchaseRepository.save(purchase);
		return convertToDto(saved);
	}

	@Override
	public PurchaseDto changeFee(Long purchaseId, BigDecimal fee) {
		final Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
		if (purchase.isConfirmed())
			throw new IllegalOperationException("Purchase has already been confirmed");
		if (fee.compareTo(BigDecimal.ZERO) < 0)
			throw new IllegalArgumentException("Fee cannot be negative");
		purchase.setDeliveryFee(fee);
		Purchase saved = purchaseRepository.save(purchase);
		return convertToDto(saved);
	}

	@Override
	public PurchaseDto changeStatus(Long purchaseId, Purchase.PurchaseStatus status) {
		final Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
		if (purchase.getStatus().ordinal() > status.ordinal())
			throw new IllegalOperationException("Cannot change status to a lower status");
		Purchase saved = purchaseRepository.save(purchase);
		return convertToDto(saved);
	}

	@Override
	public PurchaseDto upgradeStatus(Long purchaseId) {
		final Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
		if (purchase.isFinished())
			throw new IllegalOperationException("Cannot progress beyond finished status: " + purchase.getStatus());

		purchase.setStatus(purchase.getStatus().next());
		Purchase saved = purchaseRepository.save(purchase);
		return convertToDto(saved);
	}

	@Override
	public PurchaseDto convertToDto(Purchase purchase) {
		return purchaseMapper.toDto(purchase);
	}

	@Override
	public Map<String, String> availableStatuses(Long purchaseId) {
		final Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
		final var currentStatus = purchase.getStatus();

		if (currentStatus == Purchase.PurchaseStatus.CANCELLED)
			throw new IllegalOperationException("Cannot progress beyond finished status: " + currentStatus);

		Map<String, String> statusMap = new LinkedHashMap<>();

		Purchase.PurchaseStatus nextStatus = currentStatus;
		while (nextStatus != null) {
			try {
				nextStatus = nextStatus.next();
				if (nextStatus == Purchase.PurchaseStatus.CANCELLED)
					break;
				statusMap.put(nextStatus.name(), nextStatus.getDescription());
			} catch (IllegalOperationException e) {
				break;
			}
		}

		return statusMap;
	}

	/**
	 * Checks access of account to the purchase.
	 *
	 * @param purchase {@link Purchase} The purchase to check
	 */
	private void validateAccess(Purchase purchase) {
		if (AuthService.isAuthenticatedAdmin())
			return;
		Long accountId = AuthService.getAuthenticatedAccount().getAccountId();
		boolean hasAccess = purchase.getTransactions().stream().anyMatch(
			t -> t.getAccount() != null && t.getAccount().getAccountId().equals(accountId)
		);
		if (!hasAccess) {
			throw new UnauthorizedException("Account not allowed to access this purchase");
		}
	}
}
