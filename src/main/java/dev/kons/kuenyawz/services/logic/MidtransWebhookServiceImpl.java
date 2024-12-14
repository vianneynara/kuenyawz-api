package dev.kons.kuenyawz.services.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.midtrans.MidtransNotification;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.entities.Transaction;
import dev.kons.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.kons.kuenyawz.repositories.PurchaseRepository;
import dev.kons.kuenyawz.repositories.TransactionRepository;
import dev.kons.kuenyawz.services.entity.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class MidtransWebhookServiceImpl implements MidtransWebhookService {

	private final ApplicationProperties properties;
	private final TransactionService transactionService;
	private final PurchaseRepository purchaseRepository;
	private final TransactionRepository transactionRepository;
	private final ObjectMapper mapper;
	private final WhatsappApiService whatsappApiService;
	private final ObjectMapper objectMapper;

	@Override
	@CacheEvict(value = "purchasesCache", allEntries = true)
	public void processNotification(MidtransNotification notification) {
		printNotification(notification); // TODO: remove in production

		if (notification.getOrderId().startsWith("payment_notif_test_" + properties.midtrans().getMerchantId())) {
			log.info("Test notification received!");
			return;
		}

		MidtransWebhookService.validateSignatureKey(notification, properties.midtrans().getServerKey());

		Transaction transaction = transactionService.getById(Long.valueOf(notification.getOrderId()));
		Purchase purchase = transaction.getPurchase();

		// Validate status ordinal to prevent status downgrade
		Transaction.TransactionStatus newStatus = Transaction.TransactionStatus.fromString(notification.getTransactionStatus());
		if (newStatus.ordinal() < transaction.getStatus().ordinal()) {
			log.warn("Transaction status can't be downgraded, current status: {}, requested status: {}", transaction.getStatus(), newStatus);
			throw new InvalidRequestBodyValue("Transaction status can't be downgraded");
		}

		// Validate merchant id
		if (notification.getMerchantId() == null || !notification.getMerchantId().equals(properties.midtrans().getMerchantId())) {
			log.warn("Merchant id is not valid, expected: {}, actual: {}", properties.midtrans().getMerchantId(), notification.getMerchantId());
			throw new InvalidRequestBodyValue("Merchant id is not valid");
		}

		final var actualAmount = transaction.getAmount();
		if (notification.getGrossAmount() == null || !notification.getGrossAmount().equals(actualAmount.toString())) {
			log.warn("Gross amount is not valid, expected: {}, actual: {}", transaction.getAmount(), notification.getGrossAmount());
			throw new InvalidRequestBodyValue("Gross amount is invalid");
		}

		// Update the status of the transaction and purchase
		if (notFraud(notification)
			&& (notification.getTransactionStatus().equalsIgnoreCase("capture")
			|| notification.getTransactionStatus().equalsIgnoreCase("settlement")
			|| notification.getTransactionStatus().equalsIgnoreCase("success"))
		) {
			if (purchase.getStatus().ordinal() >= Purchase.PurchaseStatus.CONFIRMING.ordinal()) {
				log.warn("Purchase [{}] status is not updated, current status: {}, requested status: {}", purchase.getPurchaseId(), purchase.getStatus(), Purchase.PurchaseStatus.CONFIRMING);
				return;
			}
			transaction.setStatus(newStatus);

			// Send WhatsApp notification to vendor
			if (purchase.getStatus() == Purchase.PurchaseStatus.PENDING) {
				String message = String.format("Ada pesanan baru dengan kode *%s*, segera cek aplikasi! %n%n%s",
					purchase.getPurchaseId(), properties.frontend().getBaseUrl()
				);
				whatsappApiService.send(properties.vendor().getPhone(), message, "62");
				purchase.setStatus(Purchase.PurchaseStatus.CONFIRMING);
			}
		} else {
			transaction.setStatus(newStatus);
			purchase.setStatus(Purchase.PurchaseStatus.CANCELLED);
		}

		transactionRepository.save(transaction);
		purchaseRepository.save(purchase);
	}

	@Override
	public String signatureCreator(String orderId, String statusCode, String grossAmount, String merchantServerKey) {
		AuthService.validateIsAdmin();

		final MidtransNotification notification = MidtransNotification.builder()
			.orderId(orderId)
			.statusCode(statusCode)
			.grossAmount(grossAmount)
			.build();
		merchantServerKey = merchantServerKey != null ? merchantServerKey : properties.midtrans().getServerKey();
		return MidtransWebhookService.generateSignatureKey(notification, merchantServerKey);
	}

	@Override
	public String generateNotification(Long purchaseId, String transactionStatus, String fraudStatus) {
		AuthService.validateIsAdmin();

		// Prepare properties
		Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new InvalidRequestBodyValue("Purchase not found"));

		// Get transaction of the purchase
		Transaction transaction = transactionRepository.findByPurchase_PurchaseId(purchase.getPurchaseId(), Pageable.unpaged())
			.getContent().getFirst();

		String statusCode = "200";
		BigDecimal totalGrossAmount = purchase.getTotalPrice()
			.add(purchase.getDeliveryFee())
			.add(BigDecimal.valueOf(properties.vendor().getPaymentFee()))
			.setScale(0, RoundingMode.UNNECESSARY);
		log.info("Total gross amount: {}", totalGrossAmount);
		transactionStatus = transactionStatus != null ? transactionStatus : "capture";
		fraudStatus = fraudStatus != null ? fraudStatus : "accept";

		final var simpleNotification = MidtransNotification.builder()
			.orderId(transaction.getTransactionId().toString())
			.statusCode(statusCode)
			.grossAmount(totalGrossAmount.toString())
			.build();

		String merchantServerKey = properties.midtrans().getServerKey();
		String signatureKey = MidtransWebhookService.generateSignatureKey(
			simpleNotification,
			merchantServerKey
		);

		MidtransNotification notification = MidtransNotification.builder()
			.transactionStatus(transactionStatus)
			.orderId(transaction.getTransactionId().toString())
			.statusCode(statusCode)
			.grossAmount(totalGrossAmount.toString())
			.signatureKey(signatureKey)
			.merchantId(properties.midtrans().getMerchantId())
			.fraudStatus(fraudStatus)
			.build();

		try {
			return objectMapper
				.writerWithDefaultPrettyPrinter()
				.writeValueAsString(notification);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/// Debugging method
	private static void simpleNotificationValues(MidtransNotification simpleNotification) {
		log.info("Notification | orderId: {}, statusCode: {}, grossAmount: {}",
			simpleNotification.getOrderId(), simpleNotification.getStatusCode(), simpleNotification.getGrossAmount());
	}

	private boolean notFraud(MidtransNotification notification) {
		if (notification.getFraudStatus() == null) {
			return true;
		}
		return notification.getFraudStatus().equalsIgnoreCase("accept");
	}

	private void printNotification(MidtransNotification notification) {
		try {
			log.info("Midtrans notification: {}",
				mapper.writerWithDefaultPrettyPrinter().writeValueAsString(notification));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
