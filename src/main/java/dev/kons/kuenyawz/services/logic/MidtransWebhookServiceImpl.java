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
import org.springframework.stereotype.Service;

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

	@Override
	@CacheEvict(value = "purchasesCache", allEntries = true)
	public void processNotification(MidtransNotification notification) {
		printNotification(notification); // TODO: remove in production
		MidtransWebhookService.validateSignatureKey(notification, properties.midtrans().getServerKey());

		Transaction transaction = transactionService.getById(Long.valueOf(notification.getOrderId()));
		Purchase purchase = transaction.getPurchase();

		// Validate status ordinal to prevent status downgrade
		Transaction.TransactionStatus newStatus = Transaction.TransactionStatus.fromString(notification.getTransactionStatus());
		if (newStatus.ordinal() < transaction.getStatus().ordinal()) {
			log.warn("Transaction status can't be downgraded, current status: {}, requested status: {}", transaction.getStatus(), newStatus);
//			return;
			throw new InvalidRequestBodyValue("Transaction status can't be downgraded");
		}

		// Validate merchant id
		if (!notification.getMerchantId().equals(properties.midtrans().getMerchantId())) {
			log.warn("Merchant id is not valid, expected: {}, actual: {}", properties.midtrans().getMerchantId(), notification.getMerchantId());
//			return;
			throw new InvalidRequestBodyValue("Merchant id is not valid");
		}

		// Validate transaction amount, round the decimal to 0 fractional digits
		final var actualAmount = transaction.getAmount().setScale(0, RoundingMode.UNNECESSARY);
		if (!notification.getGrossAmount().equals(actualAmount.toString())) {
			log.warn("Gross amount is not valid, expected: {}, actual: {}", transaction.getAmount(), notification.getGrossAmount());
//			return;
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

	private boolean notFraud(MidtransNotification notification) {
		if (notification.getFraudStatus() == null) {
			return true;
		}
		return notification.getFraudStatus().equalsIgnoreCase("accept");
	}

	private void printNotification(MidtransNotification notification) {
		try {
			log.info("Midtrans notification: {}", mapper.writeValueAsString(notification));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
