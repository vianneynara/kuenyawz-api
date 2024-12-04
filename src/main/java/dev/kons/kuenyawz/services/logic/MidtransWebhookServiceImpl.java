package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.midtrans.MidtransNotification;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.entities.Transaction;
import dev.kons.kuenyawz.repositories.PurchaseRepository;
import dev.kons.kuenyawz.repositories.TransactionRepository;
import dev.kons.kuenyawz.services.entity.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MidtransWebhookServiceImpl implements MidtransWebhookService {

	private final ApplicationProperties properties;
	private final TransactionService transactionService;
	private final PurchaseRepository purchaseRepository;
	private final TransactionRepository transactionRepository;

	@Override
	public void processNotification(MidtransNotification notification) {
		MidtransWebhookService.validateSignatureKey(notification, properties.midtrans().getServerKey());

		Transaction transaction = transactionService.getById(Long.valueOf(notification.getOrderId()));
		Purchase purchase = transaction.getPurchase();

		// Validate status ordinal to prevent status downgrade
		Transaction.TransactionStatus newStatus = Transaction.TransactionStatus.fromString(notification.getTransactionStatus());
		if (newStatus.ordinal() < transaction.getStatus().ordinal()) {
			log.warn("Transaction status can't be downgraded, current status: {}, new status: {}", transaction.getStatus(), newStatus);
			return;
		}

		// Validate merchant id
		if (!notification.getMerchantId().equals(properties.midtrans().getMerchantId())) {
			log.warn("Merchant id is not valid, expected: {}, actual: {}", properties.midtrans().getMerchantId(), notification.getMerchantId());
			return;
		}

		// Validate transaction amount
		if (!notification.getGrossAmount().equals(transaction.getAmount().toString())) {
			log.warn("Gross amount is not valid, expected: {}, actual: {}", transaction.getAmount(), notification.getGrossAmount());
			return;
		}

		// Update the status of the transaction and purchase
		if (notFraud(notification)
			&& (notification.getTransactionStatus().equalsIgnoreCase("capture")
			|| notification.getTransactionStatus().equalsIgnoreCase("settlement"))
		) {
			if (purchase.getStatus().ordinal() >= Purchase.PurchaseStatus.CONFIRMING.ordinal()) {
				log.warn("Purchase status is not updated, current status: {}, new status: {}", purchase.getStatus(), Purchase.PurchaseStatus.CONFIRMING);
				return;
			}
			transaction.setStatus(newStatus);
			purchase.setStatus(Purchase.PurchaseStatus.CONFIRMING);
		} else {
			transaction.setStatus(newStatus);
			purchase.setStatus(Purchase.PurchaseStatus.CANCELLED);
		}

		transactionRepository.save(transaction);
		purchaseRepository.save(purchase);
	}

	private boolean notFraud(MidtransNotification notification) {
		if (notification.getFraudStatus() == null) {
			return true;
		}
		return notification.getFraudStatus().equalsIgnoreCase("accept");
	}
}
